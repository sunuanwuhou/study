package com.qm.study.spring.ApplicationListener.listener;

import com.qm.study.spring.ApplicationListener.event.MyApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MyApplicationListener implements ApplicationListener<MyApplicationEvent> {

    @Async("myThread")
    @Override
    public void onApplicationEvent(MyApplicationEvent event) {
        System.out.println("MyApplicationListener 收到消息: " + event.getSource());
    }
}