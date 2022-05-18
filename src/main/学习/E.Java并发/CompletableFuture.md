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
* [异常处理 exceptionally](#异常处理-exceptionally)
* [异常处理whenComplete](#异常处理whencomplete)
* [异常处理 handle](#异常处理-handle)
  * [1. handle的特点](#1-handle的特点)
  * [2. handle和thenApply的区别](#2-handle和thenapply的区别)
  * [3. handle和whenComplete的区别](#3-handle和whencomplete的区别)
  * [测试案例](#测试案例)
* [CompletableFuture使用有哪些注意点](#completablefuture使用有哪些注意点)
  * [1. Future需要获取返回值，才能获取异常信息](#1-future需要获取返回值才能获取异常信息)
  * [2. CompletableFuture的get()方法是阻塞的。](#2-completablefuture的get方法是阻塞的)
  * [3. 默认线程池的注意点](#3-默认线程池的注意点)
  * [4.自定义线程池时，注意饱和策略](#4自定义线程池时注意饱和策略)
* [原理](#原理)
* [工作总结](#工作总结)
* [并行模板抽取](#并行模板抽取)
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





# 异常处理 exceptionally



在写代码时，经常需要对异常进行处理，最常用的就是try catch。

在用CompletableFuture编写[多线程](https://so.csdn.net/so/search?q=多线程&spm=1001.2101.3001.7020)时，如果需要处理异常，可以用exceptionally，它的作用相当于catch。

exceptionally的特点：

- 当出现异常时，会触发回调方法exceptionally
- exceptionally中可指定默认返回结果，如果出现异常，则返回默认的返回结果



```java
public class Thread01_Exceptionally {

	public static void main(String[] args) throws InterruptedException, ExecutionException {

		CompletableFuture<Double> future = CompletableFuture.supplyAsync(() -> {

			if (Math.random() < 0.5) {
				throw new RuntimeException("抛出异常");
			}

			System.out.println("正常结束");
			return 1.1;
		}).thenApply(result -> {

			System.out.println("thenApply接收到的参数 = " + result);
			return result;
		}).exceptionally(new Function<Throwable, Double>() {

			@Override
			public Double apply(Throwable throwable) {
				System.out.println("异常：" + throwable.getMessage());
				return 0.0;
			}
		});

		System.out.println("最终返回的结果 = " + future.get());

	}
}

```





# 异常处理whenComplete



当CompletableFuture的任务不论是**正常完成**还是**出现异常**它都会调用**whenComplete**这[回调函数](https://so.csdn.net/so/search?q=回调函数&spm=1001.2101.3001.7020)。

- **正常完成**：whenComplete返回结果和上级任务一致，异常为null；
- **出现异常**：whenComplete返回结果为null，异常为上级任务的异常；

```java
  public static void sendMsg() {
        AtomicReference<Request> request = new AtomicReference<>(new Request());
        AtomicReference<Response> response = new AtomicReference<>(new Response());
        CompletableFuture.runAsync(() -> {
            request.set(new Request());
            //发送http信息
            response.set(send(request.get()));
        }).whenComplete(new BiConsumer<Void, Throwable>() {
            @Override
            public void accept(Void unused, Throwable throwable) {
                //记录日志
                Response response1 = response.get();

                if (null != throwable) {
                    //记录错误日志信息
                }
                insertLog(request.get(), response1);
            }
        });
    }
```



经过二元组处理后

```java
    public static void sendMsg() {
        CompletableFuture.supplyAsync(() -> {
            //发送http信息
            Request request = new Request();
            Response response = send(request);
            return new TwoTuple(request, response);
        }).whenComplete((twoTuple, throwable) -> {
            if (null != throwable) {
                //记录错误日志信息
            }
            insertLog((Request) twoTuple.getFirst(), (Response) twoTuple.getSecond());
        });
    }
```



再次优化后

```java
 public static void sendMsg() {
        CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("发送消息");
            //发送http信息
            Request request = new Request();
            Response response = send(request);
            return new TwoTuple<>(request, response);//注意这里 泛型
        }).whenComplete((twoTuple, throwable) -> {
            System.out.println("记录日志");
            if (null != throwable) {
                //记录错误日志信息
            }
            insertLog(twoTuple.first,twoTuple.second);//注意这里 泛型
        });
    }
```





# 异常处理 handle

## 1. handle的特点

在写代码时，我们常用try…catch…finally这样的代码块处理异常。

而handle就像finally，**不论正常返回还是出异常都会进入handle，类似whenComplete。**

handle()一般接收new BiFunction<T, Throwable, R>();

- T：就是任务传入的对象类型
- Throwable：就是任务传入的异常
- R：就是handle自己返回的对象类型

## 2. handle和thenApply的区别

handle和thenApply的区别：

- thenApply：任务出现异常就**不会**进入thenApply
- handle：任务出现异常**也会**进入handle，可对异常处理

## 3. handle和whenComplete的区别

handle和whenComplete的区别：

- handle对传入值进行**转换**，并产生自己的返回结果，T -> R
- whenComplete的返回值和上级任务传入的结果**一致**，不能对其转换



## 测试案例

- supplyAsync中线程返回的是部门**Dept**
- handle接收到传入的Dept，并User赋值deptId和deptName，然后返回**User**



```java
public class Thread04_Handle {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        DeptService deptService = new DeptService();
        UserService userService = new UserService();

        CompletableFuture<User> future = CompletableFuture
                .supplyAsync(() -> {
                            //int a = 1 / 0;//如果出现异常，那么thenApply则不执行
                            return deptService.getById(1);
                        }
                )
                .handle(new BiFunction<Dept, Throwable, User>() {
                            @Override
                            public User apply(Dept dept, Throwable throwable) {
                                if (throwable != null){
                                    System.out.println(throwable.getMessage());
                                    return null;
                                } else {
                                    User user = new User(1, "winter", 32, dept.getId(), dept.getName());
                                    return userService.save(user);
                                }
                            }
                        }
                );

        System.out.println("线程：" + Thread.currentThread().getName() +
                " 结果：" + future.get());
    }
}

```



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







# 工作总结

1. 场景：在做具体逻辑前，可能需要获取一些基础数据，这些基础数据如 b1,b2,b3,b4

   同步查询的话是  b1,b2,b3,b4

   我们可以这样，分为2组去并行获取数据，前提是2组基础数据互相不影响。

2. 场景：你需要处理10个航班，每个航班都需要处理一些其他信息，而且是比较耗时的。

   怎么处理呢？

   1. 首先10个航班是互不影响的，那么这10个航班是可以并行处理的。**使用自定义线程池1**

   2. 处理每个航班，处理一些信息也是可以并行的，**使用自定义线程池2**

   3. 然后使用**CompletableFuture.allOf().join()**等待每个航班处理完毕。

   4. 为什么不使用同一个线程池呢。

      对于外面的线程处理完成，是需要依赖里面的线程处理完毕的，如果使用同一个线程池的话，阻塞队列会发生线程互相等待！！


# 并行模板抽取

```java

public class CompletableFutureUtils {


    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    public void test() {
        runAsync(this::testMethod);
        CompletableFuture<UserDTO> userDTOCompletableFuture = supplyAsync(this::testMethod);
    }
    public void test1(Object object) {
        runAsync(()-> testMethod(object));
        CompletableFuture<UserDTO> userDTOCompletableFuture = supplyAsync(()-> testMethod(object));
    }
    public UserDTO testMethod() {
        return new UserDTO();
    }
    public UserDTO testMethod(Object object) {
        return new UserDTO();
    }

    //下面这2个方法是可以抽出来  封装不同的线程池进行管理
    public void runAsync(Runnable runnable) {
        CompletableFuture.runAsync(runnable, threadPoolExecutor);
    }
    public <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier) {
        return CompletableFuture.supplyAsync(supplier, threadPoolExecutor);
    }

}

```
      

# 参考资料

[廖雪峰](https://www.liaoxuefeng.com/wiki/1252599548343744/1306581182447650)

https://juejin.cn/post/6970558076642394142

https://blog.csdn.net/winterking3/article/details/116477522  异常处理

