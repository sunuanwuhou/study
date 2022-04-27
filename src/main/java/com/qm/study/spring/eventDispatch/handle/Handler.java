package com.qm.study.spring.eventDispatch.handle;

import com.qm.study.spring.eventDispatch.event.Event;

public interface Handler<E extends Event> {

    void onEvent(E event);
}