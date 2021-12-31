# Table of Contents

* [定义](#定义)
* [如何使用](#如何使用)
* [原理](#原理)




# 定义

Semaphore是计数信号量。Semaphore管理一系列许可。每个acquire方法阻塞，直到有一个许可证可以获得然后拿走一个许可证；每个release方法增加一个许可，这可能会释放一个阻塞的acquire方法。然而，其实并没有实际的许可这个对象，Semaphore只是维持了一个可获得许可证的数量。

> 有点像‘令牌桶算法’,根据许可证控制请求的次数。
>
> 一边生成令牌，一边消费令牌。
>
> 不过要注意的是，semaphore使用完毕后，会归还令牌。



# 如何使用

```java
//创建具有给定的许可数和非公平的公平设置的 Semaphore。  
Semaphore(int permits)   

//创建具有给定的许可数和给定的公平设置的 Semaphore。  
Semaphore(int permits, boolean fair)   
```



# 原理

```java
Semaphore semaphore=new Semaphore(2);
```

1、当调用new Semaphore(2) 方法时，默认会创建一个非公平的锁的同步阻塞队列。

2、把初始许可数量赋值给同步队列的state状态，state的值就代表当前所剩余的许可数量。

```java
semaphore.acquire();
```

1、当前线程会尝试去同步队列获取一个许可，获取许可的过程也就是使用原子的操作去修改同步队列的state ,获取一个许可则修改为state=state-1。

2、 当计算出来的state<0，则代表许可数量不足，此时会创建一个Node节点加入阻塞队列，挂起当前线程。

3、当计算出来的state>=0，则代表获取许可成功。

```java
 semaphore.release();
```

当调用semaphore.release() 方法时

1、线程会尝试释放一个许可，释放许可的过程也就是把同步队列的state修改为state=state+1的过程

2、释放许可成功之后，同时会唤醒同步队列的所有阻塞节共享节点线程

3、被唤醒的节点会重新尝试去修改state=state-1 的操作，如果state>=0则获取许可成功，否则重新进入阻塞队列，挂起线程。
