package com.qm.study.common.CodeEnum;

public enum CodeEnum {

    /**操作成功**/
    SUCCESS("0000","操作成功"),
    /**操作失败**/
    ERROR("9999","操作失败"),;

    /**
     * 自定义状态码
     **/
    private String code;
    /**自定义描述**/
    private String message;

    CodeEnum(String code, String message){
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
}