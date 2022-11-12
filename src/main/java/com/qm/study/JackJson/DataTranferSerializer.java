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
