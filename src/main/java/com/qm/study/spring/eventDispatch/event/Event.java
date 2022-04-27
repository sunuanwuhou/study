package com.qm.study.spring.eventDispatch.event;


public interface Event {

    Class<? extends Event> getType();
}