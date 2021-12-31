# Table of Contents

* [IOC的作用](#ioc的作用)
* [控制反转和依赖注入](#控制反转和依赖注入)
* [IOC好处](#ioc好处)
* [IOC底层分析](#ioc底层分析)
* [Spring容器创建过程](#spring容器创建过程)



# IOC的作用

Spring IOC解决的是**对象管理**和**对象依赖**的问题。

简单工厂模式的体现，不过是用反射实现了动态创建。



# 控制反转和依赖注入

+ 控制反转：把原有自己掌控的事情交给别人处理。工厂模式的体现。
+ 依赖注入：其实是【控制反转】的**实现方式**，对象无需自行创建或者管理它的依赖关系，被【自动注入】



# IOC好处

+ 对象集中管理。
+ 降低耦合度。



# IOC底层分析


+ IOC思想基于IOC容器完成,IOC容器底层就是对象工厂。
+ Spring提供IOC容器实现的2种方式:
    + BeanFactory：IOC容器基本实现，Spring内部使用接口
        + 加载配置文件时候**不会**创建对象，获取对象时后才会创建。
    + ApplicationContext：BeanFactory的子接口，提供更多强大的功能，一般是开发人员使用
        + 加载配置文件时候会创建对象。一般采用这种，将耗时放在启动时候

```java
ApplicationContext context=new ClassPathXmlApplicationContext("bean.xml");
        BeanFactory beanFactory=new ClassPathXmlApplicationContext("bean.xml");
```

+ ApplicationContext实现类
    + FileSystemXmlApplicationContext：系统文件
    + ClassPathXmlApplicationContext:相对路径 src下

# Spring容器创建过程

1. Spring容器的refresh()

2. `prepareRefresh()`:**初始化环境**
    +  initPropertySources()初始化属性设置，需要继承applicationContext()重写此方法
    +  getEnvironment().validateRequiredProperties();校验属性的合法性
    +  earlyApplicationEvents = new LinkedHashSet<ApplicationEvent>();初始化事件容器，保存容器中的事件

3. beanFactory = `obtainFreshBeanFactory();` **实例化beanFactory**


+  refreshBeanFactory()获取beanFactory
   this.beanFactory = new DefaultListableBeanFactory();
   设置id
+  getBeanFactory();返回GenericApplicationContext创建的beanFactory

4. `prepareBeanFactory(beanFactory);`**beanFactory的初始化**

    +  设置beanFactory的类加载器、表达式解析
    +  添加部分BeanPostProcessor(new ApplicationContextAwareProcessor(this));
    +  设置aware接口
    +  注册可以解析的自动装配（这点不太懂 我的理解是注册后 实现这个接口可以拿到这些信息）
       BeanFactory、ResourceLoader、ApplicationEventPublisher、ApplicationContext
    +  添加BeanPostProcessor(new ApplicationListenerDetector(this));
    +  注册能用到的组件 environment、systemProperties、systemEnvironment

5. `postProcessBeanFactory(beanFactory)`;**留个子类去实现该接口**

6. `invokeBeanFactoryPostProcessors(beanFactory);`**1. 会在此将class扫描成beanDefinition  2.bean工厂的后置处理器调用**

   2大接口：BeanFactoryPostProcessor、BeanDefinitionRegistryPostProcessor

    + invokeBeanFactoryPostProcessors

```java
        
 	1.  获取所有的BeanDefinitionRegistryPostProcessor
 	2.  优先执行实现了PriorityOrdered的BeanDefinitionRegistryPostProcessor
        invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
        
    3. 在执行实现了Ordered的BeanDefinitionRegistryPostProcessor
    4. 最后执行没有实现优先级的BeanDefinitionRegistryPostProcessor
    5. 在执行BeanFactoryPostProcessor，和上面一样的执行顺序
```


7. `registerBeanPostProcessors(beanFactory);` **注册Bean自己的后置处理器**
   BeanPostProcessor

   DestructionAwareBeanPostProcessor

   InstantiationAwareBeanPostProcessor

   SmartInstantiationAwareBeanPostProcessor

   MergedBeanDefinitionPostProcessor【internalPostProcessors】

```
1. 获取所有的BeanPostProcessor，也会有优先级的顺序 PriorityOrdered 、Ordered
2. 注册PriorityOrdered   BeanPostProcessors添加到beanFactoty
3. 注册Ordered  BeanPostProcessors添加到beanFactoty
4. 注册BeanPostProcessors添加到beanFactoty
5. 最后注册internalPostProcessors到beanFactory
6. 注册ApplicationListenerDetector
```

8. `initMessageSource();` 初始化消息组件，做消息绑定 消息解析
    1. 获取beanFactory
    2. 创建applicationEventMulticaster（有 赋值 没有创建 new SimpleApplicationEventMulticaster(beanFactory)）、
    3. 注册容器中

9. `initApplicationEventMulticaster();` 初始化事件派发器
    1. 获取beanFactory
    2. 获取applicationEventMulticaster 没有创建 new SimpleApplicationEventMulticaster(beanFactory);
    3. 注册容器中

10. `onRefresh();`这个方法同样也是留个子类实现的springboot也是从这个方法进行启动tomcat的.

11. `registerListeners();` 注册applicationListeners
        	1. 获取所有的applicationListener
             	2. 添加到事件派发器
             	3. 派发之前步骤产生的事件

12. **finishBeanFactoryInitialization(beanFactory);重点**

    + beanFactory.preInstantiateSingletons() 初始化剩下的bean

        1. 拿到所有bean的定义信息 循环创建以及初始化

        2. bean不是抽象的，是单实例的，是懒加载
```java
1.判断是否是factoryBean
2.getBean()->doGetBean
3.getSingleton(beanName)获取缓存中的bean

  private final Map<String, Object> singletonObjects = new ConcurrentHashMap<String, Object>(256);
  三级缓存就是在这里被运用的
4.缓存获取不到，获取beanFactory  
5. //标记bean已创建
   markBeanAsCreated(beanName);
6. 获取bean定义信息
7. 获取当前bean依赖的其他bean，如果有getBean

 //这里就会有循环依赖的问题 如果是构造器注入的话
  String[] dependsOn = mbd.getDependsOn();
  if (dependsOn != null) {
     for (String dep : dependsOn) {
        if (isDependent(beanName, dep)) {
           throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                 "Circular depends-on relationship between '" + beanName + "' and '" + dep + "'");
        }
        registerDependentBean(dep, beanName);
        getBean(dep);
     }
  }
8.启动单实例bean的创建流程
    1. createBean(beanName, mbd, args);
         //创建bbp对象
         Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
          protected Object resolveBeforeInstantiation(String beanName, RootBeanDefinition mbd) {
                     		Object bean = null;
                     		if (!Boolean.FALSE.equals(mbd.beforeInstantiationResolved)) {
                     			//bbp提前执行
                     			if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
                     				Class<?> targetType = determineTargetType(beanName, mbd);
                     				if (targetType != null) {
                     					bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
                                         //前置返回bean  才会执行后置的
                     					if (bean != null) {
                     						bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
                     					}
                     				}
                     			}
                     			mbd.beforeInstantiationResolved = (bean != null);
                     		}
                     		return bean;
                     	}
    2.如果没有返回代理对象 就创建bean 
     Object beanInstance = doCreateBean(beanName, mbdToUse, args);
        1.创建bean实例 
             instanceWrapper = createBeanInstance(beanName, mbd, args);
         2. 执行bpp
             applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
         3.添加到二级缓存或者三级缓存中去
         4. 初始化bean(自动装配)
             populateBean(beanName, mbd, instanceWrapper);
         	1)执行InstantiationAwareBeanPostProcessor.postProcessAfterInstantiation()
             2)执行InstantiationAwareBeanPostProcessor.postProcessPropertyValues()
             3)为属性赋值
                 applyPropertyValues(beanName, mbd, bw, pvs);
         5.initializeBean(beanName, exposedObject, mbd);
         	1)执行aware
                 invokeAwareMethods(beanName, bean);
         	2)执行bbp
                 applyBeanPostProcessorsBeforeInitialization()
             3)执行初始化方法
                 invokeInitMethods(beanName, wrappedBean, mbd);
         		((InitializingBean) bean).afterPropertiesSet()
         	4)执行bbp
                 applyBeanPostProcessorsAfterInitialization()
         6.获取单实例bean
             getSingleton(beanName, false);
         7.注册销毁方法
             registerDisposableBeanIfNecessary(beanName, bean, mbd);
         8. 放入缓存
```
        3. SmartInitializingSingleton.afterSingletonsInstantiated()
13. `finishRefresh();`最后容器刷新 发布刷新事件,完成beanFactory初始化创建工作，ioc创建完成
    + initLifecycleProcessor();
    + getLifecycleProcessor().onRefresh();
    + publishEvent(new ContextRefreshedEvent(this));
    + LiveBeansView.registerApplicationContext(this);
