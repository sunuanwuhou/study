package com.qm.study.leetCode;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/11/6 10:34
 */
public class 合并2个有序链表 {


    public static void main(String[] args) {


        Node node1 = new Node(1);
        node1.next = new Node(2);
        node1.next.next = new Node(3);

        Node node2 = new Node(3);
        node2.next = new Node(4);
        node2.next.next = new Node(5);

        Node node = merge(node1, node2);
        Node head = node;

        while (head!=null){
            System.out.println(head.val);
            head = head.next;

        }


    }

    public static Node merge(Node listNode1, Node listNode2) {
        if (null == listNode1) {
            return listNode2;
        }
        if (listNode2 == listNode1) {
            return listNode1;
        }

        Node newNode = new Node();
        Node p = newNode;


        Node p1 = listNode1;
        Node p2 = listNode2;

        while (p1 != null && p2 != null) {
            if (p1.val < p2.val) {
                p.next = p1;
                p1 = p1.next;
            } else {
                p.next = p2;
                p2 = p2.next;
            }
            p = p.next;
        }

        if (p1 != null) {
            p.next = p1;
        }

        if (p2 != null) {
            p.next = p2;
        }

        return newNode.next;

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
