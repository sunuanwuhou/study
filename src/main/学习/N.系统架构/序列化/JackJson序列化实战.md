# Table of Contents

* [前提知识](#前提知识)
* [修改字段名](#修改字段名)
* [实现JsonSerializer /JsonDeserializer](#实现jsonserializer-jsondeserializer)
* [自定义序列化注解](#自定义序列化注解)
  * [具体实现](#具体实现)
  * [代码](#代码)
* [自定义Mapper](#自定义mapper)


大家都知道，后端在输出数据的时候会**序列化**，接受数据的时候会**反序列**的时候，我们可以利用序列化做自己想做的事情。



# 前提知识

+ @jsonProperty是 Jackson的包，而@jsonfield是fastjson的包

+ 序列化框架：JackJson

+ 当前对象

```java
public class JackJsonDemo {

    private String name;

    private String sex;

    private int age;
}
```

# 修改字段名

```java
 @JsonProperty("Sex")
{"name":"name","age":18,"Sex":"sex"}

{"name":"name","age":18,"性别":"sex"}

```





# 实现JsonSerializer /JsonDeserializer

```java
public class DefaultTimeSerializer extends JsonSerializer<Date> {
```



```java
/**
 * 日期(精确到月)
 */
@JsonSerialize(using = DefaultTimeSerializer.class)
private Date Time;
```





# 自定义序列化注解

```java
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = DataTranferSerializer.class)
public @interface DataTranferSerializer {
}
```

## 具体实现

+ 思路：获取注解上的信息。进行序列化转换。
+ 实现效果: 根据原字段上注解信息，**修改原字段或者添加一个字段**

+ 场景：
  + 枚举值转换:参考mybatis Puls 的 mybatisEnumTypeHandler(拿到枚举值，进行反射转换)
  + 时间转换
  + 数据加密

## 代码

```java
package com.qm.study.JackJson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.Map;

/**
 * @version 1.0
 */
public class DataTranferSerializer extends JsonSerializer<Object>  implements ContextualSerializer {


    //存储字段上的注解信息
    private Map<String,Object> paramMap;


    /**
     *  具体序列化方法
     * @param value  当前值
     * @param jsonGenerator
     * @param serializerProvider 序列化方式
     * @throws IOException
     */
    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        //修改 原来字段值
        // jsonGenerator.writeString (value+"1");
        // jsonGenerator.writeObject(value);

        //一定要先給原来的字段，赋值。 才能在写入原值的条件上，追加字段，不然会报错，具体查看源码

        //做其他事情，得到新值
        Object newValue = null;
        //写入新值
        jsonGenerator.writeObjectField("newValue", newValue);

    }


    /**
     * ContextualSerializer是 Jackson 提供的另一个序列化相关的接口，它的作用是通过字段已知的上下文信息定制JsonSerializer，只需要实现createContextual方法即可：
     *
     * createContextual方法只会在第一次序列化字段时调用（因为字段的上下文信息在运行期不会改变），所以不用担心影响性能。
     * @param serializerProvider
     * @param beanProperty
     * @return
     * @throws JsonMappingException
     */
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        if (ObjectUtils.isEmpty(beanProperty)) {
            return serializerProvider.findNullValueSerializer(null);
        }
        DataTranfer annotation = beanProperty.getAnnotation(DataTranfer.class);
        //将注解上的信息 存放在当前类中
        // paramMap.put("", annotation.getClass());

        return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
    }
}

```



# 自定义Mapper

由于复杂的业务需求，我们可能会需要定制化自己的序列化配置。

以下是一些使用场景

```java
ObjectMapper objectMapper = new ObjectMapper();
//放弃使用注解 就是说即使你bean上加了如@JsonProperty 等注解， 也不会生效
objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
//序列化为 user_name 格式，也支持其他格式 具体详情见 PropertyNamingStrategy 配置类
//如果使用注解就是
//@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class) 作用于类上
objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

```

