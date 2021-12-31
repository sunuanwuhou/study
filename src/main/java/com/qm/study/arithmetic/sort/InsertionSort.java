package com.qm.study.arithmetic.sort;

import java.util.Arrays;

/**
 * 插入排序-打扑克
 *
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/7/9 9:09
 */
public class InsertionSort {

    public static void main(String[] args) {
        int arr[] = {101, 34, 23, 89};
        // inserSort(arr);
        insertSort(arr,4);

    }


    public static void insertSort(int[] arr) {


        //待插入有序列表的数
        int intInsertValue = arr[1];

        //有序列表的索引
        int index = 1 - 1;

        //intInsertValue < arr[index] 表示34在有序列表还没有位置
        while (index >= 0 && intInsertValue < arr[index]) {
            arr[index + 1] = arr[index];
            index--;
        }

        arr[index + 1] = intInsertValue;

        System.out.println("第一轮插入后" + Arrays.toString(arr));


        //待插入有序列表的数
        intInsertValue = arr[2];

        //有序列表的索引
        index = 2 - 1;

        //intInsertValue < arr[index] 表示34在有序列表还没有位置
        while (index >= 0 && intInsertValue < arr[index]) {
            arr[index + 1] = arr[index];
            index--;
        }

        arr[index + 1] = intInsertValue;

        System.out.println("第二轮插入后" + Arrays.toString(arr));


        //待插入有序列表的数
        intInsertValue = arr[3];

        //有序列表的索引
        index = 3 - 1;

        //intInsertValue < arr[index] 表示34在有序列表还没有位置
        while (index >= 0 && intInsertValue < arr[index]) {
            arr[index + 1] = arr[index];
            index--;
        }

        arr[index + 1] = intInsertValue;

        System.out.println("第三轮插入后" + Arrays.toString(arr));

    }


    public static void insertSort(int[] arr, int n) {
        for (int i = 1; i <= n - 1; i++) {
            int intInsertValue = arr[i];
            //有序列表的索引
            int index = i - 1;

            while (index >= 0 && intInsertValue < arr[index]) {
                arr[index + 1] = arr[index];
                index--;
            }
            arr[index + 1] = intInsertValue;
            System.out.println("第"+i+"轮插入后" + Arrays.toString(arr));

        }
    }


}
