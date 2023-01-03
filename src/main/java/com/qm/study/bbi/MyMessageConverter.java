// package com.qm.study.bbi;
//
// import com.just.springmvc4.domain.DemoObj;
// import org.springframework.http.HttpInputMessage;
// import org.springframework.http.HttpOutputMessage;
// import org.springframework.http.MediaType;
// import org.springframework.http.converter.AbstractHttpMessageConverter;
// import org.springframework.http.converter.HttpMessageNotReadableException;
// import org.springframework.http.converter.HttpMessageNotWritableException;
// import org.springframework.util.StreamUtils;
//
// import java.io.IOException;
// import java.nio.charset.Charset;
//
// public class MyMessageConverter extends AbstractHttpMessageConverter<DemoObj> {
//     /**
//      * 定义字符编码，防止乱码
//      */
//     private static final Charset DEFAULT_CHARSET=Charset.forName("UTF-8");
//     /**
//      * 新建自定义的媒体类型
//      */
//     public MyMessageConverter(){
//         super(new MediaType("application","x-wisely",DEFAULT_CHARSET));
//     }
//
//     /**
//      * 表明只处理DemoObj这个类
//      */
//     @Override
//     protected boolean supports(Class<?> aClass) {
//         return DemoObj.class.isAssignableFrom(aClass);
//     }
//
//     /**
//      * 重写readInternal方法，处理请求的数据
//      */
//     @Override
//     protected DemoObj readInternal(Class<? extends DemoObj> aClass, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
//         String temp=StreamUtils.copyToString(httpInputMessage.getBody(),DEFAULT_CHARSET);
//         String[] tempArr=temp.split("-");
//         return new DemoObj(new Long(tempArr[0]),tempArr[1]);
//     }
//
//     /**
//      * 重写writeInternal，处理如何输出数据到response
//      */
//     @Override
//     protected void writeInternal(DemoObj demoObj, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
//         String out="hello: "+demoObj.getId()+"-"+demoObj.getName();
//         StreamUtils.copy(out, DEFAULT_CHARSET, httpOutputMessage.getBody());
//     }
// }