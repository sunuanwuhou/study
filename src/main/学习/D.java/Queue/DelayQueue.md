

`DelayQueue` 是JDK中提供的延时队列，内部封装优先级队列(`PriorityQueue`)，并且提供空阻塞功能。(`AQS`)



> 其实就是在`priorityQueue`的基础上加了一个锁。

```java
//注意 DelayQueu 的泛型是 Delayed
public class DelayQueue<E extends Delayed> extends AbstractQueue<E>
    implements BlockingQueue<E> {

    private final transient ReentrantLock lock = new ReentrantLock();
    private final PriorityQueue<E> q = new PriorityQueue<E>();
    private Thread leader = null;
    private final Condition available = lock.newCondition();

    public DelayQueue() {}
```


```java
public interface Delayed extends Comparable<Delayed> {

    /**
     * Returns the remaining delay associated with this object, in the
     * given time unit.
     *
     * @param unit the time unit
     * @return the remaining delay; zero or negative values indicate
     * that the delay has already elapsed
     */
    long getDelay(TimeUnit unit);
}
```



Leader-Follower模式的变种，用于最小化不必要的定时等待，当一个线程被选择为Leader时，它会等待延迟过去执行代码逻辑，而其他线程则需要无限期等待，在从take或poll返回之前，每当队列的头部被替换为具有更早到期时间的元素时，leader字段将通过重置为空而无效，Leader线程必须向其中一个Follower线程发出信号，被唤醒的 follwer 线程被设置为新的Leader 线程。


```java
Block until signalled, interrupted, or timed out.
condition 的 awaitNanos(long time)

```






# 应用

首先要使用DelayQueue，必须自定义一个Delayed对象：	

```java
public class DelayedUser implements Delayed {
    private String name;
    private long avaibleTime;

    public DelayedUser(String name, long delayTime){
        this.name=name;
        //avaibleTime = 当前时间+ delayTime
        this.avaibleTime=delayTime + System.currentTimeMillis();

    }

    @Override
    public long getDelay(TimeUnit unit) {
        //判断avaibleTime是否大于当前系统时间，并将结果转换成MILLISECONDS
        long diffTime= avaibleTime- System.currentTimeMillis();
        return unit.convert(diffTime,TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        //compareTo用在DelayedUser的排序
        return (int)(this.avaibleTime - ((DelayedUser) o).getAvaibleTime());
    }
}
```

