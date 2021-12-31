package com.qm.study.dataStructures.stack;

/**
 *     用 栈 实现 综合计算器
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/6/27 21:02
 */
public class Calculator {

    public static void main(String[] args) {

        String expresion = "33+2*6-3";

        ArrayStack<Integer> numStack = new ArrayStack(new Integer[10]);
        ArrayStack<Character> operStack = new ArrayStack(new Character[10]);
        int pop1;
        int pop2;

        String temp="";
        char[] chars = expresion.toCharArray();
        for(int i = 0; i<= chars.length-1; i++ ){
            char c = chars[i];
            if (isOper(c)) {
                //符号栈
                if (operStack.isEmpty()) {
                    operStack.push(c);
                } else {
                    //如果当前操作符优先级小于或者等于栈中的操作符
                    if (getSuper(c) <= getSuper(operStack.getTop())) {
                        pop1 = numStack.pop();
                        pop2 = numStack.pop();
                        numStack.push(cal(pop1,pop2,operStack.pop()));
                        operStack.push(c);
                    }else {
                        operStack.push(c);
                    }
                }

            } else {
                if(i+1<=chars.length-1){
                    temp = temp+String.valueOf( (int)c-48);
                   if(isOper(chars[i+1])){
                       //这里要注意的是  ASCLL码
                       numStack.push(Integer.parseInt(temp));
                       temp = "";
                   }
                }
                if(i==chars.length-1){
                    temp = temp+String.valueOf( (int)c-48);
                    numStack.push(Integer.parseInt(temp));
                    temp = "";
                }


            }
        }

        while (true){
            if(operStack.isEmpty()){
                break;
            }
            Integer pop3 = numStack.pop();
            Integer pop4 = numStack.pop();
            Character pop = operStack.pop();

            numStack.push(cal(pop3,pop4,pop));

        }
        System.out.println(numStack.pop());


    }


    static class ArrayStack<E> {
        E[] stack;
        int top = -1;
        int maxSize;


        public ArrayStack(E[] stack) {
            this.stack = stack;
            this.maxSize = stack.length;
        }

        public boolean isFull() {
            return top == maxSize - 1;
        }


        public boolean isEmpty() {
            return top == -1;
        }


        public E getTop() {
            return stack[top];
        }

        public void push(E value) {
            if (isFull()) {
                return;
            }
            top++;
            stack[top] = value;
        }


        public E pop() {
            if (isEmpty()) {
                return null;
            }
            E e = stack[top];
            top--;
            return e;

        }

        public void list() {
            for (Object i : stack) {
                if ((int) i <= top + 1) {
                    System.out.println(i);
                }
            }
        }

    }

    //判断运算符优先级
    public static int getSuper(int oper) {
        if (oper == '*' || oper == '/') {
            return 1;
        } else if (oper == '+' || oper == '-') {
            return 0;
        } else {
            return -1;
        }

    }

    public static int cal(int num1, int num2, int oper) {
        int res = 0;
        switch (oper) {
            case '+':
                res = num1 + num2;
                break;
            case '-':
                res = num2 - num1;//注意顺序
                break;
            case '*':
                res = num1 * num2;
                break;
            case '/':
                res = num1 / num2;
                break;
            default:
                break;
        }
        return res;
    }

    public static boolean isOper(char val) {

        return val == '+' || val == '-' || val == '*' || val == '/';
    }

}
