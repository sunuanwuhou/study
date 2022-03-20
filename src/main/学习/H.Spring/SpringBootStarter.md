# Table of Contents

* [为什么要定义starter](#为什么要定义starter)
* [封装starer](#封装starer)
* [shrding-jdbc starter的SPI 机制](#shrding-jdbc-starter的spi-机制)
  * [shrding-jdbc starter自带SPI机制](#shrding-jdbc-starter自带spi机制)
  * [为什么要搞SPI机制](#为什么要搞spi机制)
  * [为什么要放在services下，还要是当前接口的全路径？](#为什么要放在services下还要是当前接口的全路径)




我们就以`sharding-jdbc-spring-boot-starter`为列子来看看是怎么封装的



# 为什么要定义starter

本质是为项目引入一个公共组件

对于代码来说，就是抽取公共类封装成一个starter，然后在引入进来

# 封装starer

1. 首先定义自己项目工程

   ```java
       <modelVersion>4.0.0</modelVersion>
       <parent>
           <groupId>org.apache.shardingsphere</groupId>
           <artifactId>sharding-jdbc-spring</artifactId>
           <version>4.0.1</version>
       </parent>
       <artifactId>sharding-jdbc-spring-boot-starter</artifactId>
       <name>${project.artifactId}</name>
        //其他一些依赖
            <dependencies>
           <dependency>
               <groupId>org.apache.shardingsphere</groupId>
               <artifactId>sharding-spring-boot-util</artifactId>
               <version>${project.version}</version>
           </dependency>
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter</artifactId>
           </dependency>
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-configuration-processor</artifactId>
               <optional>true</optional>
           </dependency>
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-test</artifactId>
           </dependency>
       </dependencies>
   ```

2. 新建配置类，写好配置项和默认的配置值，指明配置前缀

   ```java
   @ConfigurationProperties(prefix = "spring.shardingsphere.sharding")
   public class SpringBootShardingRuleConfigurationProperties extends YamlShardingRuleConfiguration {
   }
   
   @ConfigurationProperties(prefix = "spring.shardingsphere.masterslave")
   public class SpringBootMasterSlaveRuleConfigurationProperties extends YamlMasterSlaveRuleConfiguration {
   }
   
   @ConfigurationProperties(prefix = "spring.shardingsphere.encrypt")
   public class SpringBootEncryptRuleConfigurationProperties extends YamlEncryptRuleConfiguration {
   }
   
   @ConfigurationProperties(prefix = "spring.shardingsphere")
   public class SpringBootPropertiesConfigurationProperties {
       private Properties props = new Properties();
   }
   
   -- 每一个配置类都对应一些配置属性
   ```

3. 新建自动装配类，使用`@Configuration`和`@Bean`来进行自动装配。

   ```java
   @Configuration
   @ComponentScan("org.apache.shardingsphere.spring.boot.converter")
   @EnableConfigurationProperties({
           SpringBootShardingRuleConfigurationProperties.class, 
           SpringBootMasterSlaveRuleConfigurationProperties.class, SpringBootEncryptRuleConfigurationProperties.class, SpringBootPropertiesConfigurationProperties.class})
   @ConditionalOnProperty(prefix = "spring.shardingsphere", name = "enabled", havingValue = "true", matchIfMissing = true)
   @AutoConfigureBefore(DataSourceAutoConfiguration.class)
   @RequiredArgsConstructor
   public class SpringBootConfiguration implements EnvironmentAware {
       
       //以下是配置文件
       private final SpringBootShardingRuleConfigurationProperties shardingRule;
       
       private final SpringBootMasterSlaveRuleConfigurationProperties masterSlaveRule;
       
       private final SpringBootEncryptRuleConfigurationProperties encryptRule;
       
       private final SpringBootPropertiesConfigurationProperties props;
       
       private final Map<String, DataSource> dataSourceMap = new LinkedHashMap<>();
       
       private final String jndiName = "jndi-name";
   
   	//以下是其他方法逻辑
   
   
    	 @Bean
       @Conditional(EncryptRuleCondition.class)
       public DataSource encryptDataSource() throws SQLException {
           return EncryptDataSourceFactory.createDataSource(dataSourceMap.values().iterator().next(), new EncryptRuleConfigurationYamlSwapper().swap(encryptRule), props.getProps());
       }
   }
   ```

4. 新建spring.factories文件，指定Starter的自动装配类。

   ```java
   org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
   org.apache.shardingsphere.shardingjdbc.spring.boot.SpringBootConfiguration
   ```

   

   > spring.factories文件位于resources/META-INF目录下，需要手动创建;
   > `org.springframework.boot.autoconfigure.EnableAutoConfiguration`后面的类名说明了自动装配类，如果有多个 ，则用逗号分开;
   >
   > 使用者应用（SpringBoot）在启动的时候，会通过`org.springframework.core.io.support.SpringFactoriesLoader`读取classpath下每个Starter的spring.factories文件，加载自动装配类进行Bean的自动装配；

5. 使用就很简单了，导入starter的maven坐标，配置好就可以了。



#  shrding-jdbc starter的SPI 机制

SPI 机制本质是将 **接口实现类的全限定名配置在文件中**，并由服务加载器读取配置文件，加载文件中的实现类，**这样运行时可以动态的为接口替换实现类**

简单一点来说，就是你在 `META-INF/services` 下面定义个文件，然后通过一个特殊的类加载器，启动的时候加载你定义文件中的类，这样就能扩展原有框架的功能



## shrding-jdbc starter自带SPI机制



![image-20220320143308024](.images/image-20220320143308024.png)


## 为什么要搞SPI机制

以实际项目举个例子，就拿 sharding-jdbc 数据加密模块来说，sharding-jdbc 本身支持 AES 和 MD5 两种加密方式。但是，如果客户端不想用内置的两种加密，偏偏想用 RSA 算法呢？难道每加一种算法，sharding-jdbc 就要发个版本么

sharding-jdbc 可不会这么干，首先提供出 `Encryptor` 加密接口，并引入 SPI 的机制，做到服务接口与服务实现分离的效果。如果客户端想要使用新的加密算法，只需要在**客户端项目 `META-INF/services` 目录下定义接口的全限定名称文件，并在文件内写上加密实现类的全限定名**，就像这样式的

![image-20220320143007820](.images/image-20220320143007820.png)

通过 SPI 的方式，就可以将客户端提供的加密算法加载到 sharding-jdbc 加密规则中，这样就可以在项目运行中选择自定义算法来对数据进行加密存储



## 为什么要放在services下，还要是当前接口的全路径？

这就用到了我们学java的时候，用到的类加载机制。



```java
public final class ServiceLoader<S>
    implements Iterable<S>
{

    private static final String PREFIX = "META-INF/services/";

}
```

