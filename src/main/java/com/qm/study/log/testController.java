package com.qm.study.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2021/12/3 8:26
 */
@RestController
@RequestMapping("test")
public class testController {

    private static final String KEY = "TRACE_ID";
    private static final Logger logger = LoggerFactory.getLogger(testController.class);



    @GetMapping("test")
    public  void get(){
        // 入口传入请求ID
        // MDC.put(KEY, UUID.randomUUID().toString());
        // 打印日志
        logger.info("log in main thread 1");
        logger.info("log in main thread 2");
        logger.info("log in main thread 3");
        // 出口移除请求ID
        // MDC.remove(KEY);
    }

}
