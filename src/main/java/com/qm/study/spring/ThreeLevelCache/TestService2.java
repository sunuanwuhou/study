package com.qm.study.spring.ThreeLevelCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2021/10/12 13:41
 */
@Service
public class TestService2 {

    @Autowired
    private TestService1 testService1;

    @Async
    public void test2() {
    }
}
