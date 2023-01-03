package com.qm.study;

import com.qm.study.spring.ApplicationListener.EventPublisher;
import com.qm.study.spring.ApplicationListener.event.MyApplicationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    private EventPublisher eventPublisher;


    @PostMapping("test")
    public void test() throws IllegalAccessException {
        eventPublisher.publishEvent(new MyApplicationEvent("这是一个测试"));

    }

    @GetMapping("test2")
    public String test2() throws IllegalAccessException {
        return "111";
    }
}
