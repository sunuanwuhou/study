package com.qm.study.arithmetic.sort;

import java.util.Arrays;

/**
 * 希尔排序
 *
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/7/14 9:32
 */
public class ShellSort {

    public static void main(String[] args) {
        int[] arr = {8, 9, 1, 7, 2, 5, 4, 3, 6, 1};
        shellSort(arr, 10);
    }


    //逐步推导法
    // public static void shellSort(int[] arr,int n) {
    //     //第一轮排序
    //     //先将元素分为5组 gap=n/2=5
    //     for(int i = 5; i<n;i++){
    //         //这里为什么是 -=5 不太明白
    //         for(int j = i-5; j>=0;j=j-5){
    //             if(arr[j]>arr[j+5]){
    //                 int temp = arr[j + 5];
    //                 arr[j + 5] = arr[j];
    //                 arr[j] = temp;
    //             }
    //         }
    //     }
    //
    //     System.out.println(Arrays.toString(arr));
    //
    //
    //     //第二轮排序
    //     // gap=5/2=2
    //     for(int i = 2; i<n;i++){
    //         //这里为什么是 -=5 不太明白
    //         for(int j = i-2; j>=0;j=j-2){
    //             if(arr[j]>arr[j+2]){
    //                 int temp = arr[j + 2];
    //                 arr[j + 2] = arr[j];
    //                 arr[j] = temp;
    //             }
    //         }
    //     }
    //
    //     System.out.println(Arrays.toString(arr));
    //
    //     //第二轮排序
    //     // gap=2/2=1
    //     for(int i = 1; i<n;i++){
    //         //这里为什么是 -=5 不太明白
    //         for(int j = i-1; j>=0;j=j-1){
    //             if(arr[j]>arr[j+1]){
    //                 int temp = arr[j + 1];
    //                 arr[j + 1] = arr[j];
    //                 arr[j] = temp;
    //             }
    //         }
    //     }
    //
    //     System.out.println(Arrays.toString(arr));
    // }

    //进阶版
    // public static void shellSort(int[] arr, int n) {
    //     //第一轮排序
    //     //先将元素分为5组 gap=n/2=5
    //
    //     for (int gap = n / 2; gap > 0; gap=gap/2) {
    //         for (int i = gap; i < n; i++) {
    //             //这里为什么是 -=5 不太明白
    //             for (int j = i - gap; j >= 0; j = j - gap) {
    //                 if (arr[j] > arr[j + gap]) {
    //                     int temp = arr[j + gap];
    //                     arr[j + gap] = arr[j];
    //                     arr[j] = temp;
    //                 }
    //             }
    //         }
    //     }
    //
    //     System.out.println(Arrays.toString(arr));
    //
    // }

    //进阶-移动
    public static void shellSort(int[] arr, int n) {
        for (int gap = n / 2; gap > 0; gap = gap / 2) {
            for (int i = gap; i < n; i++) {

                int j = i;
                int temp = arr[j];
                while (j - gap >=0 && temp < arr[j - gap]) {
                    arr[j] = arr[j - gap];
                    j -= gap;
                }
                arr[j] = temp;
            }
        }
        System.out.println(Arrays.toString(arr));
    }

}
