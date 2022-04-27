package com.qm.study.spring.eventDispatch.event;

/**
 * @version 1.0
 */
public class ExceptionEvent extends AbstractEvent {


    /**
     * 需要包装的对象
     */
    private Object object;

    public ExceptionEvent(Object object) {
        this.object = object;
    }
}
