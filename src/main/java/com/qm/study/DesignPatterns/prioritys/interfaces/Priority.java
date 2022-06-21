package com.qm.study.DesignPatterns.prioritys.interfaces;

import java.util.List;

/**
 * @version 1.0
 */
public abstract class Priority<T> {

    protected T param;

    public Priority(T param) {
        this.param = param;
    }

    /**
     * 定义过滤方法
     * @param dataList 原始数据
     * @param priorityChain 责任链
     * @return 最终结果
     */
    public abstract T doFilter(List<T> dataList, PriorityChain<T> priorityChain);
}


