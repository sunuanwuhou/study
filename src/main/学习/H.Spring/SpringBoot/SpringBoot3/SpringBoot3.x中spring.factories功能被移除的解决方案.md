# Table of Contents

* [关于spring.factories](#关于springfactories)
* [spring.factories被移除后的替代方案](#springfactories被移除后的替代方案)


# 关于spring.factories

`spring.factories`其实是`SpringBoot`提供的`SPI`机制，底层实现是基于`SpringFactoriesLoader`检索`ClassLoader`中所有`jar`（包括`ClassPath`下的所有模块）引入的`META-INF/spring.factories`文件，基于文件中的接口（或者注解）加载对应的实现类并且注册到`IOC`容器。这种方式对于`@ComponentScan`不能扫描到的并且想自动注册到`IOC`容器的使用场景十分合适，基本上绝大多数第三方组件甚至部分`spring-projects`中编写的组件都是使用这种方案。

`spring.factories`文件的格式大致如下：

```properties
# Initializers
org.springframework.context.ApplicationContextInitializer=\
org.springframework.boot.autoconfigure.SharedMetadataReaderFactoryContextInitializer,\
org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener

# Application Listeners
org.springframework.context.ApplicationListener=\
org.springframework.boot.autoconfigure.BackgroundPreinitializer

# Environment Post Processors
org.springframework.boot.env.EnvironmentPostProcessor=\
org.springframework.boot.autoconfigure.integration.IntegrationPropertiesEnvironmentPostProcessor

# Auto Configuration Import Listeners
org.springframework.boot.autoconfigure.AutoConfigurationImportListener=\
org.springframework.boot.autoconfigure.condition.ConditionEvaluationReportAutoConfigurationImportListener

# Auto Configuration Import Filters
org.springframework.boot.autoconfigure.AutoConfigurationImportFilter=\
org.springframework.boot.autoconfigure.condition.OnBeanCondition,\
org.springframework.boot.autoconfigure.condition.OnClassCondition,\
org.springframework.boot.autoconfigure.condition.OnWebApplicationCondition

org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.baomidou.mybatisplus.autoconfigure.MybatisPlusLanguageDriverAutoConfiguration,\
com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration
```

通用格式是：`接口（或者注解）全类名=\接口实现类（或者使用了该注解的类）全类名-1,\接口实现类（或者使用了该注解的类）全类名-2,\...接口实现类（或者使用了该注解的类）全类名-n`。`spring.factories`中最常用的注解是`org.springframework.boot.autoconfigure.EnableAutoConfiguration`，通过配置此注解对应的实现了，底层会由`AutoConfigurationImportSelector`对响应的目标类进行加载和自动注册。通过阅读[Spring Boot 3.0 Migration Guide](https://link.juejin.cn/?target=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-boot%2Fwiki%2FSpring-Boot-3.0-Migration-Guide)得知，`spring.factories`功能在`Spring Boot 2.7`已经废弃，并且会在`Spring Boot 3.0`移除。





# spring.factories被移除后的替代方案

`Spring Boot 2.x`升级到`Spring Boot 3.0`其实是一个"破坏性"升级，目前来看相对较大的影响是：

- 必须使用`JDK17`
- `Jakarta EE`的引入，导致很多旧的类包名称改变
- 部分类被彻底移除
- `spring-data`模块的所有配置属性必须使用`spring.data`前缀，例如`spring.redis.host`必须更变为`spring.data.redis.host`
- `spring.factories`功能在`Spring Boot 2.7`已经废弃，在`Spring Boot 3.0`彻底移除（见下图）



替代方案比较简单，就是在类路径下创建`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`文件，文件的内容是：**每个实现类的全类名单独一行**。例如对于使用了（低版本还没适配`Spring Boot 3.0`）`mybatis-plus`、`dynamic-datasource`组件的场景，

**可以在项目某个模块的`resources`目录下建立`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`文件，输入以下内容：**

```properties
com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration
com.baomidou.mybatisplus.autoconfigure.MybatisPlusLanguageDriverAutoConfiguration
com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration
```

