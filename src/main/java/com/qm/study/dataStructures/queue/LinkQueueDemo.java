package com.qm.study.dataStructures.queue;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/6/26 10:32
 */
public class LinkQueueDemo {


    static class LinkQueue {
        Node first;
        Node last;
        //最大
        int capacity;
        //目前
        int count;


        public void create(int num) {
            if(count>capacity){
                return;
            }

            linkLast(new Node(num));
        }

        public void linkLast(Node node){

            if(first==null){
                first = node;
                last = node;
            }else {
                last = node;
            }
            count++;
        }




    }

    static class Node {

        int order;
        Node next;

        public Node() {
        }

        public Node(int order) {
            this.order = order;
        }
    }
}
