package com.qm.study.leetCode;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/11/5 9:16
 */
public class 寻找旋转排序数组中的最小值 {


    public static void main(String[] args) {

        int[] num = new int[]{1,3,5};

        // System.out.println(search(num));
        System.out.println(minArray(num));
    }

    private static int search(int[] num) {
        int length = num.length;
        if (1 == length) {
            return num[0];
        }
        int left = 0;
        int right = length - 1;
        while (left < right) {
            if (num[left] > num[right]) {
                left++;
            } else if (num[left] < num[right]) {
                right--;
            }
            if (left == right) {
                break;
            }
        }
        return num[left];
    }


    private static int minArray(int[] num) {
        int length = num.length;
        if (1 == length) {
            return num[0];
        }
        int left = 0;
        int right = length - 1;
        while (left < right) {
            int medium = (left + right) / 2;
            if (num[medium] < num[right]) {
                right = medium;
            } else {
                left = medium + 1;
            }
        }
        return num[left];
    }


}
