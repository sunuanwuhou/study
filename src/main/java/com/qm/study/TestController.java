package com.qm.study;

import com.qm.study.spring.mapstruct.Student;
import com.qm.study.spring.mapstruct.Student1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/5/6 22:21
 */
@RestController("test")
@RequestMapping("test1")
public class TestController {


    @Autowired
    private  ConversionService conversionService;


    @GetMapping("test")
    public  void  test(){
        Student student = new Student();
        student.setAge(1);
        student.setName("求");
        Student1 user = conversionService.convert(student, Student1.class);
        System.out.println(user);
    }


}
