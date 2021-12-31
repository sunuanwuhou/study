package com.qm.study.arithmetic.sort;

import java.util.Arrays;

/**
 * 冒泡排序
 *
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/7/7 9:12
 */
public class BubbleSort {


    public static void main(String[] args) {

        int arr[] = new int[]{3, 9, -1, 20, 10};
        //时间复杂度 O(n^2)
        int temp = 0;

        for (int i = 0; i < arr.length - 1; i++) {
            boolean flag = false;
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    flag = true;
                    temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
            if (!flag) {
                break;
            }
            System.out.println("第" + (i + 1) + "趟排序后的数组");
            System.out.println(Arrays.toString(arr));
        }

    }
}
