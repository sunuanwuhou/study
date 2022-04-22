# Table of Contents

* [学习前提](#学习前提)
* [什么是AQS](#什么是aqs)
* [为什么要使用AQS](#为什么要使用aqs)
* [AQS如何工作](#aqs如何工作)
* [CLH队列](#clh队列)
* [恢复与挂起](#恢复与挂起)
* [acquire()](#acquire)
* [release()](#release)
* [cancelAcquire()](#cancelacquire)
* [acquireInterruptibly()和acquireSharedInterruptibly()](#acquireinterruptibly和acquiresharedinterruptibly)
* [tryAcquireNanos()](#tryacquirenanos)
* [ConditionObject](#conditionobject)
* [问题解答](#问题解答)
* [回答关键](#回答关键)




终于还是对AQS下手了，学习Java并发这是必须要学习的知识。看了好多视频以及博客，以下是想以自己的思路去整理AQS的学习思路。

# 学习前提
+ 了解Java中的锁：公平锁和非公平锁、共享锁和独占锁等（传送门：）
+  知道CAS原理
+ 知道volatile原理
+ 了解链表数据结构
+ 对多线程有一定的了解


# 什么是AQS
并发使计算机得以充分利用计算能力，有效率地完成各类程序任务。当深入地学习Java中的并发，不可避免地将学习到锁 —— 使并发的资源能被正确访问的手段。锁的学习也将分为两部分，一部分是如何加解锁，另一部分是把锁分配给谁。

AQS(AbstractQueuedSynchronizer)也叫“抽象队列同步器”，它提供了“把锁分配给谁"这一问题的一种解决方案，使得锁的开发人员可以将精力放在“如何加解锁上”，避免陷于把锁进行分配而带来的种种细节陷阱之中。

例如JUC中，如CountDownLatch、Semaphore、ReentrantLock、ReentrantReadWriteLock等并发工具，均是借助AQS完成他们的所需要的锁分配问题。

# 为什么要使用AQS
Java 已经在语言层次提供 synchronized 锁，为什么要在 SDK 层次提供 AQS 锁？如果是是在1.6之前synchronized 性能不如 AQS，但是1.6之后synchronized已经做了锁升级，那为什么还要继续使用AQS,因为AQS提供了以下synchronized不能实现的方案。
+ 能够响应中断
+ 能够设置支持超时时间
+ 非阻塞式获取锁

# AQS如何工作

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210514095412470.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FpdW1lbmdfMTMxNA==,size_16,color_FFFFFF,t_70)
维护了一个volatile int state（代表共享资源）和一个FIFO线程等待队列（多线程争用资源被阻塞时会进入此队列）。通过CAS刷新变量

# CLH队列
CLH队列得名于Craig、Landin 和 Hagersten的名字缩写，他们提出实现了以自旋锁方式在并发中构建一个FIFO(先入先出)队列。在AQS中，也维护着这样一个同步队列，来记录各个线程对锁的申请状态。

每一记录单元，以AQS的内部类Node作为体现

```java
static final class Node {
    // 在同步队列中等待的线程等待超时或被中断，需要从同步队列中取消该Node的结点，其结点的waitStatus为CANCELLED，即结束状态，进入该状态后的结点将不会再变化。
    static final int CANCELLED =  1;
    // 表示线程正在申请锁，等待被分配,
    static final int SIGNAL    = -1;
    // 表示线程在等待某些条件达成，再进入下一阶段  与Condition相关
    static final int CONDITION = -2;  
    // 表示把对当前节点进行的操作，继续往队列传播下去 与共享锁相关
    static final int PROPAGATE = -3;
    // 表示当前线程的状态
    volatile int waitStatus;
    // 指向前一个节点，也叫前驱节点
    volatile Node prev;
    // 指向后一个节点，也叫后继节点
    volatile Node next;
    // 节点代表的线程
    volatile Thread thread;
     // 指向下一个代表要等待某些条件达成时，才进行下阶段的线程的节点
    Node nextWaiter;
}
```





# 恢复与挂起

```java
//到底是公平锁还是非公平锁 是由当前调用人决定的 默认为非公平锁
ReentrantLock reentrantLock = new ReentrantLock()
```

+ 公平锁
```java
final void lock() {
//老老实实排队
   acquire(1);
}
```
+ 非公平锁

```java
final void lock() {
	//先插队竞争锁
   if (compareAndSetState(0, 1))
       setExclusiveOwnerThread(Thread.currentThread());
   else
   //失败就是排队
       acquire(1);
}
```

<div align=left>
	<img src="https://img-blog.csdnimg.cn/img_convert/4fadaf72d1c2e2fa2fdf9d665c35c876.png" width="">
</div>



# acquire()
acquire (int arg)方法为AQS提供的模板方法

```java
public final void acquire(int arg) {
        // 如果获取到锁，获取锁的成程序就执行下去 线程交替进行，是不会构建CLH队列的，只有并发竞争的时候，才会往下走
        // 如果获取不到锁，插入代表当前线程的Node节点放入队列中，并请求锁
        if (!tryAcquire(arg) &&
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            // 中断 这里为什么会被中断？            selfInterrupt();
    }
```
以独占锁请求锁的实现方法acquire()来看，tryAcquire()是子类要实现的控制的锁获取成功与否逻辑。

```java
        protected final boolean tryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (!hasQueuedPredecessors() &&
                    compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0)
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }

    public final boolean hasQueuedPredecessors() {
        // The correctness of this depends on head being initialized
        // before tail and on head.next being accurate if the current
        // thread is first in queue.
        Node t = tail; // Read fields in reverse initialization order
        Node h = head;
        Node s;
        return h != t &&
            ((s = h.next) == null || s.thread != Thread.currentThread());
    }
```

addWaiter()，将新的代表当前线程的独占锁Node加入到CLH队列中，然后请求锁。

```java
#AbstractQueuedSynchronizer.java
    private Node addWaiter(Node mode) {
        //构造新节点
        Node node = new Node(Thread.currentThread(), mode);
        Node pred = tail;
        if (pred != null) {
            //尾节点存在
            //新节点的前驱指针指向尾节点
            node.prev = pred;//-------------为什么要设置前驱指针?
            //CAS修改为尾节点指向新节点
            if (compareAndSetTail(pred, node)) {
                //成功后
                //将最后一个节点的后继指针指向新加入的节点
                //此时新节点正式加入到同步队列里了
                pred.next = node;
                return node;
            }
        }
        //前面步骤加入队列失败，则会走到这
        enq(node);
        //返回新加入的节点
        return node;
    }

    private Node enq(final Node node) {
        //死循环务必保证插入队列成功
        for (;;) {
            Node t = tail;
            if (t == null) { 
                //队列是空的，则先创建头节点
                if (compareAndSetHead(new Node()))
                    //尾节点指向头节点
                    tail = head;
            } else {
                //和addWaiter里一样的操作，加入新节点到队尾
                node.prev = t;
                if (compareAndSetTail(t, node)) {
                    t.next = node;
                    return t;
                }
            }
        }
    }
```




```java
final boolean acquireQueued(final Node node, int arg) {
        boolean failed = true;
        try {
            boolean interrupted = false;
            for (;;) {
                // 自旋
                // 读取前驱结点，因为前驱节点可能发生了改变，如取消等待操作
                final Node p = node.predecessor();
                if (p == head && tryAcquire(arg)) {
                    // 只有当前驱节点为head时，才有资格获取锁
                    // 设置head为当前节点
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    // 返回是否发生过中断
                    return interrupted;
                }
                // 自旋 更新当前节点状态，并检查线程是否发生过中断
                if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
                    interrupted = true;
            }
        } finally {
            if (failed)
            // 说明发生了意料之外的异常，将节点移除，避免影响到其他节点
                cancelAcquire(node);
        }
    }
```
acquireQueued()表达的逻辑为：

+ 只有当自己的前驱节点为head时，才有资格去获取锁，这表达了FIFO。
+ 获取锁成功后，会返回线程是否被中断过，结合acquire()看，如果线程被中断过，会让线程回到中断状态。
+ 以acquireQueued()看，请求锁是的过程是公平的，按照队列排列顺序申请锁。
+ 以acquire()看，请求锁的过程是不公平的，因为acquire()会先尝试获取锁再入队，意味着将在某一时刻，有线程完成插队。

那么，shouldParkAfterFailedAcquire()是把Node状态更新，parkAndCheckInterrupt则将线程挂起，恢复后返回线程是否被中断过。

```java
private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
        int ws = pred.waitStatus;
        if (ws == Node.SIGNAL)
            前驱节点状态为SIGNAL直接返回
            return true;
        if (ws > 0) {
            // 这里和cancelAcquire()类似，整合移除node之前被取消的节点 这里在cancelAcquire()处理指针会用到
            do {
                node.prev = pred = pred.prev;
            } while (pred.waitStatus > 0);
            pred.next = node;
        } else {
            // CAS设置前驱节点状态为SIGNAL
            compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
        }
        return false;
    }

    private final boolean parkAndCheckInterrupt() {
        // 挂起当前线程
        LockSupport.park(this);
        return Thread.interrupted();
    }
```


情况一：Node的前驱节点为head，那么直接拿到锁，调用acquire()的线程继续执行。

情况二：Node的前驱节点不为head，并且也是申请锁状态，那么在parkAndCheckInterrupt()中此线程将被挂机。等到线程从parkAndCheckInterrupt()中回复后，再次中acquireQueued()的自旋逻辑，此时可能发生情况一、情况二、情况三。

情况三：Node的前驱节点被取消了，那么通过shouldParkAfterFailedAcquire()整合CLH队列后，走到情况一。

总结下加锁过程


![在这里插入图片描述](https://img-blog.csdnimg.cn/20210512143401747.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FpdW1lbmdfMTMxNA==,size_16,color_FFFFFF,t_70)



![在这里插入图片描述](https://img-blog.csdnimg.cn/img_convert/a90bdd61630ba12ef66184246619c46e.png#pic_center)


目前，没有申请到锁的Node在CLH队列中排队，其线程阻塞在parkAndCheckInterrupt()等待唤醒，然后继续尝试获取锁。

那么，在何时恢复线程？

#  release()
```java
 public void unlock() {
        sync.release(1);
    }
	
public final boolean release(int arg) {
 	//线程解锁 成功 设置ws为0 
     if (tryRelease(arg)) {
         Node h = head;
         if (h != null && h.waitStatus != 0)
         	//唤醒下一个排队的线程
             unparkSuccessor(h);
         return true;
     }
     return false;
 }
private void unparkSuccessor(Node node) {
     int ws = node.waitStatus;
     if (ws < 0)
         // CAS 修改节点状态为0
         compareAndSetWaitStatus(node, ws, 0);

     Node s = node.next;
     if (s == null || s.waitStatus > 0) {
         // 如果s的后继节点为空或者状态大于0
         s = null;
         for (Node t = tail; t != null && t != node; t = t.prev)
             // 从tail开始，找到最靠近head的状态不为0的节点--------为什么需要从尾部开始寻找？
             if (t.waitStatus <= 0)
                 s = t;
     }
     if (s != null)
         // 唤醒节点中记录的线程
         LockSupport.unpark(s.thread);
 }
```
线程唤醒发生在取消请求时cancelAcquire()，或释放锁时，对unparkSuccessor()的调用。

unparkSuccessor()将从CLH队里中唤醒最靠前的应该被唤醒的Node记录的线程，此之后，线程从parkAndCheckInterrupt()继续执行下去

#   cancelAcquire()
+ 当获取同步状态发生异常时，需要取消线程竞争同步状态的操作。
+ 当获取同步状态的超时时间到来之时，若此刻还无法成功获取同步状态，则调用该方法。

```java
 private void cancelAcquire(Node node) {
        if (node == null)
            return;

        node.thread = null;

        Node pred = node.prev;
        // 首先，找到当前节点前面未取消等待的节点，也就是有效节点
        while (pred.waitStatus > 0)
            node.prev = pred = pred.prev;

        // 方便操作
        Node predNext = pred.next;
        // 记录当前节点状态为取消，这样，如果发生并发，也能正确地处理掉
        node.waitStatus = Node.CANCELLED;

        //如果当前节点为tail，通过CAS将tail设置为找到的没被取消的pred节点
        if (node == tail && compareAndSetTail(node, pred)) {
            compareAndSetNext(pred, predNext, null);
        } else {
            int ws;
            //如果node既不是tail，又不是head的后继节点
        //则将node的前继节点的waitStatus置为SIGNAL
        //并使node的前继节点指向node的后继节点
            if (pred != head &&
                ((ws = pred.waitStatus) == Node.SIGNAL ||
                 (ws <= 0 && compareAndSetWaitStatus(pred, ws, Node.SIGNAL))) &&
                pred.thread != null) {
                // ① 
                Node next = node.next;
                if (next != null && next.waitStatus <= 0)
                    // 移除掉找到的CANCELLED节点，整理CLH队列
                    compareAndSetNext(pred, predNext, next);
            } else {
                // 表示当node.pred头节点，唤醒下一节点 
                unparkSuccessor(node);
            }
            node.next = node; // help GC
        }
    }
```
+ node是tail
  ![在这里插入图片描述](https://img-blog.csdnimg.cn/20210512142159679.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FpdW1lbmdfMTMxNA==,size_16,color_FFFFFF,t_70)
  具体代码

```java
if (node == tail && compareAndSetTail(node, pred)) {
       compareAndSetNext(pred, predNext, null);
   }
```

+  node不是tail 也不是head的后继节点
   ![在这里插入图片描述](https://img-blog.csdnimg.cn/202105121534388.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FpdW1lbmdfMTMxNA==,size_16,color_FFFFFF,t_70)

compareAndSetNext(pred, predNext, next);  pred.next=successor
那么successor的prev执行pred是什么时候做的呢？
是别的线程做的。当别的线程在调用cancelAcquire()或者shouldParkAfterFailedAcquire()时，会根据prev指针跳过被cancel掉的前继节点，同时，会调整其遍历过的prev指针

+  node是head的后继节点

![在这里插入图片描述](https://img-blog.csdnimg.cn/2021051214282532.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FpdW1lbmdfMTMxNA==,size_16,color_FFFFFF,t_70)
这里没有任务代码是将node出队，那么出队是什么时候完成的呢？

node被唤醒，successor的前继节点依然是node，而不是head。会调用shouldParkAfterFailedAcquire()

```java
 if (ws > 0) {
            /*
            * Predecessor was cancelled. Skip over predecessors and
            * indicate retry.
            */
           do {
               node.prev = pred = pred.prev;
           } while (pred.waitStatus > 0);
           pred.next = node;
       }
```




# acquireInterruptibly()和acquireSharedInterruptibly()

# tryAcquireNanos()

# ConditionObject



Condition的作用用一句话概括就是为了实现线程的等待（await）和唤醒（signal），多线程情况下为什么需要等待唤醒机制？原因是有些线程执行到某个阶段需要等待符合某个条件才可以继续执行，在之前学习操作系统的时候，有一个经典的场景就是在容量有限的缓冲区实现生产者消费者模型，如果缓冲区满了，这个时候生产者就不能再生产了，就要阻塞等待消费者消费，当缓冲区为空了，消费者就要阻塞等待生产者生产，这就是一个很典型的使用condition实现条件状态的场景。那本文就介绍一下AQS中的Condition实现原理，本文会涉及源码，介绍完原理之后，会和对象的wait/notify机制做一个对比。


+ 属性

```
/** First node of condition queue. */
private transient Node firstWaiter;
/** Last node of condition queue. */
private transient Node lastWaiter;
```

每个条件变量都维护了一个容器，ConditionObject中的容器就是单向链表队列，上面的属性就是队列的头结点firstWaiter和尾结点lastWaiter，需要注意，条件队列中的头结点不是虚拟头结点，而是包装了等待线程的节点！其类型和同步队列一样，也是使用AQS的内部类Node来构成，但与同步队列不同的是，条件队列是一个单向链表，所以他并没有使用Node类中的next属性来关联后继Node，而使用的nextWaiter

```
volatile Node prev;
volatile Node next;
Node nextWaiter;

```

这里我们需要注意，nextWaiter是没用volatile修饰的，为什么呢？**因为线程在调用await方法进入条件队列时，是已经拥有了锁的，此时是不存在竞争的情况，所以无需通过volatile和cas来保证线程安全。**而进入同步队列的都是抢锁失败的，所以肯定是没有锁的，故要考虑线程安全

最后需要注意一点的是，条件队列里面的Node只会存在CANCELLED和CONDITION的状态


　
+ 先看一个例子

```
public class test {
    static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        Condition condition = lock.newCondition();

        new Thread(() -> {
            lock.lock();
            try {
                System.out.println("线程一加锁成功");
                System.out.println("线程一执行await被挂起");
                condition.await();
                System.out.println("线程一被唤醒成功");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
                System.out.println("线程一释放锁成功");
            }
        }).start();

        new Thread(() -> {
            lock.lock();
            try {
                System.out.println("线程二加锁成功");
                System.out.println("线程二执行await被挂起");
                condition.await();
                System.out.println("线程二唤醒成功");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
                System.out.println("线程二释放锁成功");
            }
        }).start();


        new Thread(() -> {
            lock.lock();
            try {
                System.out.println("线程三加锁成功");
                condition.signal();
                // condition.signalAll();
                System.out.println("线程三唤醒成功");
            }finally {
                lock.unlock();
                System.out.println("线程三释放锁成功");
            }
        }).start();
    }
}

```

+ 例子示意图

![image](https://img-blog.csdnimg.cn/img_convert/e34e7a057ec0fd10acfb022fcebc1341.png)


+ 原理

当线程执行await，意味着当前线程一定是持有锁的，从首先会把当前线程放入到等待队列队尾，之后把当前线程的锁释放掉。

当执行signal的时候，会把位于等待队列中的首节点（首节点是等待时间最长的，因为是从队尾入队的）线程给唤醒，注意这里唤醒之后该线程并不能立即获取到锁，而是会把这个线程加入到阻塞队列队尾，如果阻塞队列中有很多的线程在等待，那被唤醒的线程还会继续挂起，然后慢慢等待去获取锁。


+ await 阻塞前

wait就是将节点入队并阻塞，等到其他线程唤醒(signal)或者自身中断后再重新去获取锁


```java
public final void await() throws InterruptedException {
    // 如果此线程被中断过，直接抛中断异常
    if (Thread.interrupted())
        throw new InterruptedException();
    // 将当前线程包装成节点放入条件队列
    Node node = addConditionWaiter();
    // 释放当前线程持有的额锁
    long savedState = fullyRelease(node);
    // 初始化中断模式参数
    int interruptMode = 0;
    // 检查节点s会否在同步队列中
    while (!isOnSyncQueue(node)) {
       // 不在同步队列中则阻塞此线程
        LockSupport.park(this);
        //后面代码省略不讲 后面会仔细讲到
    }
  
```
addConditionWaiter
```java
private Node addConditionWaiter() {
    Node t = lastWaiter;
    // If lastWaiter is cancelled, clean out.
    // 如果tail是取消 清楚队列中所有取消的节点
    if (t != null && t.waitStatus != Node.CONDITION) {
        unlinkCancelledWaiters();
        t = lastWaiter;
    }
    //包装线程为等待队列的新节点 状态为-2 
    //未初始化 第一个 若初始化 加入队列中 
    Node node = new Node(Thread.currentThread(), Node.CONDITION);
    if (t == null)
        firstWaiter = node;
    else
        t.nextWaiter = node;
    lastWaiter = node;
    return node;
}

```
unlinkCancelledWaiters

```java
private void unlinkCancelledWaiters() {
    Node t = firstWaiter;
    Node trail = null;
    while (t != null) {
        Node next = t.nextWaiter;
        if (t.waitStatus != Node.CONDITION) {
            t.nextWaiter = null;
            if (trail == null)
                firstWaiter = next;
           else
                trail.nextWaiter = next;
 
            if (next == null)
                lastWaiter = trail;
         }
           else
            trail = t;
        t = next;
    }
}


```

这个就是从头结点往后遍历，将Node状态为不为CONDITION的节点移除队列。，我们维护两个指针t和trail，t指向我们当前需要检查的节点，而trail指向当前节点的前驱节点，如果当前节点需要移除队列，则将trail的后继节点指向当前节点的后继节点


fullyRelease

```java
final long fullyRelease(Node node) {
    boolean failed = true;
    try {
        long savedState = getState();
        //释放同步队列中当前线程占有锁 唤醒下一个不为取消的
        if (release(savedState)) {
            failed = false;
            return savedState;
        } else {
            throw new IllegalMonitorStateException();
        }
    } finally {
    //如果发生异常 当前节点变为取消 在条件队列中  等待后面被移出去
        if (failed)
            node.waitStatus = Node.CANCELLED;
    }
}

```

isOnSyncQueue

看节点是否在同步队列中

```java

final boolean isOnSyncQueue(Node node) {
    //是取消 
    if (node.waitStatus == Node.CONDITION || node.prev == null)
        return false;
        //  prev和next都是同步队列中使用的，所以如果两个属性不为null，说明此节点是在同步队列中
    if (node.next != null) // If has successor, it must be on queue
        return true;        
    return findNodeFromTail(node);
}


//这个节点状态是0且prev不为null
private boolean findNodeFromTail(Node node) {
    Node t = tail;
    for (;;) {
        if (t == node)
            return true;
        if (t == null)
            return false;
        t = t.prev;
    }
}



```

+ await 唤醒后


```java
    while (!isOnSyncQueue(node)) {
        LockSupport.park(this);
        // 现在被唤醒后 执行下面代码
        if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
            break;
    }
    // 被唤醒后再去获取锁
    if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
        interruptMode = REINTERRUPT;
    // 当线程是被中断唤醒时，node和后继节点是没有断开的----为什么是现在移除
    是为了保证当前线程已获得锁 且从同步队列移除了 这个时候需要移除条件队列node的nextWaiter
    
    if (node.nextWaiter != null) // clean up if cancelled
        unlinkCancelledWaiters();
    // 根据异常标志位对异常进行处理
    if (interruptMode != 0)
        reportInterruptAfterWait(interruptMode);
    while (!isOnSyncQueue(node)) {
       // 不在同步队列中则阻塞此线程
        LockSupport.park(this);
        if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
            break;
    }
    // 被唤醒后再去获取锁
    if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
        interruptMode = REINTERRUPT;
    // 当线程是被中断唤醒时，node和后继节点是没有断开的
    if (node.nextWaiter != null) // clean up if cancelled
        unlinkCancelledWaiters();
    // 根据异常标志位对异常进行处理
    if (interruptMode != 0)
        reportInterruptAfterWait(interruptMode);
```

checkInterruptWhileWaiting(node

```java

/**
 * Checks for interrupt, returning THROW_IE if interrupted
 * before signalled, REINTERRUPT if after signalled, or
 * 0 if not interrupted.
 */
private int checkInterruptWhileWaiting(Node node) {
    return Thread.interrupted() ?
        (transferAfterCancelledWait(node) ? THROW_IE : REINTERRUPT) :
        0;
}
```
transferAfterCancelledWait

```java

   /**
     * Transfers node, if necessary, to sync queue after a cancelled wait.
     * Returns true if thread was cancelled before being signalled.
     *
     * @param node the node
     * @return true if cancelled before the node was signalled
     */
    final boolean transferAfterCancelledWait(Node node) {
        //如果条件中的CAS操作成功，说明此时的Node肯定是在条件队列中，则我们调动 enq 方法将此节点放入到同步队列中，然后返回true，但是这里需要特别注意，这个节点的nextWaiter还没置为null
        if (compareAndSetWaitStatus(node, Node.CONDITION, 0)) {
            enq(node);
            return true;
        }
        /*
         * If we lost out to a signal(), then we can't proceed
         * until it finishes its enq().  Cancelling during an
         * incomplete transfer is both rare and transient, so just
         * spin.
         */
        while (!isOnSyncQueue(node))
            Thread.yield();
        return false;
    }

```

acquireQueued 尝试入队


reportInterruptAfterWait

线程也拿到锁了，包装线程的节点也没在同步队列和条件队列中了，所以wait方法其实已经完成了，所以现在需要对中断进行善后处理了

```java

/**
 * Throws InterruptedException, reinterrupts current thread, or
 * does nothing, depending on mode.
 */
private void reportInterruptAfterWait(int interruptMode)
    throws InterruptedException {
    if (interruptMode == THROW_IE)
        throw new InterruptedException();
    else if (interruptMode == REINTERRUPT)
        selfInterrupt();
}

```




+ signal


signalAll是将条件队列中所有的Node转移到同步队列，signal则只转移条件队列中的第一个状态不为CANNCELLED的Node，直接看源码


doSignal

```java

private void doSignal(Node first) {
    do {
        // 将第一个节点移除条件队列 并将firstWaiter复制给第二个节点
        if ( (firstWaiter = first.nextWaiter) == null)
            lastWaiter = null;
        first.nextWaiter = null;
        
    // 如果入队不成功 检测队列中是否还有节点 重新入队
    } while (!transferForSignal(first) &&
             (first = firstWaiter) != null);
}



   private void doSignalAll(Node first) {
            lastWaiter = firstWaiter = null;
            do {
                Node next = first.nextWaiter;
                first.nextWaiter = null;
                transferForSignal(first);
                first = next;
            } while (first != null);
        }

//返回true 表示加入同步队列成功

 final boolean transferForSignal(Node node) {
        /*
         * If cannot change waitStatus, the node has been cancelled.
         */
        if (!compareAndSetWaitStatus(node, Node.CONDITION, 0))
            return false;

        /*
         * Splice onto queue and try to set waitStatus of predecessor to
         * indicate that thread is (probably) waiting. If cancelled or
         * attempt to set waitStatus fails, wake up to resync (in which
         * case the waitStatus can be transiently and harmlessly wrong).
         */
        Node p = enq(node);
        int ws = p.waitStatus;
        if (ws > 0 || !compareAndSetWaitStatus(p, ws, Node.SIGNAL))
            LockSupport.unpark(node.thread);
        return true;
    }



```





# 问题解答

+ 为什么需要selfInterrupt()

```java
private final boolean parkAndCheckInterrupt() {
        LockSupport.park(this);
        return Thread.interrupted();
    }
```
LockSupport.park(this)是将线程挂起，挂起后会调用Thread.interrupted()查询中断状态。但是Thread.interrupted()不仅会查询中断状态，也会重置中断状态，也就是说park之后，中断状态为true，调用后变为false。
从整个acquire()方法来看，是不能将中断状态设置为false，因此需要重新调用selfInterrupt(),
那这段代码是不是没有意义了？最开始直接返回一个Thread.isInterrupted()，不好吗？
主要是在于park方法！
场景：线程A被中断，unPark，获取同步状态，失败，准备继续挂起。park挂起时，会调用底层方法，如果当前线程中断状态为true，是直接返回不再挂起线程，会导致线程不断轮询，CPU飙升。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210512144942248.png)
+ 为什么要使用AQS使用的是双向队列，而不是单向。

这里我觉得可以从几个方面去说
1.双向链表便于查找，空间换时间。
2.双向链表的特性，可以循环查找

if a next field appears to be null, we can scan prev's from the tail to double-check

+ 为什么要设置前驱指针


现在同步队列里有两个结点，其中一个头结点，一个是Node1结点。若是先给pred.next 赋值，假设流程如下：

1、线程A先竞争锁，竞争失败，先将Node1的next指向NewNodeA。
2、此时另一个线程B也来竞争锁，失败，也将Node1的next指向NewNodeB。
3、将tail指针指向新的节点(可能是NewNodeA，也可能是NewNodeB)，若是NewNodeA，然后将NewNodeA的prev指向Node1。此时问题出现了：虽然NewNodeA的prev指向了Node1，但是Node1的next却是指向了NewNodeB。


多线程操作队列元素并没有做好并发保护，先给node.prev，并不是操作队列，将操作队列的步骤延迟到CAS成功之后，就能正确的修改队列。
**在prev.next执行之前，如果其他线程查询队列，会可能出先prev.next=null的情况，也就是prev.next不可靠的原因**

+ 为什么要从尾部开始索引

上面问题说过了pre.next并不可可靠。保险起见，从队尾开始索引。





# 回答关键

AQS（抽象同步队列）的核心回答要点就是：

- state 状态的维护。
- CLH队列
- ConditionObject通知
- 模板方法设计模式
- 独占与共享模式。
- 自定义同步器。
