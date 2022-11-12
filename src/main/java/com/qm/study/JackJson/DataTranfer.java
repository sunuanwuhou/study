package com.qm.study.JackJson;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/11/12 11:49
 */
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = DataTranferSerializer.class)
public @interface DataTranfer {


}
