# Table of Contents

* [基础组件](#基础组件)
* [**使用监听器**](#使用监听器)
  * [注册事件](#注册事件)
  * [注册监听器](#注册监听器)
  * [发布事件](#发布事件)
* [异步使用监听器](#异步使用监听器)
* [源码分析](#源码分析)
  * [初始化派发器](#初始化派发器)
  * [初始化监听器](#初始化监听器)
    * [添加监听器](#添加监听器)
  * [发布事件](#发布事件-1)
    * [multicastEvent](#multicastevent)
    * [getApplicationListeners](#getapplicationlisteners)
    * [invokeListener](#invokelistener)
* [EventPublisher](#eventpublisher)
* [自定义ApplicationEventMulticaster](#自定义applicationeventmulticaster)


# 基础组件

1. 事件：`ApplicationEvent`，要自定义事件，则需要创建一个类继承 ApplicationEvent。
2. 事件发布者：`ApplicationEventPublisher` 和 `ApplicationEventMulticaster`，因为 ApplicationContext 实现了 ApplicationEventPublisher，所以事件发布可以直接使用 ApplicationContext。
3. 事件监听器：`ApplicationListener`，通过创建一个实现了 ApplicationListener 并注册为 Spring bean 的类来接收消息。

> 1. 一个事件是可以注册多个监听者的
> 2. 可以有多个事件





# **使用监听器**

简单来说主要分为以下几个部分：

1. 注册事件
2. 注册监听器
3. 发布事件

在接口调用发布事件时，监听器就会做出相应的操作。



## 注册事件

```java
public class MyApplicationEvent extends ApplicationEvent {

    private String message;

    public MyApplicationEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
```



## 注册监听器

```jvaa
@Component
public class MyApplicationListener implements ApplicationListener<MyApplicationEvent> {

    @Override
    public void onApplicationEvent(MyApplicationEvent event) {

        System.out.println("MyApplicationListener 收到消息: " + event.getMessage());

    }
```

注解方式

```java
@Component
public class MyAnnotationApplicationListener {

    @EventListener(classes = MyApplicationEvent.class)
    public void myApplicationEventListener(MyApplicationEvent event) {
        System.out.println("使用注解的方式, 收到事件: " + event.getMessage());
    }
}
```





## 发布事件

```java
       MyApplicationEvent myApplicationEvent = new MyApplicationEvent( applicationContext,"呼叫土豆,呼叫土豆!");
//注意这里需要 使用applicationContext发布
        applicationContext.publishEvent(myApplicationEvent);
```



可以看到注解的优先级还大于自定义的监听器

```java
使用注解的方式, 收到事件: 事件1发布
MyApplicationListener 收到消息: 事件1发布
MyApplicationListener2 收到消息: 事件2发布

```



#  异步使用监听器

Spring提供的事件机制，默认是同步的。如果想要使用异步事件监听，可以自己实现ApplicationEventMulticaster接口，并在Spring[容器](https://cloud.tencent.com/product/tke?from=10680)中注册id为applicationEventMulticaster的Bean ， 设置 executor 。


```java
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

	@Bean(name = "applicationEventMulticaster") // Step1: id必须叫 applicationEventMulticaster
	public ApplicationEventMulticaster multicaster(){
		SimpleApplicationEventMulticaster simpleApplicationEventMulticaster = new SimpleApplicationEventMulticaster();
		simpleApplicationEventMulticaster.setTaskExecutor(threadPoolExecutor);
		return simpleApplicationEventMulticaster ;
	}
```





# 源码分析 

https://zhuanlan.zhihu.com/p/344113168

##  初始化派发器

  `AbstractApplicationContext.initApplicationEventMulticaster` 

 ```java
   
   
       protected void initApplicationEventMulticaster() {
           ConfigurableListableBeanFactory beanFactory = this.getBeanFactory();
           if (beanFactory.containsLocalBean("applicationEventMulticaster")) {
               this.applicationEventMulticaster = (ApplicationEventMulticaster)beanFactory.getBean("applicationEventMulticaster", ApplicationEventMulticaster.class);
               if (this.logger.isTraceEnabled()) {
                   this.logger.trace("Using ApplicationEventMulticaster [" + this.applicationEventMulticaster + "]");
               }
           } else {
               this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
               beanFactory.registerSingleton("applicationEventMulticaster", this.applicationEventMulticaster);
               if (this.logger.isTraceEnabled()) {
                   this.logger.trace("No 'applicationEventMulticaster' bean, using [" + this.applicationEventMulticaster.getClass().getSimpleName() + "]");
               }
           }
   
       }
 ```

## 初始化监听器

    `AbstractApplicationContext.registerListeners`

```jva
protected void registerListeners() {
    // 添加实现ApplicationListener作为侦听器的bean。
    // 不会影响其他侦听器，可以将它们添加为非bean。

    // Register statically specified listeners first.
    // 先注册静态指定的监听器
    for (ApplicationListener<?> listener : getApplicationListeners()) {
        getApplicationEventMulticaster().addApplicationListener(listener);
    }

    // Do not initialize FactoryBeans here: We need to leave all regular beans
    // uninitialized to let post-processors apply to them!
    // 只是添加 并没有执行
    String[] listenerBeanNames = getBeanNamesForType(ApplicationListener.class, true, false);
    for (String listenerBeanName : listenerBeanNames) {
        getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);
    }

    // Publish early application events now that we finally have a multicaster...
    // 发布早期的时间,并且将 earlyApplicationEvents 设置为空
    Set<ApplicationEvent> earlyEventsToProcess = this.earlyApplicationEvents;
    this.earlyApplicationEvents = null;
    if (!CollectionUtils.isEmpty(earlyEventsToProcess)) {
        for (ApplicationEvent earlyEvent : earlyEventsToProcess) {
            getApplicationEventMulticaster().multicastEvent(earlyEvent);
        }
    }
}
```



### 添加监听器

```
AbstractApplicationEventMulticaster
```

 `getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);`







## 发布事件

`applicationContext.publishEvent`

````java
protected void publishEvent(Object event, @Nullable ResolvableType eventType) {
		Assert.notNull(event, "Event must not be null");
		if (logger.isTraceEnabled()) {
			logger.trace("Publishing event in "+getDisplayName()+": "+event);
		}

		// 将事件装饰成ApplicationEvent
		ApplicationEvent applicationEvent;
		if (event instanceof ApplicationEvent) {
			applicationEvent = (ApplicationEvent) event;
		}
		else {
			applicationEvent = new PayloadApplicationEvent<>(this, event);
			if (eventType == null) {
				eventType = ((PayloadApplicationEvent)applicationEvent).getResolvableType();
			}
		}

		// 广播事件
		if (this.earlyApplicationEvents != null) {
			this.earlyApplicationEvents.add(applicationEvent);
		}
		else {
getApplicationEventMulticaster().multicastEvent(applicationEvent,eventType);
		}
  
		// 如果有父容器，则在父容器内也进行广播
		if (this.parent != null) {
			if (this.parent instanceof AbstractApplicationContext) {
				((AbstractApplicationContext) this.parent).publishEvent(event,eventType);
			}
			else {
				this.parent.publishEvent(event);
			}
		}
	}
````

### 事件广播

```java
//大部分时间会走else分支（看下文解释）
if (this.earlyApplicationEvents != null) {
			this.earlyApplicationEvents.add(applicationEvent);
		}
		else {
			getApplicationEventMulticaster().multicastEvent(applicationEvent,eventType);
	}
```



###  multicastEvent

> 这里可以自定义实现AbstractApplicationEventMulticaster 来做一些基础处理 比如记录日志等

```java
    public void multicastEvent(ApplicationEvent event, @Nullable ResolvableType eventType) {
        ResolvableType type = eventType != null ? eventType : this.resolveDefaultEventType(event);
        //根据事件类型获取当前事件类型下 所有的监听器
        Iterator var4 = this.getApplicationListeners(event, type).iterator();

        while(var4.hasNext()) {
            ApplicationListener<?> listener = (ApplicationListener)var4.next();
            Executor executor = this.getTaskExecutor();
            if (executor != null) {
                executor.execute(() -> {
                    this.invokeListener(listener, event);
                });
            } else {
                this.invokeListener(listener, event);
            }
        }

    }
```



### getApplicationListeners







### invokeListener

```java
    protected void invokeListener(ApplicationListener<?> listener, ApplicationEvent event) {
        ErrorHandler errorHandler = this.getErrorHandler();
        if (errorHandler != null) {
            try {
                this.doInvokeListener(listener, event);
            } catch (Throwable var5) {
                errorHandler.handleError(var5);
            }
        } else {
            this.doInvokeListener(listener, event);
        }

    }


  private void doInvokeListener(ApplicationListener listener, ApplicationEvent event) {
        try {
            //掉方法
            listener.onApplicationEvent(event);
        } catch (ClassCastException var6) {
            String msg = var6.getMessage();
            if (msg != null && !this.matchesClassCastMessage(msg, event.getClass())) {
                throw var6;
            }

            Log logger = LogFactory.getLog(this.getClass());
            if (logger.isTraceEnabled()) {
                logger.trace("Non-matching event type for listener: " + listener, var6);
            }
        }

    }
```



# EventPublisher

不能每次发布事件 都注入applicationContext，可以封装一个类

```java
@Component
public class EventPublisher implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void publishEvent(ApplicationEvent event){
        applicationContext.publishEvent(event);
    }
}
```




# 自定义ApplicationEventMulticaster

+ 可以根据注解类型，确定Listen是同步还是异步

```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventType {
    EventTypeEnum value() default EventTypeEnum.SYNC;
}

```

```java
public enum EventTypeEnum {
    ASYNC, //异步
    SYNC;  //同步
}
```



```java

@Component("applicationEventMulticaster")
public class MyApplicationEventMulticaster extends SimpleApplicationEventMulticaster {

    private static final Logger logger = LoggerFactory.getLogger(MyApplicationEventMulticaster.class);


    @Resource(name = "delayEvaluateExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public MyApplicationEventMulticaster() {
        System.out.println("设置广播器的TaskExecutor>>>>>>>>>>>>>>>>>>>>>");
        setTaskExecutor(threadPoolTaskExecutor);
    }

    @Override
    public void multicastEvent(ApplicationEvent event, ResolvableType eventType) {
        ResolvableType type = eventType != null ? eventType : this.resolveDefaultEventType(event);
        if(null!=threadPoolTaskExecutor){
            setTaskExecutor(threadPoolTaskExecutor);
        }
        Executor executor = this.getTaskExecutor();
        Iterator var5 = this.getApplicationListeners(event, type).iterator();

        //默认异步
        EventTypeEnum defaultEventType = EventTypeEnum.SYNC;

        while (var5.hasNext()) {
            ApplicationListener<?> listener = (ApplicationListener) var5.next();
            try {
                Class<? extends ApplicationListener> cls = listener.getClass();

                Method onApplicationEventMethod = cls.getMethod("onApplicationEvent", ApplicationEvent.class);
                if (onApplicationEventMethod.isAnnotationPresent(EventType.class)) {
                    EventType annotation = onApplicationEventMethod.getAnnotation(EventType.class);
                    defaultEventType = annotation.value();
                }
                printLog(defaultEventType, cls);
                if (executor != null && EventTypeEnum.ASYNC.equals(defaultEventType)) {
                    executor.execute(() -> {
                        this.invokeListener(listener, event);
                    });
                } else {
                    this.invokeListener(listener, event);
                }

            } catch (Exception e) {
                logger.error("错误信息:{}", e.getMessage());
            }


        }
    }

    private void printLog(EventTypeEnum defaultEventType, Class<? extends ApplicationListener> cls) {
        if(cls.getName().contains("com.sf.ares.service.web.dispatch")){
            logger.info("当前Listener:{},方法类型:{}", cls.getName(), defaultEventType);
        }
    }

    private ResolvableType resolveDefaultEventType(ApplicationEvent event) {
        return ResolvableType.forInstance(event);
    }
}

```



https://blog.csdn.net/uk8692/article/details/105920785

