# Table of Contents

* [普通java对象和Spring所管理的Bean](#普通java对象和spring所管理的bean)
* [Bean的生命周期](#bean的生命周期)
* [为什么要在初始前还要调用Aware接口](#为什么要在初始前还要调用aware接口)
* [执行顺序](#执行顺序)



# 普通java对象和Spring所管理的Bean



Spring管理的Bean除了class对象外，还有`beanDefinition`的实例来描述对象信息。

+ 启动扫描管理`bean`（xml 注解）
+ 封装成`beanDefinition`放入map




# Bean的生命周期

Spring Bean的生命周期，Spring预留了很多`hook`给我们取扩展。



+ Spring在创建bean时，是分三个步骤的

    + 实例化： 可以理解为new一个对象
    + 属性赋值：可以理解为调用set方法进行属性注入
    + 初始化： 按照spring的规则配置一些初始化方法 如	`@PostConstruct`(InitDestroyAnnotationBeanPostProcessor)注解

+ 生命周期的概念

  ​	bean的生命周期，其实就是后置处理器`beanpostprocessor` 穿插执行的过程

+ `beanpostprocessor` 的分类

    + 直接实现了`beanpostprocessor` 接口，这种只能在bean的初始化前后执行

       ```java
      public interface BeanPostProcessor {
          
       // 初始化前执行的方法
       @Nullable
       default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
       }    
          
       // 初始化后执行的方法
       default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
       }
      
      }
      ```



+ 直接实现了`InstantiationAwareBeanPostProcessor`接口,是可以在bean的实例化前后进行，典型的就是aop代理对象产生

  ```java
  // 继承了BeanPostProcessor，额外提供了两个方法用于在实例化前后的阶段执行
  // 因为实例化后紧接着就要进行属性注入，所以这个接口中还提供了一个属性注入的方法
  public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {
   
      // 实例化前执行
   default Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
    return null;
   }
      
      // 实例化后置
   default boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
    return true;
   }
      
      // 属性注入
      default PropertyValues postProcessPropertyValues(
          PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
          return pvs;
      }
  }
  ```

+ Spring内部专用的后置处理器，它是InstantiationAwareBeanPostProcessor接口的一个扩展。主要在Spring框架内部使用

+ 生命周期详细介绍

  ![图片](https://mmbiz.qpic.cn/mmbiz_png/tpEILlElskLg3XfAkuxVPvzU8SsFFprf02Aht1HibSsia3kmxycTTxjOLpxfYh3SR1ceiaU3ruCOIldRGdheae15Q/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

至少现在这张图上缺少了实例化前后后置处理器的执行流程，对吧？



# 为什么要在初始前还要调用Aware接口

> 其实就是需要用到：ApplicationContext

但是为什么要在初始前还要调用Aware接口的方法，如果你看了源码的话可能会说，源码就是这么写的，别人就是这么设计的，但是为什么要这么设计呢？**我们看源码到一定阶段后不应该仅仅停留在是什么的阶段，而应该多思考为什么，这样能帮助你更好的了解这个框架**


invokeAwareMethods->BeanNameAware->BeanFactoryAware

这样做的目的是因为，初始化可能会依赖Aware接口提供的状态，例如下面这个例子

```java
@Component
public class A implements InitializingBean, ApplicationContextAware {

    ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 初始化方法需要用到ApplicationContextAware提供的ApplicationContext
        System.out.println(applicationContext);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
```



# 执行顺序

另外，在讨论Bean的初始化的时候经常会碰到下面这个问题，`@PostConstruct`,`afterPropertiesSet`跟XML中配置的`init-method`方法的执行顺序。

请注意，`@PostConstruct`实际上是在`postProcessBeforeInitialization`方法中处理的，严格来说它不属于初始化阶段调用的方法，所以这个方法是最先调用的

1. `@PostConstruct`注解标注的方法
2. 实现了`InitializingBean`接口后复写的`afterPropertiesSet`方法
3. XML中自定义的初始化方法
