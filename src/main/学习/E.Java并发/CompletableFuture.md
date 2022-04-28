# Table of Contents

* [背景](#背景)
* [简单回调例子](#简单回调例子)
* [创建异步任务](#创建异步任务)
* [多个任务组合关系](#多个任务组合关系)
  * [AND组合关系](#and组合关系)
  * [OR组合关系](#or组合关系)
  * [AllOf](#allof)
  * [AnyOf](#anyof)
  * [thenCompose](#thencompose)
* [CompletableFuture使用有哪些注意点](#completablefuture使用有哪些注意点)
  * [1. Future需要获取返回值，才能获取异常信息](#1-future需要获取返回值才能获取异常信息)
  * [2. CompletableFuture的get()方法是阻塞的。](#2-completablefuture的get方法是阻塞的)
  * [3. 默认线程池的注意点](#3-默认线程池的注意点)
  * [4.自定义线程池时，注意饱和策略](#4自定义线程池时注意饱和策略)
* [原理](#原理)
* [参考资料](#参考资料)


# 背景

使用`Future`获得异步执行结果时，要么调用阻塞方法`get()`，要么轮询看`isDone()`是否为`true`，这两种方法都不是很好，因为主线程也会被迫等待。

从Java 8开始引入了`CompletableFuture`，它针对`Future`做了改进，CompletableFuture提供了一种观察者模式类似的机制，可以让任务执行完成后通知监听的一方。

+ 可以传入回调对象，**当异步任务完成或者发生异常时，自动调用回调对象的回调方法。**

+ 以及最重要的：多个`CompletableFuture`可以【**组合**】执行。



# 简单回调例子

```java
public class Main {
    public static void main(String[] args) throws Exception {
        // 创建异步执行任务:
        CompletableFuture<Double> cf = CompletableFuture.supplyAsync(Main::fetchPrice);
        // 如果执行成功:
        cf.thenAccept((result) -> {
            System.out.println("price: " + result);
        });
        // 如果执行异常:
        cf.exceptionally((e) -> {
            e.printStackTrace();
            return null;
        });
        // 主线程不要立刻结束，否则CompletableFuture默认使用的线程池会立刻关闭:
        Thread.sleep(200);
    }

    static Double fetchPrice() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
        if (Math.random() < 0.3) {
            throw new RuntimeException("fetch price failed!");
        }
        return 5 + Math.random() * 20;
    }
}
```

CompletableFuture的supplyAsync方法，提供了异步执行的功能，线程池也不用单独创建了。实际上，它CompletableFuture使用了默认线程池是**ForkJoinPool.commonPool**。



# 创建异步任务

- supplyAsync执行CompletableFuture任务，支持返回值

  ```java
  //使用默认内置线程池ForkJoinPool.commonPool()，根据supplier构建执行任务
  public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier)
  //自定义线程，根据supplier构建执行任务
  public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor)
  ```

  

- runAsync执行CompletableFuture任务，**没有返回值**。

  ```java
  //使用默认内置线程池ForkJoinPool.commonPool()，根据supplier构建执行任务
  public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier)
  //自定义线程，根据supplier构建执行任务
  public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor)
  
  ```

+ 具体列子

    ```java
    public static void main(String[] args) {
            //可以自定义线程池
            ExecutorService executor = Executors.newCachedThreadPool();
            //runAsync的使用
            CompletableFuture<Void> runFuture = CompletableFuture.runAsync(() -> System.out.println("run,1"), executor);
            //supplyAsync的使用
            CompletableFuture<String> supplyFuture = CompletableFuture.supplyAsync(() -> {
                System.out.print("supply,2");
                return "2"; }, executor);
            //runAsync的future没有返回值，输出null
            System.out.println(runFuture.join());
            //supplyAsync的future，有返回值
            System.out.println(supplyFuture.join());
            executor.shutdown(); // 线程池需要关闭
        }
    ```





# 任务异步回调

+ thenRun/thenRunAsync： 
  + thenRun：**做完第一个任务后，再做第二个任务**。但是前后两个任务**没有参数传递，第二个任务也没有返回值**
  + 区别
    + 调用thenRun方法执行第二个任务时，则第二个任务和第一个任务是**共用同一个线程池**。
    + 调用thenRunAsync执行第二个任务时，则第一个任务使用的是你自己传入的线程池，**第二个任务使用的是ForkJoin线程池**

+ thenApply/thenApplyAsync： 第一个任务执行完成后，执行第二个回调方法任务，会将该第一个任务的执行结果，作为入参，传递到第二个任务的回调方法中，但是二个任务回调方法是**有返回值**的。

+ thenAccept/thenAcceptAsync ： 第一个任务执行完成后，执行第二个回调方法任务，会将该第一个任务的执行结果，作为入参，传递到第二个任务的回调方法中，但是二个任务回调方法是**没有返回值**的。

  

 ## exceptionally:

 ## 某个任务执行异常时，执行的回调方法;并且有**抛出异常作为参数**，传递到回调方法。

  ```java
       // 创建异步执行任务:
          CompletableFuture<Double> cf = CompletableFuture.supplyAsync(Main::fetchPrice);
          // 如果执行成功:
          cf.thenAccept((result) -> {
              System.out.println("price: " + result);
          });
          // 如果执行异常:
          cf.exceptionally((e) -> {
              e.pri（）
          });
  ```

+ whenComplete:某个任务执行完成后，执行的回调方法，**无返回值**；并且whenComplete方法返回的CompletableFuture的**result是上个任务的结果**。

  ```java
  public static void main(String[] args) throws ExecutionException, InterruptedException {
  
          CompletableFuture<String> orgFuture = CompletableFuture.supplyAsync(
                  ()->{
                      System.out.println("当前线程名称：" + Thread.currentThread().getName());
                      try {
                          Thread.sleep(2000L);
                      } catch (InterruptedException e) {
                          e.printStackTrace();
                      }
                      return "捡田螺的小男孩";
                  }
          );
  
          CompletableFuture<String> rstFuture = orgFuture.whenComplete((a, throwable) -> {
              System.out.println("当前线程名称：" + Thread.currentThread().getName());
              System.out.println("上个任务执行完啦，还把" + a + "传过来");
              if ("捡田螺的小男孩".equals(a)) {
                  System.out.println("666");
              }
              System.out.println("233333");
          });
  
          System.out.pri
  ```

  

+ handle


上面这些api

# 多个任务组合关系

## AND组合关系

thenCombine / thenAcceptBoth / runAfterBoth都表示：**将两个CompletableFuture组合起来，只有这两个都正常执行完了，才会执行某个任务**。

- thenCombine：会将两个任务的执行结果作为方法入参，传递到指定方法中，且**有返回值**
- thenAcceptBoth: 会将两个任务的执行结果作为方法入参，传递到指定方法中，且**无返回值**
- runAfterBoth 不会把执行结果当做方法入参，且没有返回值。

```java
public class ThenCombineTest {

    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {

        CompletableFuture<String> first = CompletableFuture.completedFuture("第一个异步任务");
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CompletableFuture<String> future = CompletableFuture
                //第二个异步任务
                .supplyAsync(() -> "第二个异步任务", executor)
                // (w, s) -> System.out.println(s) 是第三个任务
                .thenCombineAsync(first, (s, w) -> {
                    System.out.println(w);
                    System.out.println(s);
                    return "两个异步任务的组合";
                }, executor);
        System.out.println(future.join());
        executor.shutdown();

    }
}
//输出
第一个异步任务
第二个异步任务
两个异步任务的组合

```



## OR组合关系

applyToEither / acceptEither / runAfterEither 都表示：**将两个CompletableFuture组合起来，只要其中一个执行完了,就会执行某个任务。**

- applyToEither：会将已经执行完成的任务，作为方法入参，传递到指定方法中，且有返回值
- acceptEither: 会将已经执行完成的任务，作为方法入参，传递到指定方法中，且无返回值
- runAfterEither： 不会把执行结果当做方法入参，且没有返回值。

```java
public class AcceptEitherTest {
    public static void main(String[] args) {
        //第一个异步任务，休眠2秒，保证它执行晚点
        CompletableFuture<String> first = CompletableFuture.supplyAsync(()->{
            try{

                Thread.sleep(2000L);
                System.out.println("执行完第一个异步任务");}
                catch (Exception e){
                    return "第一个任务异常";
                }
            return "第一个异步任务";
        });
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CompletableFuture<Void> future = CompletableFuture
                //第二个异步任务
                .supplyAsync(() -> {
                            System.out.println("执行完第二个任务");
                            return "第二个任务";}
                , executor)
                //第三个任务
                .acceptEitherAsync(first, System.out::println, executor);

        executor.shutdown();
    }
}
//输出
执行完第二个任务
第二个任务

```



## AllOf

所有任务都执行完成后，才执行 allOf返回的CompletableFuture。如果任意一个任务异常，allOf的CompletableFuture，执行get方法，会抛出异常

```
public class allOfFutureTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        CompletableFuture<Void> a = CompletableFuture.runAsync(()->{
            System.out.println("我执行完了");
        });
        CompletableFuture<Void> b = CompletableFuture.runAsync(() -> {
            System.out.println("我也执行完了");
        });
        CompletableFuture<Void> allOfFuture = CompletableFuture.allOf(a, b).whenComplete((m,k)->{
            System.out.println("finish");
        });
    }
}
//输出
我执行完了
我也执行完了
finish
```

## AnyOf

任意一个任务执行完，就执行anyOf返回的CompletableFuture。如果执行的任务异常，anyOf的CompletableFuture，执行get方法，会抛出异常

```java
public class AnyOfFutureTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        CompletableFuture<Void> a = CompletableFuture.runAsync(()->{
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("我执行完了");
        });
        CompletableFuture<Void> b = CompletableFuture.runAsync(() -> {
            System.out.println("我也执行完了");
        });
        CompletableFuture<Object> anyOfFuture = CompletableFuture.anyOf(a, b).whenComplete((m,k)->{
            System.out.println("finish");
//            return "捡田螺的小男孩";
        });
        anyOfFuture.join();
    }
}
//输出
我也执行完了
finish

```



## thenCompose

thenCompose方法会在某个任务执行完成后，将该任务的执行结果,作为方法入参,去执行指定的方法。该方法会返回一个新的CompletableFuture实例

- 如果该CompletableFuture实例的result不为null，则返回一个基于该result新的CompletableFuture实例；
- 如果该CompletableFuture实例为null，然后就执行这个新任务



# CompletableFuture使用有哪些注意点

## 1. Future需要获取返回值，才能获取异常信息

​	建议使用exceptionally方法

## 2. CompletableFuture的get()方法是阻塞的。

```java
//反例
 CompletableFuture.get();
//正例
CompletableFuture.get(5, TimeUnit.SECONDS);

```

## 3. 默认线程池的注意点

CompletableFuture代码中又使用了默认的线程池，处理的线程个数是电脑CPU核数-1。在**大量请求过来的时候，处理逻辑复杂的话，响应会很慢**。一般建议使用自定义线程池，优化线程池配置参数。

## 4.自定义线程池时，注意饱和策略

CompletableFuture的get()方法是阻塞的，我们一般建议使用`future.get(3, TimeUnit.SECONDS)`。并且一般建议使用自定义线程池。

但是如果线程池拒绝策略是`DiscardPolicy`或者`DiscardOldestPolicy`，当线程池饱和时，会直接丢弃任务，不会抛弃异常。因此建议，CompletableFuture线程池策略**最好使用AbortPolicy**，然后耗时的异步线程，做好**线程池隔离**哈。



# 原理

可以试着这么去理解

+ Callable，有结果的同步行为，比如做蛋糕，产生蛋糕
+ Runnable，无结果的同步行为，比如喝牛奶，仅仅就是喝
+ Future，异步封装Callable/Runnable，比如委托给师傅（其他线程）去做糕点
+ CompletableFuture，封装Future，使其拥有回调功能，比如让师傅主动告诉我蛋糕做好了


https://blog.csdn.net/CoderBruis/article/details/103181520



# 参考资料

[廖雪峰](https://www.liaoxuefeng.com/wiki/1252599548343744/1306581182447650)

https://juejin.cn/post/6970558076642394142
