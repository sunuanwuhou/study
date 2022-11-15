package com.qm.study.JackJson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

/**
 * @author 01399578
 * @version 1.0
 */
public class JackJsonDemo {

    private String name;

   @DataTranfer
    private String sex;

    private int age;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public static void main(String[] args) throws JsonProcessingException {
        JackJsonDemo jackJsonDemo = new JackJsonDemo();
        jackJsonDemo.setName("name");
        jackJsonDemo.setSex("sex");
        jackJsonDemo.setAge(18);
        ObjectMapper objectMapper = new ObjectMapper();
        //放弃使用注解
        objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        String jsonStr = objectMapper.writeValueAsString(jackJsonDemo);
        System.out.println(jsonStr);
    }
}
