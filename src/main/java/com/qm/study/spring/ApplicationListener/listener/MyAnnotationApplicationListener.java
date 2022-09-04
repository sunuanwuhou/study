package com.qm.study.spring.ApplicationListener.listener;

import com.qm.study.spring.ApplicationListener.event.MyApplicationEvent;
import org.springframework.stereotype.Component;

@Component
public class MyAnnotationApplicationListener {

    // @EventListener(classes = MyApplicationEvent.class)
    public void myApplicationEventListener(MyApplicationEvent event) {
        System.out.println("使用注解的方式, 收到事件: " + event.getSource());
    }
}