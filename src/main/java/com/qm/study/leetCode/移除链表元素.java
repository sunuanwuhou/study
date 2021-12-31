package com.qm.study.leetCode;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2021/11/11 19:24
 */
public class 移除链表元素 {

    public ListNode removeElements(ListNode head, int val) {

        if (null == head) {
            return null;
        }
        ListNode dummy = new ListNode(-1);
        dummy.next = head;
        ListNode p = dummy;
        while (p.next != null) {
            if (val == p.next.val) {
                p.next = p.next.next;
            } else {
                p = p.next;
            }
        }
        return dummy.next;
    }
}
