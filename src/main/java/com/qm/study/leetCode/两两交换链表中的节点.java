package com.qm.study.leetCode;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/11/26 16:00
 */
public class 两两交换链表中的节点 {


    public static void main(String[] args) {

        ListNode listNode = new ListNode(1);
        ListNode listNode2 = new ListNode(2);
        ListNode listNode3 = new ListNode(3);
        ListNode listNode4 = new ListNode(4);

        listNode.next = listNode2;
        listNode2.next = listNode3;
        listNode3.next = listNode4;

        ListNode.list(swapPairs(listNode));

    }

    public static ListNode swapPairs(ListNode head) {

        if (null == head) {
            return null;
        }
        if (null == head.next) {
            return head;
        }

        ListNode dummy = new ListNode(0);
        dummy.next = head;

        ListNode temp = dummy;

        if (temp.next != null && temp.next.next != null) {
            //当前节点
            ListNode cur = temp.next;
            //下一个节点
            ListNode next = temp.next.next;

            //头节点的下一个是 next
            temp.next = next;
            //下一个节点 的next指向当前
            cur.next = next.next;
            next.next = cur;
            temp = cur;
        }
        return dummy.next;
    }
}
