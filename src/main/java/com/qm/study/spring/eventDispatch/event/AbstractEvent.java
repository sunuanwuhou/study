package com.qm.study.spring.eventDispatch.event;

public abstract class AbstractEvent implements Event {

    public Class<? extends Event> getType() {
        return getClass();
    }

}