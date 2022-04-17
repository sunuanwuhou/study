package com.qm.study.spring.SpringAop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/4/17 19:43
 */
@Service
public class TestService {

    @Autowired
    private ApplicationContext  applicationContext;


    public void save(final Object user) {

        TestService testService = applicationContext.getBean(TestService.class);

    }


}
