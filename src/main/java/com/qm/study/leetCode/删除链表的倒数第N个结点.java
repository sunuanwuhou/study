package com.qm.study.leetCode;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/11/9 8:05
 */
public class 删除链表的倒数第N个结点 {

    public ListNode removeNthFromEnd(ListNode head, int n) {
        if (null == head) {
            return null;
        }
        ListNode p = getKthFromEnd(head,n+1);
        p.next = p.next.next;
        return head;
    }

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
