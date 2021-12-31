package com.qm.study.Jvm;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/9/13 22:29
 */
public class DynamicTest {

    private B b;

    public void funcA() {
        b.funcB();
    }


    public abstract class B {
        public int funcB() {
            return 1;
        }
    }

    public static void main(String[] args) {
        DynamicTest dynamicTest = new DynamicTest();
        dynamicTest.funcA();
    }
}
