package com.qm.study.arithmetic.sort;

import java.util.Arrays;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/8/2 8:56
 */
public class HeapSort {


    public static void main(String[] args) {
        // int[] arr = {2, 3, 9, 4, 7, 11, 5, 6, 8, 1, 68, 90, 56};
        int[] arr = {4, 6, 8, 5, 9};
        heapSort(arr);
        System.out.println(Arrays.toString(arr));

    }

    public static void heapSort(int[] arr) {
        int temp = 0;
        for (int i = arr.length / 2 - 1; i >= 0; i--) {
            adjustHeapSort(arr, i, arr.length);
        }

        //为什么不排序了？
        for (int j = arr.length - 1; j >= 0; j--) {
            temp = arr[j];
            arr[j] = arr[0];
            arr[0] = temp;
            //为什么是从0开始
            adjustHeapSort(arr, 0, j);
        }

    }


    /**
     * 待调整数组构造 大顶堆
     *
     * @param arr
     * @param i 当前非叶子节点
     * @param length
     */
    public static void adjustHeapSort(int[] arr, int i, int length) {

        int temp = arr[i];

        for (int k = i * 2 + 1; k < length; k = k * 2 + 1) {

            //比较当前非叶子节点的 左右叶子节点的大小
            if (k + 1 < length && arr[k] < arr[k + 1]) {
                k++;
            }
            if (arr[k] > temp) {
                arr[i] = arr[k];
                i = k;//要看这个节点还有没有子节点需要比较
            } else {
                break;
            }
        }
        arr[i] = temp;//这里为什么要放在外面？
    }
}
