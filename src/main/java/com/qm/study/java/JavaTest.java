package com.qm.study.java;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/1/15 12:03
 */
public class JavaTest {


    public static void main(String[] args) throws Exception {
        String str = "451wt489bc5894";
        System.out.println(str+"-->"+str.replaceAll("(\\d{3})(\\w+)(\\d{3})(\\w+)(\\d{4})","$5/$4+$3+$2+$1"));
    }
}
