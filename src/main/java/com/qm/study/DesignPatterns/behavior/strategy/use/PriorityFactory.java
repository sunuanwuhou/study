package com.qm.study.DesignPatterns.behavior.strategy.use;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/2/23 21:27
 */
public class PriorityFactory {



    public static  PriorityChain<TestParam> creatTestDataChain(TestParam testParam){

        return new PriorityChain<TestParam>()
                .addPriority(new TestProiroty1(testParam))
                .addPriority(new TestProiroty2(testParam));
    }

}
