package com.qm.study.spring.startedTest;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2022/5/14 16:10
 */
@Component
public class StartedTestDemo implements InitializingBean {


    static{
        System.out.println("I'm A static code block");
    }

    public StartedTestDemo() {
        System.out.println("I'm A constructor code block");
    }

    @PostConstruct
    public void testPostConstruct(){
        System.out.println("MyPostConstructBean");
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("MyInitializingBean.afterPropertiesSet()");
    }
}
