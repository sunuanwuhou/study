package com.qm.study.dataStructures.linkList;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/6/24 22:07
 */
public class JosephuDemo {


    public static void main(String[] args) {


        CircleSingleLinkList circleSingleLinkList = new CircleSingleLinkList();
        circleSingleLinkList.addNode(5);
        // circleSingleLinkList.list();

        circleSingleLinkList.remove(1, 2,1);


    }


    static class CircleSingleLinkList {

        public CircleSingleLinkList() {
        }

        Node first = null;

        public void addNode(int num) {

            Node temp = null;
            for (int i = 1; i <= num; i++) {
                Node node = new Node(i);
                if (1 == i) {
                    first = node;
                    first.next = first;
                    temp = first;
                } else {
                    temp.next = node;
                    node.next = first;
                    temp = node;
                }
            }
        }


        public void list() {
            if (first == null) {
                return;
            }

            Node temp = first;

            while (temp.next != first) {
                System.out.println(temp.order);
                temp = temp.next;
            }
        }


        /**
         * @date 2021-06-25
         * @param k     编号为k的人开始报数
         * @param m     数到m的人出列
         * @param start   开始的人报数从几开始
         */
        public void remove(int k, int m,int start) {
            if (first == null) {
                return;
            }
            Node tail = first;
            while (tail.next!=first){
                tail = tail.next;
            }

            for(int i=0;i<k-1;i++ ){
                first = first.next;
                tail = tail.next;
            }
            while (tail!=first){
                for(int i=0;i<m-1;i++ ){
                    first = first.next;
                    tail = tail.next;
                }
                System.out.println(first.order);
                first = first.next;
                tail.next = first;
            }
            System.out.println(first.order);
        }

    }


    static class Node {
        int order;
        Node next;

        public Node(int order) {
            this.order = order;
        }

    }

}
