# Table of Contents

* [组件注册](#组件注册)
  * [@Configuration](#configuration)
  * [@ComponentScan](#componentscan)
    * [FilterType](#filtertype)
      * [FilterType.CUSTOM](#filtertypecustom)
  * [@Scope](#scope)
  * [@Lazy](#lazy)
  * [==@Conditional==](#conditional)
  * [==@import==](#import)
  * [FactoryBean](#factorybean)
* [Bean生命周期](#bean生命周期)
  * [@Bean指定初始化和销毁](#bean指定初始化和销毁)
  * [InitializingBean和DisposableBean](#initializingbean和disposablebean)
  * [@PostConstruct和@PreDestory](#postconstruct和predestory)
  * [@BeanPostProcessor](#beanpostprocessor)
  * [@BeanPostProcessor原理](#beanpostprocessor原理)
  * [@BeanPostProcessor在Spring底层使用](#beanpostprocessor在spring底层使用)
* [组件赋值](#组件赋值)
  * [@Value](#value)
  * [@PropertySource](#propertysource)
* [组件注入](#组件注入)
  * [@Autowired](#autowired)
  * [@Resource @Inject](#resource-inject)
    * [方法 构造器自动装配](#方法-构造器自动装配)
  * [Aware接口原理](#aware接口原理)
  * [@Profile](#profile)


[TOC]

文章来源：https://www.bilibili.com/video/BV1ME411o7Uu?p=4  
大纲
![image](https://note.youdao.com/yws/public/resource/984c7d822d19c1882f18f49e1a982d51/xmlnote/C97E241296AF44D9A90BB0CEB248B129/2424)


# 组件注册

## @Configuration

用于定义配置类，被定义的类会被AnnotationConfigApplicationContext或AnnotationConfigWebApplicationContext类进行扫描，并用于构建bean定义，初始化Spring容器。

一般和@bean一起使用

```xml
@Configuration就相当于Spring配置文件中的<beans />标签，里面可以配置bean
```

```java
@Bean相当于Spring配置文件中的<bean/>标签可以在Spring容器中注入一个bean
```

## @ComponentScan



```xml
//相当于之前
<context:component-scan/>
```
```java
@ComponentScan(basePackages ={"spring.java"},
includeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION,classes = Controller.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,classes = {BookNot.class,Person.class})},
        //是否开启默认扫描 @Controller，@Service，@Repository，@Component 
        useDefaultFilters = false,
excludeFilters={
        @ComponentScan.Filter(type = FilterType.ANNOTATION,classes = Controller.class)}
        )
public class MyConfig {
```

+ @ComponentScan自定扫描路径下边带有@Controller，@Service，@Repository，@Component注解的类到spring容器中
+ includeFilters指定要扫描的包
+ excludeFilters指定要排除的
+ 可以自定义指定某个类是否被扫描
+ @componentScan 指定多个扫描

###  FilterType

```java
//常用
FilterType.ANNOTATION：注解
FilterType.ASSIGNABLE_TYPE:自定义
//不太常用
FilterType.ASPECTJ：按照Aspectj的表达式，基本上不会用到
FilterType.REGEX：按照正则表达式
FilterType.CUSTOM：自定义规则
```

#### FilterType.CUSTOM

编写自定义类实现TypeFilter

```java
public class MyFilterType implements TypeFilter {

    //metadataReader
    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        //metadataReader:the metadata reader for the target class 当前类信息
        //metadataReaderFactory:a factory for obtaining metadata readers  其他类信息
        //获取当前类注解的信息
        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
        //获取当前正在扫描的类的信息
        ClassMetadata classMetadata = metadataReader.getClassMetadata();
        //获取当前类的资源信息，例如：类的路径等信息
        Resource resource = metadataReader.getResource();
        //获取当前正在扫描的类名
        String className = classMetadata.getClassName();

        //自定义你想要要加入容器的

        return false;// true表示加入进去
    }
}

```

## @Scope

spring默认bean都是单例的singleton

+ singleton:全局有且仅有一个实例,启动时会调用方法放入ioc容器中，后面直接从容器中拿
+ prototype：启动不放入ioc容器中，每次获取Bean的时候会有一个新的实例
+ request:request表示该针对每一次HTTP请求都会产生一个新的bean，同时该bean仅在当前HTTP request内有效
+ session: session作用域表示该针对每一次HTTP请求都会产生一个新的bean，同时该bean仅在当前HTTP session内有效

## @Lazy

表示用到的时候初始化

## ==@Conditional==

+ 按照一定的条件进行判断，满足条件，给容器中注册bean

```java
//其他版本的 @ContionalOnClass 等等 其实也是实现了Condition接口
public class MyCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        //自己的逻辑判断
        
        return false;
    }
}
```

+ 可以作用在方法上，也可以放在类上

```java
@Configuration
@Conditional({MyCondition.class})
public class MyConfig1 {

    @Bean(name = "person")
    // @Scope("singleton")
    // @Lazy
    public Person person1(){
        return new Person("1",12);
    }

    @Bean("bill")
    // @Conditional({MyCondition.class})
    public Person person(){
        return new Person("conditional",12);
    }

}
```

+ 多个自定义condition

C1&C2

## ==@import==

给容器中加bean
1. @ComponentScan扫描的注解
2. @Bean
3. @Import

具体用法

1. @Import默认是类的群路径  
   @Import({BookNot.class, Book.class})
2. **@ImportSelector**

```java
public class MyImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {

        String className = importingClassMetadata.getClassName();
        //自定义选择注入的类
        return new String[0];
    }
}
```

3. **ImportBeanDefinitionRegistrar**

```java
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        //扫描注解
        Map<String, Object> annotationAttributes = importingClassMetadata
                .getAnnotationAttributes(ComponentScan.class.getName());
        String[] basePackages = (String[]) annotationAttributes.get("basePackages");

        //扫描类
        ClassPathBeanDefinitionScanner scanner =
                new ClassPathBeanDefinitionScanner(registry, false);

        TypeFilter helloServiceFilter = new AssignableTypeFilter(Book.class);

        scanner.addIncludeFilter(helloServiceFilter);
        scanner.scan(basePackages);
        
        //也可以直接调用registry注册bean
        // registry.registerBeanDefinition();
    }
}
```



```java
@Configuration
// @Conditional({MyCondition.class})
@Import({BookNot.class, Book.class,MyImportSelector.class,MyImportBeanDefinitionRegistrar.class})
public class MyConfig1 {
```

## FactoryBean

```java
public class MyFactoryBean implements FactoryBean {


    @Override
    public Object getObject() throws Exception {
        return new Person();
    }

    @Override
    public Class<?> getObjectType() {
        return Person.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
```

+ 默认获取的是工厂bean创建的对象
+ 使用‘&’获取工厂本身bean

``` //获取的是getObject实例
 //获取的是getObject实例
 Object myFactoryBean = context.getBean("myFactoryBean");
 //获取的是myFactoryBean实例
 //public interface BeanFactory {
 //String FACTORY_BEAN_PREFIX = "&";
 Object myFactoryBean1 = context.getBean("&myFactoryBean");
```

# Bean生命周期

了解Spring容器下的生命周期，可以更好的帮我们理解AOP、spring循环依赖（Spring三级缓存）

+ 传统的bean生命周期很简单 实例化-使用-销毁




## @Bean指定初始化和销毁

**==Spring只会管理单实例的bean,多实例只会创建,剩下交给使用者==**

```java
 @Bean(initMethod = "init",destroyMethod = "destory")
    public Person person() {
        return new Person();
    }
```

## InitializingBean和DisposableBean



```java
public class Person implements InitializingBean, DisposableBean {
    @Override
    public void destroy() throws Exception {
        System.out.println("destroy--------------");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("afterPropertiesSet--------------");
    }
```

==Constructor >InitializingBean>ininMethod>DisposableBean>destroyMethod==



```java
AbstractAutowireCapableBeanFactory.invokeInitMethods

```

## @PostConstruct和@PreDestory

作用于方法上

Constructor >PostConstruct>InitializingBean>ininMethod>PreDestory>DisposableBean>destroyMethod


## @BeanPostProcessor

```java
public interface BeanPostProcessor {
    //bean初始化方法调用前被调用
    Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException;
    //bean初始化方法调用后被调用
    Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;
}
```

正确的：Constructor>postProcessBeforeInitialization>@PostConstruct>InitializingBean>ininMethod>postProcessAfterInitialization>@PreDestory>DisposableBean>destroyMethod

**自己写的有问题的：后面看了spring整体流程再来找问题**

Constructor>@PostConstruct>InitializingBean>ininMethod>**postProcessBeforeInitialization**>postProcessAfterInitialization>@PreDestory>DisposableBean>destroyMethod

## @BeanPostProcessor原理

https://www.bilibili.com/video/BV1ME411o7Uu?p=16#sepwin=VIDEO



![image](https://note.youdao.com/yws/public/resource/984c7d822d19c1882f18f49e1a982d51/xmlnote/C1B6BD2F993E4016A309B1EAADCA3F0E/2467)



```java
//赋值
populateBean(beanName, mbd, instanceWrapper);
if (exposedObject != null) {
    //初始化bean
   exposedObject = initializeBean(beanName, exposedObject, mbd);
}
```


```java
//initializeBean

Object wrappedBean = bean;
if (mbd == null || !mbd.isSynthetic()) {
    //before
   wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
}

try {
    //初始化方法
   invokeInitMethods(beanName, wrappedBean, mbd);
}
catch (Throwable ex) {
   throw new BeanCreationException(
         (mbd != null ? mbd.getResourceDescription() : null),
         beanName, "Invocation of init method failed", ex);
}

if (mbd == null || !mbd.isSynthetic()) {
    //after
   wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
}
```

## @BeanPostProcessor在Spring底层使用

ApplicationContextAwareProcessor  
AutowiredAnnotationBeanPostProcessor

# 组件赋值

## @Value

```
//1.基本数值
//2.SPEL #{}
//3. ${} 配置文件的值

@Value("zhangsan")
String name;
@Value("#{20-2}")
Integer age;
```

## @PropertySource


@PropertySources

```java
@PropertySource("classpath:/person.properties")
@Configuration
public class MyConfigPropertyValues {


    @Bean
    public MyConfigPropertyValuesBean myConfigPropertyValuesBean(){
        return new MyConfigPropertyValuesBean();
    }
}
```

```
person.name=nihao
```

```
public class MyConfigPropertyValuesBean {

    //1.基本数值
    //2.SPEL #{}
    //3. ${} 配置文件的值

    @Value("zhangsan")
    String name;
    @Value("#{20-2}")
    Integer age;
    @Value("${person.name}")
    String names;
```

# 组件注入



Spring利用依赖注入（DI）,完成对IOC容器中各个组件的依赖关系赋值。

## @Autowired

可参考： <https://www.jianshu.com/p/2f1c9fad1d2d>



+ 优先按照类型去容器中找对应的组件(byType)
+ 如果找到相同的，在将属性的名称作为组件的id去容器中（byName）
+ 配合 @Qualifier 指定按照名称去装配 bean。
+ 自动装配前，如果没有对应的组件会报错，@Autowired(required = false)
+ @Primary：自动装配时当出现多个Bean候选者时，被注解为@Primary的Bean将作为首选者，否则将抛出异常

如果注册一个bean，在配置类重新Bean 只会找配置类的第一个Bean

##   @Resource @Inject

+ @Resource JSR250 java原生注解
    1. 默认按照byName去装配
    2. 不能支持@Primary、@Qualifier 和(required = false)
+ @Inject  JSR330 java原生注解
    1. 跟@Autowired一样，不支持required = false
    2. 需要导入依赖

### 方法 构造器自动装配

1. 方法位置

```java
@Autowired
public void setBookNot(BookNot bookNot) {
    this.bookNot = bookNot;
}
```

2. 构造器

```java
//默认在ioc容器中的组件，启动会默认调用无参构造创建对象，在进行初始化赋值
@Component
public class Boss {

    @Autowired
  //如果组件只有一个有参构造器 @Autowired可以省略
    public Boss(BookNot bookNot) {
        this.bookNot = bookNot;
    }
```

但是这种情况容易造成循环依赖问题

3. 方法上标注

   ```
   @Bean
   public Boss boss(BookNot bookNot){
       Boss boss = new Boss();
       boss.setBookNot(bookNot);
       return boss;
   }
   ```

## Aware接口原理

ApplicationContextAware

实现Aware接口，将Spring底层组件注入自定的方法中

```java
@Component
public class MyApplicationContextAware implements ApplicationContextAware, BeanNameAware, EmbeddedValueResolverAware {

    private ApplicationContext applicationContext=null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
        System.out.println(applicationContext);
    }

    @Override
    public void setBeanName(String name) {
        System.out.println(name);
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        System.out.println(resolver);
    }

    @Override
    public String toString() {
        return "MyApplicationContextAware{" +
                "applicationContext=" + applicationContext +
                '}';
    }
}
```

```java
private void invokeAwareInterfaces(Object bean) {
   if (bean instanceof Aware) {
      if (bean instanceof EnvironmentAware) {
         ((EnvironmentAware) bean).setEnvironment(this.applicationContext.getEnvironment());
      }
      if (bean instanceof EmbeddedValueResolverAware) {
         ((EmbeddedValueResolverAware) bean).setEmbeddedValueResolver(this.embeddedValueResolver);
      }
      if (bean instanceof ResourceLoaderAware) {
         ((ResourceLoaderAware) bean).setResourceLoader(this.applicationContext);
      }
      if (bean instanceof ApplicationEventPublisherAware) {
         ((ApplicationEventPublisherAware) bean).setApplicationEventPublisher(this.applicationContext);
      }
      if (bean instanceof MessageSourceAware) {
         ((MessageSourceAware) bean).setMessageSource(this.applicationContext);
      }
      if (bean instanceof ApplicationContextAware) {
         ((ApplicationContextAware) bean).setApplicationContext(this.applicationContext);
      }
   }
}

```

## @Profile


pring为我们提供的可以根据当前环境，动态的激活和切换一系列组件的功能；


1. 加了环境标识的bean，只有这个环境被激活的时候才能注册到容器中。默认是default环境

2. 写在配置类上，只有是指定的环境的时候，整个配置类里面的所有配置才能开始生

   ```java

   /**
    *
    *  Spring为我们提供的可以根据当前环境，动态的激活和切换一系列组件的功能；
    *
    *  local dev test 数据源
    *  @Profile:指定组件在哪个环境的情况下才能被注册到容器中，不指定，任何环境下都能注册这个组件
    *
    * @version 1.0
    * @description
    * @date 2020/7/3 21:26
    */
   // @Profile("local") 只有指定的环境 配置类才会生效
   @PropertySource("classpath:db.properties")
   @Configuration
   public class MyConfigProfile implements EmbeddedValueResolverAware {
   ```



       @Value("${db.user}")
       String user;
    
       @Value("${db.password}")
       String password;
    
       String driver;
    
       // @Profile("local")
       @Bean("local")
       public DataSource local() throws PropertyVetoException {
           ComboPooledDataSource dataSource = new ComboPooledDataSource();
    
           dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/40028");
           dataSource.setPassword(password);
           dataSource.setUser(user);
           dataSource.setDriverClass(driver);
           return dataSource;
       }
    
       @Profile("dev")
       @Bean("dev")
       public DataSource dev() throws PropertyVetoException {
           ComboPooledDataSource dataSource = new ComboPooledDataSource();
    
           dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/kmyx");
           dataSource.setPassword(password);
           dataSource.setUser(user);
           dataSource.setDriverClass(driver);
           return dataSource;
       }
    
       @Profile("test")
       @Bean("test")
       public DataSource test() throws PropertyVetoException {
           ComboPooledDataSource dataSource = new ComboPooledDataSource();
    
           dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/24");
           dataSource.setPassword(password);
           dataSource.setUser(user);
           dataSource.setDriverClass(driver);
           return dataSource;
       }


       @Override
       public void setEmbeddedValueResolver(StringValueResolver resolver) {
    
           String driver = resolver.resolveStringValue("${db.driver}");
           this.driver=driver;
       }
}

   ```


激活环境

1. 使用命令行,在虚拟机参数位置加载 -Dspring.profiles.active=test

2. 使用代码的方式激活某种环境

   ```java
   public static void main(String[] args) {

       AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
       //这里不能使用有参构造
       //设置环境
       context.getEnvironment().setActiveProfiles("dev","test");
       context.register(MyConfigProfile.class);
       context.refresh();

       String[] beanNamesForType = context.getBeanNamesForType(DataSource.class);

       for (String s : beanNamesForType) {
           System.out.println(s);
       }

   }
   ```



    
