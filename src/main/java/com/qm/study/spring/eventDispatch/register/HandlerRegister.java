package com.qm.study.spring.eventDispatch.register;

import com.google.common.collect.Maps;
import com.qm.study.spring.eventDispatch.event.Event;
import com.qm.study.spring.eventDispatch.handle.Handler;

import java.util.Map;

/**
 * @version 1.0
 */
public class HandlerRegister {

    private final Map<Class<? extends Event>, Handler> handlerMap = Maps.newHashMap();


    /**
     *  注册 event和handler
     * @param event
     * @param handler
     */
    public void registerHandler(Class<? extends Event> event,Handler handler) {
        handlerMap.put(event, handler);
    }

    /**
     * 根据事件获取对应handler
     * @param event
     * @return
     */
    public Handler getHandler(Class<? extends Event> event) {
        return handlerMap.get(event);
    }

}
