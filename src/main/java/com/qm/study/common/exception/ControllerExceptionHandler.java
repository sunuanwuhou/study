package com.qm.study.common.exception;

import com.qm.study.common.BaseResponse;
import com.qm.study.common.CodeEnum.CodeEnum;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
public class ControllerExceptionHandler {


    @ExceptionHandler(BizException.class)
    @ResponseBody
    public BaseResponse<Void> handler(BizException e) {
        System.out.println("进入业务异常"+e.getRetCode()+e.getRetMessage());
        return BaseResponse.fail(CodeEnum.ERROR.getCode(), CodeEnum.ERROR.getMessage());
    }

}

