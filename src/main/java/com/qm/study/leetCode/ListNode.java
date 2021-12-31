package com.qm.study.leetCode;

class ListNode {

    int val;
    ListNode next;

    public ListNode(int val) {
        this.val = val;
    }


    public static void list(ListNode node) {
        while (node != null) {
            System.out.println(node.val);
            node = node.next;
        }
    }
}