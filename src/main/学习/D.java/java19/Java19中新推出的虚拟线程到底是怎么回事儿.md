# Table of Contents

* [前提知识](#前提知识)
* [虚拟线程](#虚拟线程)
* [虚拟线程和平台线程的区别](#虚拟线程和平台线程的区别)
* [如何使用](#如何使用)
* [性能测试](#性能测试)
* [总结](#总结)
* [参考资料](#参考资料)




# 前提知识

+ [协程](../../A.操作系统/协程.md)
+ [Java线程模型](../../E.Java并发/Java线程模型.md)









# 虚拟线程

**JDK 19引入的虚拟线程，是JDK 实现的轻量级线程，他可以避免上下文切换带来的的额外耗费。**



他的实现原理其实是JDK不再是每一个线程都一对一的对应一个操作系统的线程了，**而是会将多个虚拟线程映射到少量操作系统线程中**，通过有效的调度来避免那些上下文切换。





# 虚拟线程和平台线程的区别

首先，虚拟线程总是守护线程。setDaemon (false)方法不能将虚拟线程更改为非守护线程。**所以，需要注意的是，当所有启动的非守护进程线程都终止时，JVM将终止。这意味着JVM不会等待虚拟线程完成后才退出。**

其次，即使使用setPriority()方法，**虚拟线程始终具有normal的优先级**，且不能更改优先级。在虚拟线程上调用此方法没有效果。

还有就是，**虚拟线程是不支持stop()、suspend()或resume()等方法**。这些方法在虚拟线程上调用时会抛出UnsupportedOperationException异常。



# 如何使用

接下来介绍一下，在JDK 19中如何使用虚拟线程。

首先，通过Thread.startVirtualThread()可以运行一个虚拟线程：

```java
<br />Thread.startVirtualThread(() -> {
    System.out.println("虚拟线程执行中...");
});

```

其次，通过Thread.Builder也可以创建虚拟线程，Thread类提供了ofPlatform()来创建一个平台线程、ofVirtual()来创建虚拟现场。

```java
Thread.Builder platformBuilder = Thread.ofPlatform().name("平台线程");
Thread.Builder virtualBuilder = Thread.ofVirtual().name("虚拟线程");

Thread t1 = platformBuilder .start(() -> {...}); 
Thread t2 = virtualBuilder.start(() -> {...}); 

```

另外，线程池也支持了虚拟线程，可以通过Executors.newVirtualThreadPerTaskExecutor()来创建虚拟线程：

```java
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    IntStream.range(0, 10000).forEach(i -> {
        executor.submit(() -> {
            Thread.sleep(Duration.ofSeconds(1));
            return i;
        });
    });
}
```

但是，**其实并不建议虚拟线程和线程池一起使用**，因为Java线程池的设计是为了避免创建新的操作系统线程的开销，但是创建虚拟线程的开销并不大，所以其实没必要放到线程池中。



# 性能测试



# 总结

+ 主要是为了解决在读书操作系统中线程需要依赖内核线程的实现，导致有很多额外开销的问题。通过在Java语言层面引入虚拟线程，通过JVM进行调度管理，从而减少上下文切换的成本。
+ 发现虚拟线程的执行确实高效了很多。但是使用的时候也需要注意，虚拟线程是守护线程，所以有可能会没等他执行完虚拟机就会shutdown掉。













# 参考资料

https://juejin.cn/post/7155406687598280740
