package com.qm.study.lombok;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2021/12/1 17:54
 */
@Builder
@Setter
@Getter
public class User {

    /**
     *
     */
    private String name;

    /**
     *
     */
    private Integer age;



}
