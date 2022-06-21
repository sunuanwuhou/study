package com.qm.study.DesignPatterns.prioritys.context;

import com.google.common.collect.Lists;
import com.qm.study.DesignPatterns.prioritys.impl.Priority11;
import com.qm.study.DesignPatterns.prioritys.interfaces.PriorityChain;
import com.qm.study.java.AQS.Test;

import java.util.List;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/6/21 20:53
 */
public class PriorityContext {


    public static PriorityChain<Test> create(Test test){
        return new PriorityChain<Test>().addPriorityChain(new Priority11(test));
    }


    public static void main(String[] args) {
        List<Test> dataList = Lists.newArrayList();
        Test test = new Test();
        PriorityChain<Test> testPriorityChain = PriorityContext.create(test);
        testPriorityChain.doFilter(dataList,testPriorityChain);
    }

}
