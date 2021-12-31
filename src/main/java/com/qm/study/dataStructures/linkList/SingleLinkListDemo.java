package com.qm.study.dataStructures.linkList;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/6/21 17:01
 */
public class SingleLinkListDemo {

    public static void main(String[] args) {

        SingleLinkList one = new SingleLinkList();
        one.add(new Node(1, "1"));
        one.add(new Node(2, "2"));
        one.add(new Node(4, "4"));

        SingleLinkList two = new SingleLinkList();
        two.add(new Node(3, "3"));
        two.add(new Node(5, "5"));
        two.add(new Node(7, "7"));


        SingleLinkList merge = one.merge(one.getHead(), two.getHead());

        merge.list();

    }


}

class SingleLinkList {

    public SingleLinkList() {
    }

    public SingleLinkList(Node head) {
        this.head = head;
    }

    private Node head = new Node("");

    private Node tail;

    public void add(Node node) {

        Node temp = head;

        while (true) {
            if (temp.next == null) {
                break;
            }
            temp = temp.next;
        }
        temp.next = node;
    }

    public Node getHead() {
        return head;
    }


    public void add(Node node, int n) {

        Node temp = head;

        while (true) {
            if (temp.next == null) {
                break;
            }
            temp = temp.next;
        }
        temp.next = node;
    }


    public void list() {
        Node temp = head;
        while (true) {

            if (temp.next == null) {
                return;
            }
            temp = temp.next;
            System.out.println(temp);
        }

    }

    public void reverseList(Node headNode) {
        Node current = headNode.next;
        if (current == null) {
            //为空直接返回
            return;
        }
        //定义一个新节点
        Node node = new Node(1, "");
        Node next = null;
        while (current != null) {
            //临时保存下一个节点信息
            next = current.next;
            //正链表当前节点的next执行 反链表的next
            // 也就是将正链表的节点插入 反链表头节点和头节点下一个之间
            current.next = node.next;
            //反链表的下一个节点等于当前接待你
            node.next = current;
            current = next;
        }
        headNode.next = node.next;
    }

    //1.oneNode 定义一个临时变量 twoNode定义一个临时变量
    //2.定义一个新单链表 以及临时变量
    //3.比较 oneNode 和twoNode 放入新链表
    //4.处理oneNode和twoNode 末尾某个链表为空的情况
    public SingleLinkList merge(Node headOne, Node headTwo) {

        Node currentOne = headOne.next;
        Node currentTwo = headTwo.next;
        if (null == currentOne) {
            return new SingleLinkList(headTwo);
        }
        if (null == currentTwo) {
            return new SingleLinkList(headOne);
        }

        Node node = new Node("1");
        Node temp = node;

        while (currentOne != null && currentTwo != null) {
            if (currentOne.order < currentTwo.order) {
                temp.next = currentOne;
                temp = temp.next;
                currentOne = currentOne.next;

            } else {
                temp.next = currentTwo;
                temp = temp.next;
                currentTwo = currentTwo.next;
            }
        }

        if (currentOne == null) {
            while (currentTwo != null) {
                temp.next = new Node(currentTwo.order, currentTwo.name);
                temp = temp.next;
                currentTwo = currentTwo.next;
            }
        } else {
            while (currentOne != null) {
                temp.next = new Node(currentOne.order, currentOne.name);
                temp = temp.next;
                currentOne = currentOne.next;
            }
        }

        return new SingleLinkList(node);
    }


    //将新链表合并到旧链表中
    public SingleLinkList merge(SingleLinkList listOne, SingleLinkList listTwo) {

        Node headOne = listOne.head;
        Node headTwo = listTwo.head;
        if (headOne.next == null) {
            headOne.next = headTwo.next;
            return listOne;
        }
        if (headTwo.next == null) {
            return listOne;
        }

        Node cueerntOne = headOne.next;
        Node cueerntTwo = headTwo.next;
        Node cueerntOneTemp = null;
        Node cueerntTwoTemp = null;

        while (cueerntOne != null && cueerntTwo != null) {
            if (cueerntOne.order < cueerntTwo.order) {
                cueerntOneTemp = cueerntOne;
                cueerntTwoTemp = cueerntTwo;

                cueerntTwo.next = cueerntOne.next;
                cueerntOne.next = cueerntTwo;
                cueerntTwo = cueerntTwo.next;
                cueerntOne = cueerntOneTemp.next;

            }else {
                cueerntTwoTemp = cueerntTwo;
                cueerntTwo.next = cueerntOne;
                cueerntTwo = cueerntTwoTemp.next;
            }
        }

        return listOne;
    }

}


class Node {
    int order;
    String name;
    Node next;

    public Node() {
    }

    public Node(int order) {
        this.order = order;
    }

    public Node(String name) {
        this.name = name;
    }

    public Node(int order, String name) {
        this.order = order;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Node{" +
                "name='" + name + '\'' +
                '}';
    }
}
