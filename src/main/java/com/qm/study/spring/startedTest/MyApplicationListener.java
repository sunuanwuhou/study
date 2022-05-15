package com.qm.study.spring.startedTest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class MyApplicationListener implements ApplicationListener<ApplicationReadyEvent> {

     private static final Logger logger= LoggerFactory.getLogger(MyApplicationListener.class);
	@Override
	public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        logger.info("MyApplicationListener is started up");
	}
}