package com.qm.study.java.AQS;

import java.util.concurrent.CyclicBarrier;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2021/10/19 10:01
 */
public class Test {
    public static void main(String[] args) {

        CyclicBarrier cyclicBarrier = new CyclicBarrier(7,()->{
            System.out.println("最后执行的主线程");
        });

        for(int i=1;i<=7;i++ ){

            final  int temp=i;

            new Thread(()->{
                System.out.println("线程"+temp+"执行");
                try {
                    cyclicBarrier.await();
                    System.out.println("线程"+temp+"在主线程执行完毕后执行");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }).start();
        }

    }
}
