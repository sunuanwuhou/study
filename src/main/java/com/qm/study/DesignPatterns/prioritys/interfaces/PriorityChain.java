package com.qm.study.DesignPatterns.prioritys.interfaces;

import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author 01399578
 * @version 1.0
 */
public class PriorityChain<T> extends Priority<T>{

    private List<Priority<T>> priorityChainList = Lists.newArrayList();

    private int index = 0;

    public PriorityChain(T param) {
        super(param);
    }

    public PriorityChain() {
        super(null);
    }

    public PriorityChain<T> addPriorityChain(Priority<T> priority){
        priorityChainList.add(priority);
        return this;
    }

    @Override
    public T doFilter(List<T> dataList, PriorityChain<T> priorityChain) {
        if(index==priorityChainList.size()|| CollectionUtils.isEmpty(dataList)){
            return null;
        }
        Priority<T> priority = priorityChainList.get(index);
        return priority.doFilter(dataList,priorityChain);
    }
}
