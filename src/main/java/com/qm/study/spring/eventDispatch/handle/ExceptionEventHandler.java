package com.qm.study.spring.eventDispatch.handle;

import com.qm.study.spring.eventDispatch.event.ExceptionEvent;
import com.qm.study.spring.eventDispatch.register.HandlerRegister;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;


public class ExceptionEventHandler extends AbstractHandler<ExceptionEvent> implements InitializingBean {


    @Autowired
    private HandlerRegister handlerRegister;


    void beforeHandle(ExceptionEvent event) {
    }


    void afterHandle(ExceptionEvent event) {
    }

    /**
     * 子类需要实现的党法
     *
     * @param event
     */
    void Handle(ExceptionEvent event) {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        handlerRegister.registerHandler(ExceptionEvent.class, this);

    }

}