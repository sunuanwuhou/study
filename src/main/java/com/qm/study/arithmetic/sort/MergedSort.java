package com.qm.study.arithmetic.sort;

import java.util.Arrays;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/7/19 9:19
 */
public class MergedSort {

    public static void main(String[] args) {

        int arr[] = {8, 4, 5, 6, 4, 1, 2, 3, 56, 8, 56};
        int temp[] = new int[arr.length];

        mergeSort(arr, 0, arr.length - 1, temp);


        System.out.println(Arrays.toString(arr));
    }


    public static void mergeSort(int arr[], int left, int right, int[] temp) {

        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(arr, left, mid, temp);
            mergeSort(arr, mid + 1, right, temp);
            merge(arr, left, mid, right, temp);
        }

    }


    /**
     * 合并2各有序链表
     *
     * @param arr   排序的原始数组
     * @param left  左边有序序列的初始索引
     * @param mid   中间索引
     * @param right 右边索引
     * @param temp  临时数组
     */
    public static void merge(int arr[], int left, int mid, int right, int[] temp) {
        int i = left; //初始化i 左边有序序列的初始索引
        int j = mid + 1; //初始化j 右边有序序列的初始索引
        int t = 0; // 指向temp数组的当前索引

        //先把左右两边有序的的数据按照规则填充到temp数组,

        while (i <= mid && j <= right) {

            if (arr[i] <= arr[j]) {
                temp[t] = arr[i];
                t++;
                i++;
            } else {
                temp[t] = arr[j];
                t++;
                j++;

            }
        }

        while (i <= mid) {
            temp[t] = arr[i];
            t++;
            i++;
        }
        while (j <= right) {
            temp[t] = arr[j];
            t++;
            j++;
        }
        // 将temp的元素copy到arr中

        t = 0;
        int tempLeft = left;

        while (tempLeft <= right) {
            arr[tempLeft] = temp[t];
            t++;
            tempLeft++;

        }

    }


}
