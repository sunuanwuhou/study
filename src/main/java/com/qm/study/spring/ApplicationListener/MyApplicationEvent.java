package com.qm.study.spring.ApplicationListener;

import org.springframework.context.ApplicationEvent;

public class MyApplicationEvent extends ApplicationEvent {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public MyApplicationEvent(Object source) {
        super(source);
    }
}


