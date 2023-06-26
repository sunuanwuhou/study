# Table of Contents

* [概要](#概要)
* [基本使用](#基本使用)
* [Recover](#recover)
* [原理](#原理)
  * [切入点](#切入点)
  * [重试逻辑及策略实现](#重试逻辑及策略实现)
* [Guava-Retry](#guava-retry)
  * [参考资料](#参考资料)
* [参考资料](#参考资料-1)
* [总结](#总结)


# 概要

​		重试，其实我们其实很多时候都需要的，为了保证容错性，可用性，一致性等。一般用来应对外部系统的一些不可预料的返回、异常等，特别是网络延迟，中断等情况。
​	如果我们要做重试，要为特定的某个操作做重试功能，**则要硬编码，大概逻辑基本都是写个循环，根据返回或异常，计数失败次数，然后设定退出条件。**这样做，且不说每个操作都要写这种类似的代码，而且重试逻辑和业务逻辑混在一起，给维护和扩展带来了麻烦。从面向对象的角度来看，我们应该把重试的代码独立出来。



# 基本使用

+ pom文件引用

  ```java
  <dependency>
    <groupId>org.springframework.retry</groupId>
    <artifactId>spring-retry</artifactId>
   </dependency>
  ```

+ 开启使用

  ```java
  @EnableRetry
  public class Application {
  ```

+ 例子

  ```java
  @Service("service")
  public class RetryService {
  
      @Retryable(value = IllegalAccessException.class, maxAttempts = 5,
              backoff= @Backoff(value = 1500, maxDelay = 100000, multiplier = 1.2))
      public void service() throws IllegalAccessException {
          System.out.println("service method...");
          throw new IllegalAccessException("manual exception");
      }
  
      @Recover
      public void recover(IllegalAccessException e){
          System.out.println("service retry after Recover => " + e.getMessage());
      }
  
  }
  ```

  
  + 参数详情

    + `value`：抛出指定异常才会重试

    + `include`：和value一样，默认为空，当exclude也为空时，默认所有异常

    + `exclude`：指定不处理的异常

    + `maxAttempts`：最大重试次数，默认3次

    + `backoff`：重试等待策略，默认使用`@Backoff`，`@Backoff`的value默认为1000L，我们设置为2000L；`multiplier`（指定延迟倍数）默认为0，表示固定暂停1秒后进行重试，如果把`multiplier`设置为1.5，则第一次重试为2秒，第二次为3秒，第三次为4.5秒。

      > 可以理解为 失败后，间隔几秒进行重试。

  

  
  
  # Recover
  
  
  
  ```java
  @Recover
  public int recover(Exception e, int code){
     System.out.println("回调方法执行！！！！");
     //记日志到数据库 或者调用其余的方法
      return 400;
  }
  ```
  
  
  
  可以看到传参里面写的是 `Exception e`，这个是作为回调的接头暗号（重试次数用完了，还是失败，我们抛出这个`Exception e`通知触发这个回调方法）。对于`@Recover`注解的方法，需要特别注意的是：
  
  - 方法的返回值必须与`@Retryable`方法一致
  - 方法的第一个参数，必须是Throwable类型的，建议是与`@Retryable`配置的异常一致，其他的参数，需要哪个参数，写进去就可以了（`@Recover`方法中有的）
  - 该回调方法与重试方法写在同一个实现类里面



# 原理

原理部分我想分开两部分来讲，一是重试机制的切入点，即它是如何使得你的代码实现重试功能的；二是重试机制的详细，包括重试的逻辑以及重试策略和退避策略的实现。

## 切入点

 @EnableRetry

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EnableAspectJAutoProxy(proxyTargetClass = false)
@Import(RetryConfiguration.class)
@Documented
```

我们可以看到 `@EnableAspectJAutoProxy(proxyTargetClass = false)` 这个并不陌生，就是打开Spring AOP功能。重点看看`@Import(RetryConfiguration.class)` @Import相当于注册这个Bean



RetryOperationsInterceptor也是一个MethodInterceptor，我们来看看它的`invoke`方法。

```java
public Object invoke(final MethodInvocation invocation) throws Throwable {

		String name;
		if (StringUtils.hasText(label)) {
			name = label;
		} else {
			name = invocation.getMethod().toGenericString();
		}
		final String label = name;

		RetryCallback<Object, Throwable> retryCallback = new RetryCallback<Object, Throwable>() {

			public Object doWithRetry(RetryContext context) throws Exception {
				
				context.setAttribute(RetryContext.NAME, label);

				/*
				 * If we don't copy the invocation carefully it won't keep a reference to
				 * the other interceptors in the chain. We don't have a choice here but to
				 * specialise to ReflectiveMethodInvocation (but how often would another
				 * implementation come along?).
				 */
				if (invocation instanceof ProxyMethodInvocation) {
					try {
						return ((ProxyMethodInvocation) invocation).invocableClone().proceed();
					}
					catch (Exception e) {
						throw e;
					}
					catch (Error e) {
						throw e;
					}
					catch (Throwable e) {
						throw new IllegalStateException(e);
					}
				}
				else {
					throw new IllegalStateException(
							"MethodInvocation of the wrong type detected - this should not happen with Spring AOP, " +
									"so please raise an issue if you see this exception");
				}
			}

		};

		if (recoverer != null) {
			ItemRecovererCallback recoveryCallback = new ItemRecovererCallback(
					invocation.getArguments(), recoverer);
			return this.retryOperations.execute(retryCallback, recoveryCallback);
		}

		return this.retryOperations.execute(retryCallback);

	}

```



无论是`RetryOperationsInterceptor`还是`StatefulRetryOperationsInterceptor`，最终的拦截处理逻辑还是调用到RetryTemplate的execute方法，从名字也看出来，RetryTemplate作为一个模板类，里面包含了重试统一逻辑。不过，我看这个RetryTemplate并不是很“模板”，因为它没有很多可以扩展的地方。



## 动态配置重试次数

实现RetryConfiguration 设置Spel表达式，从而实现动态配置重试次数



## 重试逻辑及策略实现

下面我们继续看看重试的逻辑做了什么。RetryTemplate的doExecute方法。

```java
protected <T, E extends Throwable> T doExecute(RetryCallback<T, E> retryCallback,
   RecoveryCallback<T> recoveryCallback, RetryState state)
   throws E, ExhaustedRetryException {

  RetryPolicy retryPolicy = this.retryPolicy;
  BackOffPolicy backOffPolicy = this.backOffPolicy;

  //新建一个RetryContext来保存本轮重试的上下文
  RetryContext context = open(retryPolicy, state);
  if (this.logger.isTraceEnabled()) {
   this.logger.trace("RetryContext retrieved: " + context);
  }

  // Make sure the context is available globally for clients who need
  // it...
  RetrySynchronizationManager.register(context);

  Throwable lastException = null;

  boolean exhausted = false;
  try {

   //如果有注册RetryListener，则会调用它的open方法，给调用者一个通知。
   boolean running = doOpenInterceptors(retryCallback, context);

   if (!running) {
    throw new TerminatedRetryException(
      "Retry terminated abnormally by interceptor before first attempt");
   }

   // Get or Start the backoff context...
   BackOffContext backOffContext = null;
   Object resource = context.getAttribute("backOffContext");

   if (resource instanceof BackOffContext) {
    backOffContext = (BackOffContext) resource;
   }

   if (backOffContext == null) {
    backOffContext = backOffPolicy.start(context);
    if (backOffContext != null) {
     context.setAttribute("backOffContext", backOffContext);
    }
   }

   //判断能否重试，就是调用RetryPolicy的canRetry方法来判断。
   //这个循环会直到原方法不抛出异常，或不需要再重试
   while (canRetry(retryPolicy, context) && !context.isExhaustedOnly()) {

    try {
     if (this.logger.isDebugEnabled()) {
      this.logger.debug("Retry: count=" + context.getRetryCount());
     }
     //清除上次记录的异常
     lastException = null;
     //doWithRetry方法，一般来说就是原方法
     return retryCallback.doWithRetry(context);
    }
    catch (Throwable e) {
     //原方法抛出了异常
     lastException = e;

     try {
      //记录异常信息
      registerThrowable(retryPolicy, state, context, e);
     }
     catch (Exception ex) {
      throw new TerminatedRetryException("Could not register throwable",
        ex);
     }
     finally {
      //调用RetryListener的onError方法
      doOnErrorInterceptors(retryCallback, context, e);
     }
     //再次判断能否重试
     if (canRetry(retryPolicy, context) && !context.isExhaustedOnly()) {
      try {
       //如果可以重试则走退避策略
       backOffPolicy.backOff(backOffContext);
      }
      catch (BackOffInterruptedException ex) {
       lastException = e;
       // back off was prevented by another thread - fail the retry
       if (this.logger.isDebugEnabled()) {
        this.logger
          .debug("Abort retry because interrupted: count="
            + context.getRetryCount());
       }
       throw ex;
      }
     }

     if (this.logger.isDebugEnabled()) {
      this.logger.debug(
        "Checking for rethrow: count=" + context.getRetryCount());
     }

     if (shouldRethrow(retryPolicy, context, state)) {
      if (this.logger.isDebugEnabled()) {
       this.logger.debug("Rethrow in retry for policy: count="
         + context.getRetryCount());
      }
      throw RetryTemplate.<E>wrapIfNecessary(e);
     }

    }

    /*
     * A stateful attempt that can retry may rethrow the exception before now,
     * but if we get this far in a stateful retry there's a reason for it,
     * like a circuit breaker or a rollback classifier.
     */
    if (state != null && context.hasAttribute(GLOBAL_STATE)) {
     break;
    }
   }

   if (state == null && this.logger.isDebugEnabled()) {
    this.logger.debug(
      "Retry failed last attempt: count=" + context.getRetryCount());
   }

   exhausted = true;
   //重试结束后如果有兜底Recovery方法则执行，否则抛异常
   return handleRetryExhausted(recoveryCallback, context, state);

  }
  catch (Throwable e) {
   throw RetryTemplate.<E>wrapIfNecessary(e);
  }
  finally {
   //处理一些关闭逻辑
   close(retryPolicy, context, state, lastException == null || exhausted);
   //调用RetryListener的close方法
   doCloseInterceptors(retryCallback, context, lastException);
   RetrySynchronizationManager.clear();
  }

 }
```





#  Guava-Retry

Guava retryer工具与spring-retry类似，都是通过定义重试者角色来包装正常逻辑重试，但是Guava retryer有更优的策略定义，在支持重试次数和重试频度控制基础上，能够兼容支持多个异常或者自定义实体对象的重试源定义，让重试功能有更多的灵活性。

这里设定跟Spring-Retry不一样，我们可以根据返回的结果来判断是否重试，比如返回false我们就重试


## 参考资料

https://mp.weixin.qq.com/s/euRNjRbNUnpH_3nFXbar-Q



# 参考资料

[Spring-Retry重试实现原理 (qq.com)](https://mp.weixin.qq.com/s/eNcEgxFD-2mcm67DpQ6q3g)




# 总结

1. @Retryable可以实现方法的重试，**指定异常。**
2. @Recover回调方法与重试方法写**在同一个实现类里面**.
3. Spring Retry采用了一个更加轻量级的做法，就是**针对每一个需要重试的方法只new一个上下文Context对象，然后在重试时，把这个Context传到策略里，策略再根据这个Context做重试**，而且Spring Retry还对这个Context做了cache。 这样就相当于对重试的上下文做了优化。

