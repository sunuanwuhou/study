# Table of Contents

* [基本知识](#基本知识)
* [用法](#用法)
* [总结](#总结)





# 基本知识

CountDownLatch是一个同步工具类，用来协调多个线程之间的同步，或者说起到线程之间的通信（而不是用作互斥的作用）。

CountDownLatch能够使一个线程在等待另外一些线程完成各自工作之后，再继续执行。使用一个计数器进行实现。计数器初始值为线程的数量。当每一个线程完成自己任务后，计数器的值就会减一。当计数器的值为0时，表示所有的线程都已经完成一些任务，然后在CountDownLatch上等待的线程就可以恢复执行接下来的任务。

比如：客户端一次请求5个统计数据，服务器需要全部统计完成后，才返回客户端，可以使用CountDownLatch 。

# 用法



```java

static CountDownLatch c = new CountDownLatch(2);

    public static void main(String[] args) throws InterruptedException {

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(1);
                c.countDown();
                System.out.println(2);
                c.countDown();
            }
        }).start();
        //主线程阻塞
        c.await();
        System.out.println("3");

    }

不加CountDownLatch,可能有很多种结果
加了后
1 2 3 


```








# 总结

CountDownLatch通过内部实现的AQS的子类来实现对state的维护，初始化时设置state值，await方法会根据state是否等于0来判断线程是否阻塞，

阻塞的线程会放在同步队列上，

而countDown方法会把state的值减一，当减到0的时候唤醒所有等待的线程。

实现多个线程等待一个线程执行，即初始化state为1多个线程调用await，一个线程调用countDown的作用相当于于Object的wait与notifyAll方法；而CountDownLatch可以实现一个线程等待多个线程执行完成，还可以多个CountDownLatch组合使用，比如上面的使用场景；

从源码我们也可以看到只能在初始化的时候设置state的值，而countDown都是对state减，所以CountDownLatch的缺点是只能用一次，当state变为0的时候就没有作用了。
