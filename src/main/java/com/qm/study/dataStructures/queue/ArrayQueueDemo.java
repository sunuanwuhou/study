package com.qm.study.dataStructures.queue;

/**
 * 数组模拟队列
 *
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/6/20 19:59
 */
class arrayQueueDemo {

    public static void main(String[] args) {
        arrayQueue arrayQueue = new arrayQueue(3);


    }

}


class arrayQueue {


    final int[] items;

    int first;

    int end;

    int count;

    public arrayQueue(int capacity) {
        this.items = new int[capacity];
        count = capacity;
        first = -1; //队列头前一个位置
        end = -1;//队列最后一个数据
    }

    public boolean isFull(){
        return end == count - 1;
    }


    public boolean isEmpty(){
        return first == end;
    }

    public void addQueue(int n){
        if(isFull()){
            return;
        }
        end++;
        items[end] = n;
    }

    public int getQueue(){
        if(isEmpty()){
            throw new RuntimeException("");
        }
        first++;
        return items[first];
    }


}



