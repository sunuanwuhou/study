package com.qm.study.spring.eventDispatch.handle;

import com.qm.study.spring.eventDispatch.event.Event;


/**
 * 使用一些模板方法来控制方法执行
 *
 * @param <E>
 */
public abstract class AbstractHandler<E extends Event> implements Handler {


    /**
     * 执行前
     * @param event
     */
    void beforeHandle(E event) {
    }

    /**
     * 执行后
     * @param event
     */
    void afterHandle(E event) {
    }

    /**
     * 子类需要实现的党法
     *
     * @param event
     */
    abstract void Handle(E event);


    public void onEvent(Event event) {
        E ev = (E) event;
        beforeHandle(ev);
        try {
            Handle(ev);
        } catch (Exception e) {
            //失败处理
        }
        afterHandle(ev);
    }
}