



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



feign的接口方法支持动态指定URI，因此我们只需在传递的参数方法中添家URI参数即可

```java
@FeignClient(name = "energyCloudAdminFeignClient", url = "${energy.cloud.url}")
public interface EnergyCloudAdminFeignClient {
	@GetMapping("/sys/user/getCurrUser")
	JSONObject getCurrUser(URI uri, @RequestHeader MultiValueMap<String, String> headers, @RequestParam Map<String, String> bodies);
}

//传入URL
 serviceConfigClient.getMultipleBoxLoad(new URI("Url"),planeBoxTypeDtoList);

```



+ 也可以从指定配置文件中读取当前feign所需的配置 （不起作用）

```java
# 调用service-config路径地址
spring.cloud.discovery.client.simple.instances.service-config[0].uri=http://ares-service-config.intsit.sfcloud.local:1080/

@FeignClient(name = FeignClientName.SERVICE_CONFIG, configuration = FeignLogConfig.class)
public interface ServiceConfigClient {
}
```





# 调用方法

1. 引入feiginClient，直接调就可以了。