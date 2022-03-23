package com.qm.study.java.Thread.CompletableFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/1/4 22:09
 */
public class CompletableFutureTest {

    // public static void main(String[] args) throws Exception {
    //     // 创建异步执行任务:
    //     CompletableFuture<Double> cf = CompletableFuture.supplyAsync(CompletableFutureTest::fetchPrice);
    //     // 如果执行成功:
    //     cf.thenAccept((result) -> {
    //         System.out.println("price: " + result);
    //     });
    //     // 如果执行异常:
    //     cf.exceptionally((e) -> {
    //         e.printStackTrace();
    //         return null;
    //     });
    //     // 主线程不要立刻结束，否则CompletableFuture默认使用的线程池会立刻关闭:
    //     Thread.sleep(200);
    // }
    //
    // static Double fetchPrice() {
    //     try {
    //         Thread.sleep(100);
    //     } catch (InterruptedException e) {
    //     }
    //     if (Math.random() < 0.3) {
    //         throw new RuntimeException("fetch price failed!");
    //     }
    //     return 5 + Math.random() * 20;
    // }

    public static void main(String[] args) {
        //第一个异步任务，休眠2秒，保证它执行晚点
        CompletableFuture<String> first = CompletableFuture.supplyAsync(() -> {
            try {

                Thread.sleep(2000L);
                System.out.println("执行完第一个异步任务");
            } catch (Exception e) {
                return "第一个任务异常";
            }
            return "第一个异步任务";
        });
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CompletableFuture<Void> future = CompletableFuture
                //第二个异步任务
                .supplyAsync(() -> {
                            // System.out.println("执行完第二个任务");
                            return "第二个任务";
                        }
                        , executor)
                //第三个任务
                .acceptEitherAsync(first,(s)->{
                    // System.out.println(s+);;
                }, executor);

        executor.shutdown();
    }
}
