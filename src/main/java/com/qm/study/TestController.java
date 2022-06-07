package com.qm.study;

import com.qm.study.common.BaseResponse;
import com.qm.study.spring.ServiceNameConstant;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/5/6 22:21
 */
@RestController("test")
@RequestMapping("test1")
public class TestController {



    @PostMapping("test")
    public BaseResponse test() {

        System.out.println(ServiceNameConstant.ServiceNameUrl);
        return BaseResponse.success(null);
    }


}
