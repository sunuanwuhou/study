package com.qm.study.dataStructures.recursion;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/6/30 11:07
 */
public class RecursionDemo {

    public static void main(String[] args) {

        // test(4);

        System.out.println(factorial(9));

    }

    public static void test(int n) {
        if (n > 2) {
            test(n - 1);
        }
        System.out.println("n=" + n);
    }

    public static int factorial(int n) {
        if (n == 1) {
            return 1;
        } else {
            return factorial(n - 1) * n;
        }
    }

}
