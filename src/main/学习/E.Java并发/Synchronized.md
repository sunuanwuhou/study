# Table of Contents

* [什么是锁](#什么是锁)
* [对象头](#对象头)
* [Synchronized](#synchronized)
  * [原子性](#原子性)
  * [可见性](#可见性)
  * [有序性](#有序性)
* [Lock Record](#lock-record)
* [Monitor](#monitor)
* [自旋锁 VS 适应性自旋锁](#自旋锁-vs-适应性自旋锁)
* [锁优化](#锁优化)
  * [无锁](#无锁)
  * [偏向锁](#偏向锁)
  * [轻量级锁](#轻量级锁)
  * [重量级锁](#重量级锁)
  * [锁消除](#锁消除)
  * [锁粗化](#锁粗化)
* [问题](#问题)
  * [1. mark word 非结构化后`分代年龄`去哪里了？](#1-mark-word-非结构化后分代年龄去哪里了)
  * [2.monitore和Lock Record 的关系](#2monitore和lock-record-的关系)




这边文章理解难度有点大 可以慢慢看

https://www.cnblogs.com/three-fighter/p/14396208.html
https://blog.csdn.net/MichaelSuns/article/details/108055895

https://tech.meituan.com/2018/11/15/java-lock.html

# 什么是锁

​		  在并发情况下，多个线程队会对一个资源进行争抢，可能会导致数据不一致的问题，为了解决这个问题，很多编程语言都引入了锁机制这个概念。来对资源进行锁定。java锁机制是怎么实现的？

我们需要先了解下Java虚拟机内存结构：

<div align=left>
	<img src=".images/1625146894362.png" width="">
</div>



多个线程对共享数据区进行竞争时，数据会发生难以预料的情况，因此需要锁机制对其进行限制。



+ 锁是一种抽象的概念，代码层面是怎么实现的？
  
    简单来说，在java中，每个object，也就是每个对象都拥有一把锁。这把锁存放在<font color=red>对象头中</font>,记录了当前对象被哪个线程所占用，
那么对象头是什么？



# 对象头

以Hotspot(是Jvm的一种实现)为例，JAVA对象 = 对象头 + 实例数据 + 对象填充(java对象必须是8byte的倍数)。

<div align=left>
	<img src=".images/image-20210701153124535.png" width="">
</div>


Hotspot对象头主要包括俩部分数据：Mark Word(标记字段)、klass pointer(类型指针)

+ Mark Word:默认存储对象的<font color=red>HashCode，分代年龄和锁标志位信息</font>。这些信息都是与对象自身定义无关的数据，所以Mark Word被设计成一个非固定的数据结构以便在极小的空间内存存储尽量多的数据。<font color=red>它会根据对象的状态复用自己的存储空间</font>，也就是说在运行期间Mark Word里存储的数据会随着锁标志位的变化而变化。

+ Klass Point:对象指向它的类元数据的指针，虚拟机通过这个指针来确定这个对象是哪个类的实例。

+ 数组长度：数组对象才有




<div align=left>
	<img src=".images/image-20210701152900595.png" width="">
</div>
这里我有2个疑问

1. 为什么hashCode只在`无锁`的时候才会有？
2. 为什么分代年龄在`轻量级锁`之后就没有了？



通过这张表我们可以看到Mark Word只有32bit(32位机器下），并且是非结构化的。在不同锁标志位下，不同的字段可以重用不同的比特位。因此可以节省空间。





# Synchronized

大家都知道Synchronized是可以同步线程。

+ 修饰方法上

  对于第一种情况，编译器会为其自动生成了一个 `ACC_SYNCHRONIZED` 关键字用来标识。

  在 JVM 进行方法调用时，当发现调用的方法被 `ACC_SYNCHRONIZED` 修饰，则会先尝试获得锁。

+ 修饰同步代码块

  对于第二种情况，编译时在代码块开始前生成对应的1个 `monitorenter` 指令，代表同步块进入。2个 `monitorexit` 指令，代表同步块退出。

  这两种方法底层都需要一个 reference 类型的参数，指明要锁定和解锁的对象。

  如果 `synchronized` 明确指定了对象参数，那就是该对象。

  如果没有明确指定,那就根据修饰的方法是实例方法还是类方法，取对应的对象实例或类对象（Java 中类也是一种特殊的对象）作为锁对象。



`synchronized` 关键字加到 `static` 静态方法和 `synchronized(class)` 代码块上都是是给 Class 类上锁。

`synchronized` 关键字加到实例方法上是给对象实例上锁。

Synchronized被编译后会生成`monitor enter`和`monitor exit`，依赖这2个字节码指令来进行线程同步。



## 原子性
加锁肯定是原子性
## 可见性

JMM对synchronized关键字有两条规定：

- 线程解锁前，必须将共享变量的值刷新到主内存当中
- 线程加锁时，将清空工作内存中共享变量的值，在使用共享变量时就需要从主内存中重新获取最新的值
## 有序性

有效解决重排序问题，即 “一个unlock操作先行发生(happen-before)于后面对同一个锁的lock操作”；

# Lock Record

在线程进入同步代码块的时候，如果此同步对象没有被锁定，即它的锁标志位是01，则虚拟机首先在当前线程的栈中创建我们称之为“锁记录（Lock Record）”的空间，用于存储锁对象的Mark Word的拷贝，官方把这个拷贝称为Displaced Mark Word。整个Mark Word及其拷贝至关重要。

**Lock Record是线程私有的数据结构**，每一个线程都有一个可用Lock Record列表，同时还有一个全局的可用列表。每一个被锁住的对象Mark Word都会和一个Lock Record关联（对象头的MarkWord中的Lock Word指向Lock Record的起始地址），同时Lock Record中有一个Owner字段存放拥有该锁的线程的唯一标识（或者`object mark word`），表示该锁被这个线程占用。如下图所示为Lock Record的内部结构：



<div align=left>
	<img src=".images/1625194877532.png" width="">
</div>



# Monitor

Monitor常常被翻译成监视器或者管程。可以理解为一个同步工具或一种同步机制，通常被描述为一个对象。每一个Java对象就有一把看不见的锁，称为内部锁或者Monitor锁。

而Monitor机制则是指任何一个对象都会有一个Monitor与之关联，当且一个Monitor被持有后，它将处于锁定状态。在字节码方面，使用synchronized的同步代码块的字节码前后会被插入monitorenter和monitorexit指令，通过monitorenter来获得Monitor对象，通过monitorexit来释放Monitor对象。而使用synchronized修饰的关键字则会在该方法的字节码的标志位中增加ACC_SYNCRHONIZED标志。JVM每次调用方法时会检查该标志位是否存在，如果存在则表明需要获得Monitor对象，才能进入该方法。



```java
public synchronized void method2 () {
    System.out.println("Hello");
}

// method2的字节码
 public synchronized void method2();
    descriptor: ()V
    flags: ACC_PUBLIC, ACC_SYNCHRONIZED		// 方法标志位中有ACC_SYNCHRONIZED标志
    Code:
      stack=2, locals=1, args_size=1
         0: getstatic     #2                  // Field java/lang/System.out:Ljava/io/PrintStream;
         3: ldc           #3                  // String Hello
         5: invokevirtual #4                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
         8: return
```



在Hotspot虚拟机中，Monitor机制是通过ObjectMonitor来实现的，ObjectMonitor中维护了一个EntryList即锁池和WaitSet即等待池。ObjectMonitor锁的竞争过程：

1. 所有到达的线程都会被放入到EntryList中去竞争锁
2. ObjectMonitor会将Owner标记为竞争成功的线程，并且将count+1（可重入：只是重新进入，则进入monitor的进入数加1）
3. 如果线程调用了Object.wait()方法，将会释放Monitor并放入到WaitSet等待池中，等待其他线程唤醒
4. 当其他线程调用了Object.notify()或者Object.notifyAll()方法时，处于WaitSet中的进程将会重新尝试获得Monitor对象
5. 当线程执执行完同步代码块后，便会释放Monitor对象。

<div align=left>
	<img src=".images/1625147798807.png" width="">
</div>


这样会存在性能问题，`monitor`是靠依赖操作系统的`mutex  lock`来实现的。<font color=red>每当挂起或者唤醒一个线程，都要操作操作系统内核，这种操作是比较重量级的，在一些情况下切换本身将会超过池线程执行任务的时间。</font>内存模型是$$1:1$$


```java
这里说一下自己的理解。因为自己是先看了AQS在回头看Synchronized，
monitor enter  lock.lock()
记录当前线程     setExclusiveOwnerThread
count+1         state
moiter exit     lock.unlock()

```





从java6开始，`Synchronized`引入了优化，引入了偏向锁、轻量级锁。所以目前锁是有4中状态。



# 自旋锁 VS 适应性自旋锁

自旋就是CPU空转->适应性自旋：自旋的时间不在固定，而是由前一次在同一个锁上的自旋时间及锁的拥有者的状态来决定。如果在同一个锁对象上，自旋等待刚刚成功获得过锁，并且持有锁的线程正在运行中，那么虚拟机就会认为这次自旋也是很有可能再次成功，进而它将允许自旋等待持续相对更长的时间。如果对于某个锁，自旋很少成功获得过，那在以后尝试获取这个锁时将可能省略掉自旋过程，直接阻塞线程，避免浪费处理器资源。

# 锁优化

## 无锁

无锁是没有对资源进行加锁，所有线程都可以访问并修改同一个资源，但同时只能有一个线程修改成功。

无锁的缺点就是修改操作在循环类进行，利用CAS就是无锁的实现。

## 偏向锁

偏向锁是指一段同步代码一直被一个线程所访问，那么该线程会自动获取锁，降低获取锁的代价。

<font color=red>当一个线程访问同步代码块并获取锁时，会在`Mark Word`里存储偏向锁的`threadId`</font>
在线程进入和退出同步块时不再通过CAS操作来加锁和解锁，而是检测Mark Word里是否存储着指向当前线程的偏向锁。

引入偏向锁是为了在无多线程竞争的情况下尽量减少不必要的轻量级锁执行路径，因为轻量级锁的获取及释放依赖多次CAS原子指令，而偏向锁只需要在置换ThreadID的时候依赖一次CAS原子指令即可。

偏向锁只有遇到其他线程尝试竞争偏向锁时，持有偏向锁的线程才会释放锁，线程不会主动释放锁。需要等待全局安全点(这个时间点没有字节码执行)，会首先暂停拥有偏向锁的线程，判断对象是否处于被锁定状态。

<font color=red>偏向锁撤销后恢复到无锁(标志位`01`)或轻量级锁`00`的状态</font>

## 轻量级锁



是指当锁是偏向锁的时候，被另外的线程所访问，<font color=red>偏向锁就会升级为轻量级锁，其他线程会通过自旋的形式尝试获取锁，不会阻塞，从而提高性能。</font>



若当前只有一个等待线程，则该线程通过自旋进行等待。但是当自旋超过一定的次数，或自旋等待线程数超过一个，轻量级锁升级为重量级锁。

## 重量级锁

自旋锁重试之后如果抢锁依然失败，同步锁会升级至重量级锁，锁标志位改为10。在这个状态下，未抢到锁的线程都会被阻塞。


## 锁消除

在JIT编译时，编译器会对运行的上下文进行扫描，对于某些没必要的加锁操作，如：对局部变量的加锁，编译器会进行优化，去除加锁的操作，避免没必要的加锁产生额外的开销。

## 锁粗化

通常情况下，使用synchronized关键字进行加锁时，加锁的粒度应当尽可能的小，避免过粗的粒度而产生额外的加锁开销。但在运行的时候，如果JVM检测到同一个对象被频繁的进行加锁操作甚至是加锁操作出现在循环体当中，那么JVM就会尝试扩大加锁的范围，避免频繁的加锁造成不必要的开销。


# 问题

## 1. mark word 非结构化后`分代年龄`去哪里了？

   



## 2.monitore和Lock Record 的关系


<div align=left>
	<img src=".images/微信图片_20210714090703.png" width="">
</div>




<div align=left>
	<img src=".images/1625190707823.png" width="">
</div>
