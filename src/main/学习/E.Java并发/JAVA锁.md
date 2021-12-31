# Table of Contents

* [基础知识](#基础知识)
  * [乐观锁和悲观锁](#乐观锁和悲观锁)
  * [自旋锁 VS 适应性自旋锁](#自旋锁-vs-适应性自旋锁)
  * [无锁 VS 偏向锁 VS 轻量级锁 VS 重量级锁](#无锁-vs-偏向锁-vs-轻量级锁-vs-重量级锁)
  * [公平锁和非公平锁](#公平锁和非公平锁)
  * [可重入锁和递归锁](#可重入锁和递归锁)
  * [独享锁和共享锁](#独享锁和共享锁)


[toc]

文章来源

https://mp.weixin.qq.com/s/sA01gxC4EbgypCsQt5pVog

https://mp.weixin.qq.com/s/E2fOUHOabm10k_EVugX08g

https://www.bilibili.com/video/BV18b411M7xz?p=26

# 基础知识

![image](https://mmbiz.qpic.cn/mmbiz_png/hEx03cFgUsXibicYtRt824nicRjKGTibicl7aNvORaIktWZgicKekEn5YS5ULbJsgdUvfSHibrSEj3EnVMzHf53ykYnjA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

## 乐观锁和悲观锁

+ 乐观锁：认为自己在使用数据的时候，不会有别的线程来修改数据，所以不会添加锁。只有在更新数据的时候，去判断之前有没有别的数据进行了修改。如果没有更新，当前线程将自己修改的数据写入。如果有更新，根据不同的实现方式进行操作（自旋或者报错）。
+ 悲观锁：认为自己在使用数据的时候，一定有别的线程进行数据修改，因此在获取数据的时候，会提前进行加速。Synchroized和Lock的实现类都是悲观锁。
  从上面的概念我们会发现
+ 乐观锁适用于读比较多的场景。
+ 悲观锁适用于写比较多，用锁保证


CAS就是一种乐观锁的实现，具体可以参考我之前的笔记。

## 自旋锁 VS 适应性自旋锁

1. 自旋锁

**前提知识**

阻塞或者唤醒线程一个java线程需要操作系统切换CPU状态来完成，这样是比较耗时间的
。
为了让当前线程 ‘**稍等一下**’，需要将当前线程进行自旋。如果在自旋完成后前面锁定同步资源的线程已经释放了锁，那么当前线程就可以不必阻塞而是直接获取同步资源，从而避免切换线程的开销。这就是自旋锁。

![image](https://mmbiz.qpic.cn/mmbiz_png/hEx03cFgUsXibicYtRt824nicRjKGTibicl7atsAOXEVNheMmSrqJ9Wo1cmgxVv9kB26bjB2TDxz9kp1vrWel8rJKrw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

自旋锁本身是有缺点的，不能代替阻塞。如果锁的时间过长，那么自旋也会浪费时间，所以自旋等待的时间是由一定限度的。**如果超过了限定次数(默认是10次，可以使用-XX:PreBlockSpin来更改)，没有成功获得锁，也需要挂起锁**。

2. 适应性自旋锁

自适应意味着自旋的时间（次数）不再固定，而是由前一次在同一个锁上的自旋时间及锁的拥有者的状态来决定。如果在同一个锁对象上，自旋等待刚刚成功获得过锁，并且持有锁的线程正在运行中，那么虚拟机就会认为这次自旋也是很有可能再次成功，进而它将允许自旋等待持续相对更长的时间。如果对于某个锁，自旋很少成功获得过，那在以后尝试获取这个锁时将可能省略掉自旋过程，直接阻塞线程，避免浪费处理器资源。

在自旋锁中 另有三种常见的锁形式:TicketLock、CLHlock和MCSlock

## 无锁 VS 偏向锁 VS 轻量级锁 VS 重量级锁

这四种锁是指锁的状态，专门针对synchronized的。

无锁

无锁没有对资源进行锁定，所有的线程都能访问并修改同一个资源，但同时只有一个线程能修改成功。

无锁的特点就是修改操作在循环内进行，线程会不断的尝试修改共享资源。如果没有冲突就修改成功并退出，否则就会继续循环尝试。如果有多个线程修改同一个值，必定会有一个线程能修改成功，而其他修改失败的线程会不断重试直到修改成功。上面我们介绍的CAS原理及应用即是无锁的实现。无锁无法全面代替有锁，但无锁在某些场合下的性能是非常高的。

偏向锁

偏向锁是指一段同步代码一直被一个线程所访问，那么该线程会自动获取锁，降低获取锁的代价。

在大多数情况下，锁总是由同一线程多次获得，不存在多线程竞争，所以出现了偏向锁。其目标就是在只有一个线程执行同步代码块时能够提高性能。

当一个线程访问同步代码块并获取锁时，会在Mark Word里存储锁偏向的线程ID。在线程进入和退出同步块时不再通过CAS操作来加锁和解锁，而是检测Mark Word里是否存储着指向当前线程的偏向锁。引入偏向锁是为了在无多线程竞争的情况下尽量减少不必要的轻量级锁执行路径，因为轻量级锁的获取及释放依赖多次CAS原子指令，而偏向锁只需要在置换ThreadID的时候依赖一次CAS原子指令即可。

偏向锁只有遇到其他线程尝试竞争偏向锁时，持有偏向锁的线程才会释放锁，线程不会主动释放偏向锁。偏向锁的撤销，需要等待全局安全点（在这个时间点上没有字节码正在执行），它会首先暂停拥有偏向锁的线程，判断锁对象是否处于被锁定状态。撤销偏向锁后恢复到无锁（标志位为“01”）或轻量级锁（标志位为“00”）的状态。

偏向锁在JDK 6及以后的JVM里是默认启用的。可以通过JVM参数关闭偏向锁：-XX:-UseBiasedLocking=false，关闭之后程序默认会进入轻量级锁状态。

轻量级锁

是指当锁是偏向锁的时候，被另外的线程所访问，偏向锁就会升级为轻量级锁，其他线程会通过自旋的形式尝试获取锁，不会阻塞，从而提高性能。

在代码进入同步块的时候，如果同步对象锁状态为无锁状态（锁标志位为“01”状态，是否为偏向锁为“0”），虚拟机首先将在当前线程的栈帧中建立一个名为锁记录（Lock Record）的空间，用于存储锁对象目前的Mark Word的拷贝，然后拷贝对象头中的Mark Word复制到锁记录中。

拷贝成功后，虚拟机将使用CAS操作尝试将对象的Mark Word更新为指向Lock Record的指针，并将Lock Record里的owner指针指向对象的Mark Word。

如果这个更新动作成功了，那么这个线程就拥有了该对象的锁，并且对象Mark Word的锁标志位设置为“00”，表示此对象处于轻量级锁定状态。

如果轻量级锁的更新操作失败了，虚拟机首先会检查对象的Mark Word是否指向当前线程的栈帧，如果是就说明当前线程已经拥有了这个对象的锁，那就可以直接进入同步块继续执行，否则说明多个线程竞争锁。

若当前只有一个等待线程，则该线程通过自旋进行等待。但是当自旋超过一定的次数，或者一个线程在持有锁，一个在自旋，又有第三个来访时，轻量级锁升级为重量级锁。

重量级锁

升级为重量级锁时，锁标志的状态值变为“10”，此时Mark Word中存储的是指向重量级锁的指针，此时等待锁的线程都会进入阻塞状态。

整体的锁状态升级流程如下：

无锁-偏向锁-轻量级-重量级锁


综上，偏向锁通过对比Mark Word解决加锁问题，避免执行CAS操作。而轻量级锁是通过用CAS操作和自旋来解决加锁问题，避免线程阻塞和唤醒而影响性能。重量级锁是将除了拥有锁的线程以外的线程都阻塞。

## 公平锁和非公平锁

```
 public ReentrantLock(boolean fair) {
        sync = fair ? new FairSync() : new NonfairSync();
    }

默认是非公平锁

synchronized也是非公平锁    

```
公平锁是先来后到，多个线程按照申请的顺序来获取锁
非公平锁是抢占式的，根据线程优先级，获取锁


ReentrantLock:是通过AQS来实现公平锁和非公平锁的，具体会在AQS中讲到
synchronized:是通过监视器模式来实现的

## 可重入锁和递归锁

可重入锁又名递归锁，是指在同一个线程在外层方法获取锁的时候，再进入该线程的内层方法会自动获取锁（前提锁是同一个对象或者class）,ReentrantLock和synchronized都是可重入锁，可重入锁一定程度上避免了死锁。

①.同步实例方法，锁是当前实例对象

②.同步类方法，锁是当前类对象

③.同步代码块，锁是括号里面的对象


![image](https://mmbiz.qpic.cn/mmbiz_jpg/hEx03cFgUsUWxF3IJSFicIicbpueYm7MoKLgLXJF1wsy176whx1VXDS0AnTAicWibmmH9vjq8cCzNTrGCicadSsAnGg/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)


Lock与unlock只要对应，不论有几把，都没问题

## 独享锁和共享锁

独享锁/独占锁/排他锁：该锁一次只能被一个线程所持有，synchronized和Lock的实现类就是这种锁。

共享锁：是指改锁可以被其他线程所持有。如果线程A对数据A加了共享锁，其他线程只能对A加共享锁，不能加排他锁。获得共享锁的线程，只能读数据，不能修改数据。


ReentrantReadWriteLock 独享锁和共享锁也是通过AQS来实现的

```java
public class ReentrantReadWriteLock
        implements ReadWriteLock, java.io.Serializable {
    private static final long serialVersionUID = -6992448646407690164L;
    /** Inner class providing readlock */
    private final ReentrantReadWriteLock.ReadLock readerLock;
    /** Inner class providing writelock */
    private final ReentrantReadWriteLock.WriteLock writerLock;
    /** Performs all synchronization mechanics */
    final Sync sync;
```
