package com.qm.study.spring.eventDispatch.dispatch;

import com.qm.study.spring.eventDispatch.event.Event;
import com.qm.study.spring.eventDispatch.handle.Handler;
import com.qm.study.spring.eventDispatch.register.HandlerRegister;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 */
public class EventDispatcherImpl implements EventDispatcher, InitializingBean {

    @Autowired
    private HandlerRegister handlerRegister;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;


    @Override
    public void dispatch(Event event) {
        Handler handler = handlerRegister.getHandler(event.getType());

        threadPoolExecutor.submit(() -> {
            try {
                handler.onEvent(event);
            } catch (Exception e) {
                //处理失败消息
            }
        });


    }

    @Override
    public void afterPropertiesSet() throws Exception {
        threadPoolExecutor = new ThreadPoolExecutor(10, 10, 10L, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
    }
}
