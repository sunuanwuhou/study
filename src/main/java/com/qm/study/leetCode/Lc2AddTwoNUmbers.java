package com.qm.study.leetCode;

/**
 * 2数之加
 *
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2021/7/5 14:36
 */
public class Lc2AddTwoNUmbers {

    static ListNode listNode=new ListNode(9);
    static ListNode listNode2=new ListNode(2);


    public static void main(String[] args) {
        //789+32=821

        create();
        list(add(listNode, listNode2));

    }

    public static  void list(ListNode listNode){

        ListNode temp = listNode;
        while (null!=temp){
            System.out.println(temp.val);
            temp = temp.next;
        }

    }



    private static ListNode add(ListNode listNode1, ListNode listNode2) {
        ListNode listNode = new ListNode();

        ListNode temp = listNode;

        Integer carry = 0;


        while (null!=listNode1 || null!=listNode2){
            int x = listNode1 == null ? 0 : listNode1.val;
            int y = listNode2 == null ? 0 : listNode2.val;
            int sum = x + y + carry;

            carry = sum / 10;
            sum = sum % 10;
            temp.next = new ListNode(sum);

            temp = temp.next;
            if(listNode1 != null)
                listNode1 = listNode1.next;
            if(listNode2 != null)
                listNode2 = listNode2.next;

        }
        if(carry>0) {
            temp.next = new ListNode(carry);
        }
        return listNode.next;

    }


    private static void create() {
        listNode.next = new ListNode(8);
        listNode.next.next = new ListNode(7);

        listNode2.next = new ListNode(3);
    }


    static class ListNode {
        Integer val;
        ListNode next;

        public ListNode() {
        }

        public ListNode(int val) {
            this.val = val;
        }
    }


}
