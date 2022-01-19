package com.qm.study.java;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/1/15 12:03
 */
public class JavaTest {


    public static void main(String[] args) throws Exception {

        try {
            for(int i=0;i<=10;i++ ){
                if(i==2){
                    int a = i / 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
