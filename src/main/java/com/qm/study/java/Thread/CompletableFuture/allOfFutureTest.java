package com.qm.study.java.Thread.CompletableFuture;

import java.util.concurrent.CompletableFuture;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/1/4 22:09
 */
public class allOfFutureTest {


    public static void main(String[] args) {

        CompletableFuture<Void> a = CompletableFuture.runAsync(() -> {
            System.out.println("我执行完了");
        });
        CompletableFuture<Void> b = CompletableFuture.runAsync(() -> {
            System.out.println("我也执行完了");
        });
        CompletableFuture<Void> allOfFuture = CompletableFuture.allOf(a, b).whenComplete((m, k) -> {
            System.out.println("finish");
        });

    }
}
