package com.qm.study.DesignPatterns.behavior.strategy.use;

import java.util.List;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/2/23 21:25
 */
public class TestProiroty1 extends  Priority<TestParam>{


    public TestProiroty1(TestParam param) {
        super(param);
    }

    @Override
    protected TestParam doFilter(List<TestParam> list, PriorityChain<TestParam> priorityChain) {

        return null;
    }
}
