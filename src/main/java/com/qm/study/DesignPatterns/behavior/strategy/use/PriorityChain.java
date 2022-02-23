package com.qm.study.DesignPatterns.behavior.strategy.use;

import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/2/23 21:13
 */
public class PriorityChain<T> extends Priority<T> {


    private List<Priority<T>> priorityChainsList = Lists.newArrayList();

    private int index;

    public PriorityChain(T param) {
        super(param);
    }

    public PriorityChain() {
        super(null);
    }


    /**
     * 添加责任链
     * @param priority
     * @return
     */
    public PriorityChain<T> addPriority(Priority priority){
        priorityChainsList.add(priority);
        return this;
    }


    @Override
    protected T doFilter(List<T> list, PriorityChain<T> priorityChain) {
        if(index==list.size()|| CollectionUtils.isEmpty(list)){
            return null;
        }
        return priorityChainsList.get(index++).doFilter(list,priorityChain);
    }


}
