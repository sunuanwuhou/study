# Table of Contents

* [引入Maven](#引入maven)
* [编写feign接口](#编写feign接口)
* [开启调用日志](#开启调用日志)
* [配置相关](#配置相关)
* [FeignClient设置动态Url](#feignclient设置动态url)
* [调用方法](#调用方法)
* [设置超时时间](#设置超时时间)






# 引入Maven

```java
 <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
            <version>2.1.0.RELEASE</version>
        </dependency>
    <!-- feign自带的hystrix并不是启动依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
            <version>2.1.0.RELEASE</version>
        </dependency>
```



# 编写feign接口

```java
@FeignClient(name = "serviceConfig", url = "${serviceConfig.url}", configuration = FeignLogConfig.class)
public interface ServiceConfigClient {


    @PostMapping("/aresConfig/plateCase/getMultipleBoxLoad")
    Response<List<PlaneBoxTypeDto>> getMultipleBoxLoad(@RequestBody  List<PlaneBoxTypeDto> planeBoxTypeDtoList);

}
```



# 开启调用日志

```java
@Configuration
public class FeignLogConfig {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
```



# 配置相关

```
1.启动类加上@EnableFeignClients
2.默认是application.properties 需要配置${serviceConfig.url}



```



**FeignLogConfig 日志级别要制定DEBUG**

```xml
# 指定feiginClient
logging.level.com.sf.ares.rpc=DEBUG
```



# FeignClient设置动态Url



+ feign的接口方法支持动态指定URI，因此我们只需在传递的参数方法中添家URI参数即可

```java
@FeignClient(name = "energyCloudAdminFeignClient", url = "${energy.cloud.url}")
public interface EnergyCloudAdminFeignClient {
	@GetMapping("/sys/user/getCurrUser")
	JSONObject getCurrUser(URI uri, @RequestHeader MultiValueMap<String, String> headers, @RequestParam Map<String, String> bodies);
}

//传入URL
 serviceConfigClient.getMultipleBoxLoad(new URI("Url"),planeBoxTypeDtoList);

```



+ 推荐使用这种，使用接口定义服务名称以及url(**推荐使用**)


  ```java
  public interface ServiceNameConstant {
  
      public static final String ServiceName = "service_name";
        //spring 会自动从配置文件中找这个变量
      public static final String ServiceNameUrl = "${service.name.url}";
  }
  
  ```

  

  ```java
  @FeignClient(name = ServiceNameConstant.ServiceName, url = ServiceNameConstant.ServiceNameUrl)
  public interface EnergyCloudAdminFeignClient {
  	@GetMapping("/sys/user/getCurrUser")
  	JSONObject getCurrUser(URI uri, @RequestHeader MultiValueMap<String, String> headers, @RequestParam Map<String, String> bodies);
  }
  ```

  



+ 也可以从指定配置文件中读取当前feign所需的配置 （**不起作用**）

```java
# 调用service-config路径地址
spring.cloud.discovery.client.simple.instances.service-config[0].uri=http://ares-service-config.intsit.sfcloud.local:1080/

@FeignClient(name = FeignClientName.SERVICE_CONFIG, configuration = FeignLogConfig.class)
public interface ServiceConfigClient {
}
```





# 调用方法

1. 引入feiginClient，直接调就可以了。



# 设置超时时间

+ feign 设置单个接口超时时间：https://www.jianshu.com/p/809370702760

+ 全局设置：

  ```java
  # OpenFeign请求连接超时时间
  feign.client.config.default.connectTimeout=3000
  # OpenFeign请求处理超时时间
  feign.client.config.default.readTimeout=10000
  # OpenFeign启用HttpClient
  feign.client.httpclient.enabled=true
  
  #指定服务
  feign.client.config.sendInfoClient.connectTimeout=3000
  ```

  

