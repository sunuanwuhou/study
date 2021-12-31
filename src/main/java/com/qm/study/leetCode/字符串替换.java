package com.qm.study.leetCode;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/10/31 19:11
 */
public class 字符串替换 {


    public static final char EMPTY=' ';



    public static void main(String[] args) {

        String oldStr = "We are happy";
        int oldLength = oldStr.length();

        int num = 0;
        for (char c : oldStr.toCharArray()) {
            if (c == EMPTY) {
                num = num + 1;
            }
        }

        char[] newStr = new char[oldLength + num];


        for (char c : oldStr.toCharArray()) {
            if (c == EMPTY) {

            }else {

            }
        }


    }

}
