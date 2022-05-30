package com.qm.study.common;

import com.qm.study.common.CodeEnum.CodeEnum;

/**
 * @param <T>
 */
public class BaseResponse<T> {

    /**
     * 响应状态码（0000表示成功，9999表示失败
     */
    private String code;

    /**
     * 响应结果描述
     */
    private String message;

    /**
     * 返回的数据
     */
    private T data;

    /**
     * 成功返回
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data) {
        BaseResponse<T> response= new BaseResponse<>();
        response.setCode(CodeEnum.SUCCESS.getCode());
        response.setMessage(CodeEnum.SUCCESS.getMessage());
        response.setData(data);
        return response;
    }

    /**
     *  失败返回
     * @param code
     * @param message
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> fail(String code, String message) {
        BaseResponse<T> response = new BaseResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}