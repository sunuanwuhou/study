package com.qm.study.DesignPatterns.behavior.prioritys.impl;

import com.qm.study.DesignPatterns.behavior.prioritys.interfaces.Priority;
import com.qm.study.DesignPatterns.behavior.prioritys.interfaces.PriorityChain;
import com.qm.study.java.AQS.Test;

import java.util.List;

/**
 * @version 1.0
 */
public  class Priority11 extends Priority<Test> {


    public Priority11(Test param) {
        super(param);
    }

    @Override
    public Test doFilter(List<Test> dataList, PriorityChain<Test> priorityChain) {
        return null;
    }
}


