package com.qm.study.arithmetic.sort;

import com.qm.study.common.utils.ArraysUtil;

import java.util.Arrays;

/**
 * 快速排序
 *
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2021/7/17 9:36
 */
public class QuickSort {

    public static void main(String[] args) {

        int[] arr = {-1, -9, 78, 0, 23, -45, 70, -2, -1, -5, 89, 90};
        System.out.println("原始数组" + Arrays.toString(arr));
        sort(arr, 0, arr.length - 1);
        System.out.println("sort数组" + Arrays.toString(arr));
    }





    static void sort(int[] nums, int lo, int hi) {
        if(lo<=hi){
            /****** 前序遍历位置 ******/
            // 通过交换元素构建分界点 p
            int p = partition(nums, lo, hi);
            /************************/
            sort(nums, lo, p - 1);
            sort(nums, p + 1, hi);
        }

    }
    public static int partition(int[]arr,int begin,int end){
        int temp = arr[begin];
        int i = begin;
        int j = end;
        while (i!=j){
            while(i<j&&arr[j]>=temp){
                j--;
            }
            while(i<j&&arr[i]<=temp){
                i++;
            }
            if(i<j){
                //交换ij位置
                ArraysUtil.swap(arr,i,j);
            }
        }
        //ij相等 交换基准位置
        ArraysUtil.swap(arr, begin, i);
        return i;
    }



    /**
     * @param arr   待排序的数组
     * @param left  左边索引
     * @param right 右边索引
     * @date 2021-07-17
     */
    // public static void quickSort(int[] arr, int left, int right) {
    //
    //     int l = left;
    //     int r = right;
    //
    //     // int medium = arr[(l + r) / 2];
    //     int medium = arr[left];
    //
    //     while (l < r) {
    //         while (arr[l] < medium) {
    //             l++;
    //         }
    //         while (arr[r] > medium) {
    //             r--;
    //         }
    //         ArraysUtil.swap(arr,l,r);
    //         if (l >= r) {
    //             break;
    //         }
    //         //防止死循环 左边和右边 出现和 medium一样的值 这事需要 左+ 有-
    //         if (arr[l] == medium) {
    //             r--;
    //         }
    //         if (arr[r] == medium) {
    //             l++;
    //         }
    //     }
    //     //第一次交换完毕
    //     if (l == r) {
    //         l++;
    //         r--;
    //     }
    //     //向左递归
    //
    //     if (left < r) {
    //         quickSort(arr, left, r);
    //     }
    //
    //     if (l < right) {
    //         quickSort(arr, l, right);
    //     }
    // }
}
