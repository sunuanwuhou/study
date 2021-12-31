package com.qm.study.leetCode;

import java.util.HashSet;
import java.util.Set;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2021/12/1 16:45
 */
public class 快乐数 {


    public static void main(String[] args) {


        int n=156;

        System.out.println(n%10);
        System.out.println(n/10);
    }


    public boolean isHappy(int n) {

        Set record = new HashSet();
        while (n != 1 && !record.contains(n)) {
            record.add(n);
            n = getNextNum(n);
        }
        return n == 1;
    }

    public int getNextNum(int num) {
        int res = 0;
        while (num > 0) {
            int temp = num % 10;
            res += temp * temp;
            num = num / 10;
        }
        return res;
    }
}
