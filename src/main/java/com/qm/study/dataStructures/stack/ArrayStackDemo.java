package com.qm.study.dataStructures.stack;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/6/25 16:10
 */
public class ArrayStackDemo {


    public static void main(String[] args) {

        // ArrayStack arrayStack = new ArrayStack(5);
        // arrayStack.push(1);
        // arrayStack.push(2);
        // arrayStack.push(3);
        // arrayStack.push(4);
        // arrayStack.push(5);
        //
        // arrayStack.pop();
        // arrayStack.list();

        linkStack linkStack = new linkStack();
        linkStack.push(1);
        linkStack.push(2);
        linkStack.push(3);

        linkStack.pop();
        linkStack.pop();
        linkStack.pop();
        linkStack.list();


    }


    static class ArrayStack {
        int[] stack;
        int top = -1;
        int maxSize;

        public ArrayStack() {

        }

        public ArrayStack(int maxSize) {
            this.maxSize = maxSize;
            stack = new int[this.maxSize];
        }

        public boolean isFull() {
            return top == maxSize - 1;
        }


        public boolean isEmpty() {
            return top == -1;
        }

        public void push(int value) {
            if (isFull()) {
                return;
            }
            top++;
            stack[top] = value;
        }


        public void pop() {
            if (isEmpty()) {
                return;
            }
            System.out.println(stack[top]);
            top--;

        }

        public void list() {
            for (int i : stack) {
                if (i <= top + 1) {
                    System.out.println(i);
                }
            }
        }

    }


    static class linkStack {

        Node head = new Node();

        public boolean isEmpty() {
            if (head.next == null) {
                return true;
            }
            return false;
        }

        public void push(int order) {
            Node node = new Node(order);
            node.next = head.next;
            head.next = node;
        }

        public void pop() {
            if (isEmpty()) {
                return;
            }
            while (true) {
                System.out.println("出栈" + head.next.order);
                head.next = head.next.next;
                break;
            }

        }

        public void list() {

            Node temp = head.next;
            while (null != temp) {
                System.out.println("栈中数据" + temp.order);
                temp = temp.next;
            }
        }

    }

    static class Node {
        int order;
        Node next;

        public Node() {
        }

        public Node(int order) {
            this.order = order;
        }
    }


}
