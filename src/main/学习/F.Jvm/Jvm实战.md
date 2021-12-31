# Table of Contents

* [背景](#背景)
* [机器参数](#机器参数)
* [第一次调优(新生代大小)](#第一次调优新生代大小)
* [第二次调优(内存泄漏)](#第二次调优内存泄漏)
* [第三次调优(MetaspaceSize)](#第三次调优metaspacesize)
* [参考资料](#参考资料)






# 背景




前一段时间，线上服务器的FullGC非常频繁，平均【一天40多次】，而且隔几天就有服务器自动重启。



# 机器参数

首先服务器的配置非常一般（2核4G），总共4台服务器集群。每台服务器的FullGC次数和时间基本差不多。其中JVM几个核心的启动参数为：

```xml
-Xms1000M -Xmx1800M -Xmn350M -Xss300K
-XX:+DisableExplicitGC -XX:SurvivorRatio=4 
-XX:+UseParNewGC -XX:+UseConcMarkSweepGC 
-XX:CMSInitiatingOccupancyFraction=70 
-XX:+CMSParallelRemarkEnabled -XX:LargePageSizeInBytes=128M 
-XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly 
-XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintHeapAtGC
```



> 其实光看参数就看出一些问题了
>
> 1. Xms和Xmx 应该设置一样大小
>
> 2. SurvivorRatio设置为4 那么比列就是4：1：1 对于新生代比较多的，是有点问题的。



# 第一次调优(新生代大小)

> 提升新生代大小，将初始化堆内存设置为最大内存



|       参数        | before | after |
| :---------------: | :----: | :---: |
|       -Xmn        |  350M  | 800M  |
| -XX:SurvivorRatio |   4    |   8   |
|       -Xms        | 1000M  | 1800M |



运行了5天后，观察GC结果，【YoungGC减少了一半以上的次数，时间减少了400s，但是FullGC的平均次数增加了41次。】YoungGC基本符合预期设想，但是这个FullGC就完全不行了。



# 第二次调优(内存泄漏)



在优化的过程中，我们的主管发现了有个对象T在内存中有一万多个实例，而且这些实例占据了将近20M的内存。于是根据这个bean对象的使用，在项目中找到了原因：匿名内部类引用导致的

伪代码如下：

```java
public void doSmthing(T t){
 redis.addListener(new Listener(){
  public void onTimeout(){
   if(t.success()){
    //执行操作
   }
  }
 });
}
```

由于listener在回调后不会进行释放，而且回调是个超时的操作，当某个事件超过了设定的时间（1分钟）后才会进行回调，这样就导致了T这个对象始终无法回收，所以内存中会存在这么多对象实例。通过上述的例子发现了存在内存泄漏后，首先对程序中的error log文件进行排查，首先先解决掉所有的error事件。然后再次发布后，GC操作还是基本不变，**虽然解决了一点内存泄漏问题，但是可以说明没有解决根本原因，服务器还是继续莫名的重启。**



是在线上不是很繁忙的时候继续进行dump内存，终于抓到了一个大对象

![image-20211220075126722](.images/image-20211220075126722.png)

原来是在某个条件下，会查询表中所有未处理的指定数据，但是由于查询的时候where条件中少加了模块这个条件，导致查询出的数量达40多万条，而且通过log查看当时的请求和数据，可以判断这个逻辑确实是已经执行了的，dump出的内存中只有4W多个对象，这个是因为dump时候刚好查询出了这么多个，剩下的还在传输中导致的。而且这也能非常好的解释了为什么服务器会自动重启的原因。

解决了这个问题后，线上服务器运行完全正常了，**使用未调优前的参数，运行了3天左右FullGC只有5次**



# 第三次调优(MetaspaceSize)

查阅相关博客后：https://blog.csdn.net/qq_41154882/article/details/102623394?utm_medium=distribute.pc_relevant.none-task-blog-2~default~baidujs_title~default-0.highlightwordscore&spm=1001.2101.3001.4242.1

得出如下信息：

>  　Metaspace由于使用不断扩容到-XX:MetaspaceSize参数指定的量，就会发生FGC；且之后每次Metaspace扩容都可能会发生FGC。


服务器默认的metaspace是21M，在GC log中看到了最大的时候metaspace占据了200M左右，于是进行如下调优




|       参数       | before | after |
| :--------------: | :----: | :---: |
| XX:MetaspaceSize |  21M   | 200M  |



要根据具体项目 具体设置`XX:MetaspaceSize`


# 参考资料 



https://mp.weixin.qq.com/s/F1aE0S5voRZFtEGnw1KsQg

