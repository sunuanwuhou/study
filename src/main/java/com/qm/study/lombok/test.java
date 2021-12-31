package com.qm.study.lombok;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2021/12/1 17:57
 */
public class test {

    public static void main(String[] args) {


        List<User>  list=  Lists.newArrayList();

        User.UserBuilder builder = User.builder().age(12).name("13");


        User build = builder.build();

        System.out.println(build.getAge());
    }
}
