package com.qm.study;

import com.qm.study.common.BaseResponse;
import com.qm.study.common.utils.Assert;
import com.qm.study.spring.ApplicationListener.EventPublisher;
import com.qm.study.spring.Validation.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/5/6 22:21
 */
@RestController("test")
@RequestMapping("test1")
public class TestController {


    @Autowired
    private EventPublisher eventPublisher;


    @PostMapping("test")
    public BaseResponse test(@Validated UserDTO userDTO, BindingResult result) {

        List<FieldError> fieldErrors = result.getFieldErrors();
        Assert.isTrue(fieldErrors.isEmpty(), fieldErrors.get(0).getDefaultMessage());
        return BaseResponse.success(userDTO);
    }


}
