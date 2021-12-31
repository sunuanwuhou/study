package com.qm.study.leetCode;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/11/9 7:35
 */
public class 单链表的倒数第K个点 {


    public ListNode getKthFromEnd(ListNode head, int k) {
        if (null == head) {
            return null;
        }
        ListNode p1 = head;
        while (p1.val <= k && null != p1.next) {
            p1 = p1.next;
        }
        ListNode p2 = head;
        while (null != p1.next) {
            p1 = p1.next;
            p2 = p2.next;
        }
        return p2;
    }



}