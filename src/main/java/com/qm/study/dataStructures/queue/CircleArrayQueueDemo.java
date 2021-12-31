package com.qm.study.dataStructures.queue;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/6/21 11:07
 */
public class CircleArrayQueueDemo {




}

class   CircleArray{

    final int[] items;

    int first;

    int end;

    int maxSize;


    public CircleArray(int capacity) {
        this.items = new int[capacity];
    }

    public boolean isFull(){
        return (end+1)%maxSize ==first;
    }


    public boolean isEmpty(){
        return first == end;
    }

    public void addQueue(int n){
        if(isFull()){
            return;
        }
        items[end] = n;
        end = (end + 1) % maxSize;
    }

    public int getQueue(){
        if(isEmpty()){
            throw new RuntimeException("");
        }
        int item = items[first];
        first = (first + 1) % maxSize;

        return item;
    }
}
