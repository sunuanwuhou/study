# Table of Contents

* [spring例子](#spring例子)
* [通用日志组件封装](#通用日志组件封装)
* [定义基础类](#定义基础类)
* [定义注解](#定义注解)
  * [定义配置项处理具体逻辑](#定义配置项处理具体逻辑)
  * [如何使用](#如何使用)


我们都知道sping-boot-starter的用法，引入jar包，会自动注入jar包的一些属性方法。

本文讲解@Enablexx的用法，个人理解@Enable是可以传递一些参数给jar包。



# spring例子

```java
@Import({AutoConfigurationImportSelector.class})
public @interface EnableAutoConfiguration {
    String ENABLED_OVERRIDE_PROPERTY = "spring.boot.enableautoconfiguration";

    Class<?>[] exclude() default {};

    String[] excludeName() default {};
}
```

EnableAutoConfiguration是注解，但是注解会有一些参数。

真正起作用的是AutoConfigurationImportSelector，我们可以在AutoConfigurationImportSelector中可以看到。可以获取EnableAutoConfiguration注解的一些参数，做自定义处理。



# 通用日志组件封装

背景：每个项目都会有自己的操作日志，但是每个项目的操作日志表名都不一样，a_operation，b_operation,我们如何封装通用starter呢？

# 定义基础类

+ po

  ​	

  ```java
  @TableName("")
  public class OperationPO {
  
  }
  ```

  这里我们需要动态，根据调用方传递过来的参数，替换表名。因为每个项目的表名不一样。

+ mapper

+ service



#  定义注解

```java
@Import({EnableLogCompareConfiguration.class})
public @interface EnableLogCompare {
   
    String  table() default '';
}
```



## 定义配置项处理具体逻辑

```java
public class EnableLogCompareConfiguration implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        //做2件事 1.注入当前的 mapper service 
        // 将beanDefinition注册到Ioc容器中.
        // registry.registerBeanDefinition("person", beanDefinition);

        //2.获取注解名字，替换当前表名
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableLogCompare.class.getName()));

        String table = annotationAttributes.getString("table");


		//反射修改注解属性

        // 获取 Test 上的注解
        TableName annoTable = Test.class.getAnnotation(TableName.class);
        // 获取代理处理器
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(annoTable);
        // 过去私有 memberValues 属性
        Field f = invocationHandler.getClass().getDeclaredField("memberValues");
        f.setAccessible(true);
        // 获取实例的属性map
        Map<String, Object> memberValues = (Map<String, Object>) f.get(invocationHandler);
        // 修改属性值
        memberValues.put("value", tableName);

    }

}
```

## 如何使用

```java
@EnableLogCompare(table='xx')
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

