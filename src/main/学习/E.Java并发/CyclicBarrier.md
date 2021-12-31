# Table of Contents

* [CyclicBarrier](#cyclicbarrier)
* [基本理念](#基本理念)
* [用法](#用法)
* [原理](#原理)
* [Barrier被破坏](#barrier被破坏)
* [CountDownLatch和CyclicBarrier](#countdownlatch和cyclicbarrier)
* [如何给别人讲](#如何给别人讲)



# CyclicBarrier



现实生活中我们经常会遇到这样的情景，**在进行某个活动前需要等待人全部都齐了才开始**。例如吃饭时要等全家人都上座了才动筷子，旅游时要等全部人都到齐了才出发，比赛时要等运动员都上场后才开始。

在JUC包中为我们提供了一个同步工具类能够很好的模拟这类场景，它就是CyclicBarrier类。利用CyclicBarrier类可以实现一组线程相互等待，当所有线程都到达某个屏障点后再进行后续的操作。下图演示了这一过程。

<iframe  height=500 width=150  src=".images/20181218144511688.gif">


# 基本理念

构造方法


CyclicBarrier(int parties)

创建一个新的 CyclicBarrier，它将在给定数量的参与者（线程）处于等待状态时启动，但它不会在启动 barrier 时执行预定义的操作。

CyclicBarrier(int parties, Runnable barrierAction)

创建一个新的 CyclicBarrier ，当给定数量的线程（线程）等待时，它将跳闸，当屏障跳闸时执行给定的屏障动作，由最后一个进入屏障的线程执行。


# 用法

```java

public static void main(String[] args) {

        CyclicBarrier cyclicBarrier = new CyclicBarrier(7,()->{
            System.out.println("最后执行的主线程");
        });

        for(int i=1;i<=7;i++ ){

            final  int temp=i;

            new Thread(()->{
                System.out.println("线程"+temp+"执行");
                try {
                    cyclicBarrier.await();
                    System.out.println("线程"+temp+"在主线程执行完毕后执行");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }).start();
        }

    }
    
线程1执行
线程4执行
线程3执行
线程2执行
线程7执行
线程6执行
线程5执行
最后执行的主线程
线程5在主线程执行完毕后执行
线程4在主线程执行完毕后执行
线程6在主线程执行完毕后执行
线程1在主线程执行完毕后执行
线程7在主线程执行完毕后执行
线程3在主线程执行完毕后执行
线程2在主线程执行完毕后执行

```

==复用调用reset()==



# 原理

 **CyclicBarrier 基于 Condition 来实现的**
在CyclicBarrier类的内部有一个计数器，每个线程在到达屏障点的时候都会调用await方法将自己阻塞，此时计数器会减1，当计数器减为0的时候所有因调用await方法而被阻塞的线程将被唤醒。

这就是实现一组线程相互等待的原理

# Barrier被破坏

https://juejin.im/entry/6844903487482904584


# CountDownLatch和CyclicBarrier

+ `CountDownLatch`是【线程组之间的等待】，即一个(或多个)线程等待N个线程完成某件事情之后再执行；而CyclicBarrier则是【线程组内的等待】，即每个线程相互等待，即N个线程都被拦截之后，然后依次执行。
+ `CountDownLatch`调用wait()通常是**主线程调用**，`CyclicBarrier`调用wait()是**任务线程调用**。
+ `CountDownLatch`是减计数方式，而`CyclicBarrier`是加计数方式。
+ `CountDownLatch`计数为0无法重置，而`CyclicBarrier`计数达到初始值，则可以重置。
+ `CountDownLatch`不可以复用，而`CyclicBarrier`可以复用。



# 如何给别人讲

+ `CountDownLatch`是基于AQS实现的，会将构造器的入参传入state，countDown()就是在做减法，await()就是让头节点一直在等待state为0的时候，释放所有线程。
+ ![image-20211021193630799](.images/image-20211021193630799.png)
