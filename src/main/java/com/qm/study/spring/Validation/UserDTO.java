package com.qm.study.spring.Validation;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/5/30 21:22
 */
@Setter
@Getter
public class UserDTO {

    @NotNull(message = "用户名不能为空")
    private String userName;

    @NotNull(message = "年龄为空")
    @Max(value = 99)
    private Integer age;


    /**
     * 校验方法
     */
    public void check() {
        Assert.isTrue(StringUtils.isEmpty(userName), "用户名字必填");
        Assert.isTrue(ObjectUtils.isEmpty(age), "年龄必填");
    }
}
