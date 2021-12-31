package com.qm.study.dataStructures.linkList;

import java.util.Stack;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/6/23 9:55
 */
public class StackDemo {

    public static void main(String[] args) {

        Stack<String> stack = new Stack<>();
        stack.add("111");
        stack.add("222");
        stack.add("333");

        while (!stack.isEmpty()){
            System.out.println(stack.pop());
        }

    }
}
