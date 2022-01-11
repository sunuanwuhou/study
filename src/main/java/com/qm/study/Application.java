package com.qm.study;

import com.mzt.logapi.starter.annotation.EnableLogRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAsync
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class, RedisAutoConfiguration.class})
// @EnableAspectJAutoProxy
@EnableTransactionManagement
@EnableLogRecord(tenant = "com.qm.study")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
