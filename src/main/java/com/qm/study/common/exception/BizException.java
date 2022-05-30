package com.qm.study.common.exception;

import lombok.Data;

@Data
public class BizException extends RuntimeException {

    private String retCode;

    private String retMessage;

    public BizException() {
        super();
    }

    public BizException(String retCode, String retMessage) {
        this.retCode = retCode;
        this.retMessage = retMessage;
    }

    public String getRetCode() {
        return retCode;
    }

    public String getRetMessage() {
        return retMessage;
    }
}