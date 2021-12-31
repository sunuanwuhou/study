package com.qm.study.arithmetic.sort;

import java.util.Arrays;

/**
 * 选择排序
 *
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/7/7 20:01
 */
public class SelectionSort {


    public static void main(String[] args) {
        selectionSort1(new int[]{3, 9, -1, 20, 10});
        // init();
    }


    public static void init() {
        int arr[] = new int[]{3, 9, -1, 20, 10};
        int n = 0;
        int min = arr[n];
        //第一步
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] < min) {
                min = arr[i];
                n = i;
            }
        }
        arr[n] = arr[0];
        arr[0] = min;
        System.out.println("第一轮过后:" + Arrays.toString(arr));

        //第二步
        n = 1;
        min = arr[n];
        for (int i = 1; i < arr.length - 1; i++) {
            if (arr[i] < min) {
                min = arr[i];
                n = i;
            }
        }
        arr[n] = arr[1];
        arr[1] = min;
        System.out.println("第二轮过后:" + Arrays.toString(arr));

        //依次类推
    }

    //进化版
    public static void selectionSort(int arr[]) {
        for (int i = 0; i < arr.length - 1; i++) {
            int n = i;
            int min = arr[n];
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < min) {
                    min = arr[j];
                    n = j;
                }
            }
            arr[n] = arr[i];
            arr[i] = min;
            System.out.println("第" + (i + 1) + "轮过后:" + Arrays.toString(arr));
        }
    }


    public static void selectionSort1(int arr[]) {
        for (int i = arr.length - 1; i >0; i--) {
            int n = i;
            int max = arr[n];
            for (int j = i - 1; j >=0; j--) {
                if (arr[j] > max) {
                    max = arr[j];
                    n = j;
                }
            }
            arr[n] = arr[i];
            arr[i] = max;
            System.out.println("第" + (i + 1) + "轮过后:" + Arrays.toString(arr));
        }
    }

}
