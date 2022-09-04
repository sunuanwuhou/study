package com.qm.study.spring.ApplicationListener.event;

import org.springframework.context.ApplicationEvent;

public class MyApplicationEvent extends ApplicationEvent {

    public MyApplicationEvent(Object source) {
        super(source);
    }
}


