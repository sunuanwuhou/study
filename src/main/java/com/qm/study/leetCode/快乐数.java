package com.qm.study.leetCode;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2021/12/1 16:45
 */
public class 快乐数 {


    public static void main(String[] args) {


        int n=1;

        System.out.println(n%10);
        System.out.println(n/10);

        System.out.println( isHappy(4));;
    }


    // public static boolean isHappy(int n) {
    //
    //     Set record = new HashSet();
    //     while (n != 1 && !record.contains(n)) {
    //         record.add(n);
    //         n = getNextNum(n);
    //     }
    //     return n == 1;
    // }


    public static boolean isHappy(int n) {

        int slow = n;
        int fast = getNextNum(n);

        while (slow!=fast){
            slow = getNextNum(slow);
            fast = getNextNum(getNextNum(fast));
        }

        return fast == 1;
    }


    public static  int getNextNum(int num) {
        int res = 0;
        while (num>0){
            int temp = num % 10;
            res += temp * temp;
            num /= 10;
        }

        return res;
    }
}
