package com.qm.study.leetCode;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2021/11/9 11:47
 */
public class 环形链表 {



    public boolean hasCycle(ListNode head) {
        if (null == head) {
            return false;
        }
        ListNode p1 = head;
        ListNode p2 = head;
        while (p1 != null&&p2.next!=null) {
            p1 = p1.next;
            p2 = p2.next.next;
            if (p1 == p2) {
                return  true;
            }
        }
        return false;
    }
}
