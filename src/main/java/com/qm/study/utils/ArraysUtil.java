package com.qm.study.utils;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/11/2 21:00
 */
public class ArraysUtil {


    /**
     *  交换数组2个元素
     * @author qiumeng
     * @param arr
     * @param l
     * @param r
     */
    public static void swap(int[] arr, int l, int r) {
        int temp = arr[l];
        arr[l] = arr[r];
        arr[r] = temp;
    }
}
