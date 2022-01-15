package com.qm.study.java;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/1/15 12:03
 */
public class JavaTest {


    public static void main(String[] args) throws Exception {
        boolean flag=true;
        if (flag){
            throw new Exception("111");
        }

        Assert.isTrue(!flag,"mesage");

        HashMap<String,Integer >  map = Maps.newHashMap();

        List<Integer> list =  Lists.newArrayList();
    }
}
