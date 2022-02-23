package com.qm.study.DesignPatterns.behavior.strategy.use;

import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/2/23 21:11
 */
public abstract class Priority<T> {


    protected T param;

    public Priority(T param) {
        this.param = param;
    }


    /**
     *  定义过滤方法
     * @param list 数据
     * @param priorityChain 优先级链条
     * @return 过滤后的数据
     */
    protected abstract T doFilter(List<T> list, PriorityChain<T> priorityChain);


    protected  T doFilter(List<T> list, Predicate<T> predicate){
        List<T> filterList = list.stream().filter(predicate).collect(Collectors.toList());
        return CollectionUtils.isEmpty(filterList) ? null : filterList.get(0);
    }


}
