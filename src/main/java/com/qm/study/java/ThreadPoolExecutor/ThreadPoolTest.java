package com.qm.study.java.ThreadPoolExecutor;

import org.springframework.scheduling.concurrent.DefaultManagedAwareThreadFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/10/13 21:13
 */
@Service
public class ThreadPoolTest {


    private ThreadPoolExecutor threadPool;

    public ThreadPoolTest() {
        init();
    }

    private void init() {
        threadPool = new ThreadPoolExecutor
                (10, 20, 600L,
                        TimeUnit.SECONDS, new LinkedBlockingQueue<>(4096),
                        new DefaultManagedAwareThreadFactory() {
                        }, new ThreadPoolExecutor.AbortPolicy());
    }

    public void addTask(Runnable runnable) {
        threadPool.execute(runnable);
    }

    public Future addFutureTask(Runnable runnable){
        return threadPool.submit(runnable);
    }


    public static void main(String[] args) {

        int i1 = Thread.activeCount();

        ThreadPoolTest threadPoolTest = new ThreadPoolTest();
        threadPoolTest.init();

        for(int i=0;i<=100;i++ ){
            MyThread myThread = new MyThread();
            threadPoolTest.addTask(myThread);
        }

    }

}
