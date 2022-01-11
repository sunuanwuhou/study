package com.qm.study.log;

import com.mzt.logapi.starter.annotation.LogRecordAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("log")
    @LogRecordAnnotation(success = "{{#order.purchaseName}}下了一个订单,购买商品「{{#order.productName}}」,下单结果:{{#_ret}}",
            prefix = "test", bizNo = "{{#order.orderNo}}")
    public  void logTest(@RequestParam(value = "test") String test){
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
