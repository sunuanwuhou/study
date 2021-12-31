package com.qm.study.leetCode;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * 难点在于怎么找 每个链表的最小值？ 优先队列 -》堆排序
 *
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/11/6 10:34
 */
public class 合并K个升序链表 {


    public static void main(String[] args) {


        Node node1 = new Node(1);
        node1.next = new Node(2);
        node1.next.next = new Node(6);

        Node node2 = new Node(3);
        node2.next = new Node(4);
        node2.next.next = new Node(5);

        List<Node> listNode = new ArrayList<>();
        listNode.add(node1);
        listNode.add(node2);

        Node node = merge(listNode);
        Node head = node;
        while (head!=null){
            System.out.println(head.val);
            head = head.next;
        }

    }

    public static Node merge(List<Node> listNode) {
        if (null == listNode) {
            return null;
        }
        Node node = new Node(-1);
        Node p =node;
        //优先队列 从小到大
        PriorityQueue<Node> queue = new PriorityQueue<>((a, b) -> a.val - b.val);

        for (Node node1 : listNode) {
            if(null!=node1){
                queue.add(node1);
            }
        }

        while (!queue.isEmpty()){
            Node head = queue.poll();
            p.next = head;
            if(null!=head.next){
                queue.add(head.next);
            }
            p = p.next;
        }


        return node;

    }


    static class Node {

        int val;
        Node prev;
        Node next;

        public Node() {
        }

        public Node(int val) {
            this.val = val;
        }
    }
}
