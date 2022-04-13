package com.qm.study.spring.ThreeLevelCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *  测试三级缓存AOP问题  AbstractAutowireCapableBeanFactory
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2021/10/12 13:40
 */
@Service
public class TestService1{
    @Autowired
    private TestService2 testService2;

    public void test1() {


    }
}
