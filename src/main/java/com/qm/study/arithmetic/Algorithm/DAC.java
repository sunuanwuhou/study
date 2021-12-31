package com.qm.study.arithmetic.Algorithm;

/**
 * 分治算法 解决 汉诺塔问题
 *
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/8/13 14:52
 */
public class DAC {


    public static void main(String[] args) {

        tow(4, 'A', 'B', 'C');

    }


    public static void tow(int num, char a, char b, char c) {

        if (num == 1) {
            System.out.println("第1个盘从" + a + "->" + c);
            return;
        }
        //n-1移动到B
        tow(num - 1, a, c, b);
        //n移动到C
        System.out.println("第" + num + "个盘从" + a + "->" + c);
        //n-1移动到C
        tow(num - 1, b, a, c);

    }


}

