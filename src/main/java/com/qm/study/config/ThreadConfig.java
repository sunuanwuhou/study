package com.qm.study.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/9/4 10:41
 */

@Component
public class ThreadConfig {
    // 线程数
    public static final int THREAD_POOL_SIZE = 16;

    @Bean("myThread")
    ThreadPoolExecutor myThread() {

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("hyn-demo-pool-%d").build();

        ThreadPoolExecutor executor = new ThreadPoolExecutor(THREAD_POOL_SIZE,
                THREAD_POOL_SIZE,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024),
                namedThreadFactory,
                new ThreadPoolExecutor.AbortPolicy());
        return executor;
    }
}
