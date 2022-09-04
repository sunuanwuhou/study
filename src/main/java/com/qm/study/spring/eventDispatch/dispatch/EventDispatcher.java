package com.qm.study.spring.eventDispatch.dispatch;


import com.qm.study.spring.eventDispatch.event.Event;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/4/27 20:25
 */
public interface EventDispatcher {


    void dispatch(Event event);

}
