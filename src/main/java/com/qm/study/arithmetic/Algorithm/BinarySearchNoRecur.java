package com.qm.study.arithmetic.Algorithm;

/**
 * 二分查找 非递归
 *
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/8/13 13:56
 */
public class BinarySearchNoRecur {

    public static void main(String[] args) {
        int arr[] = {1, 3};
        System.out.println(binary(arr, 6));
        // System.out.println(binaryCur(arr, 0, arr.length, 6));

    }

    /**
     * 非递归版
     *
     * @param arr
     * @param target
     * @return
     */
    public static int binary(int[] arr, int target) {

        int left = 0;
        int right = arr.length - 1;

        while (left <= right) {
            int medium = (left + right) / 2;
            if (arr[medium] == target) {
                return medium;
            } else if (arr[medium] > target) {
                right = medium - 1;
            } else if (arr[medium] < target) {
                left = medium + 1;
            }
        }
        return -1;
    }


    /**
     * 递归版
     * @param arr
     * @param n
     * @param m
     * @param target
     * @return
     */
    public static int binaryCur(int[] arr, int n, int m, int target) {
        int left = 0;
        int right = arr.length - 1;

        while (left <= right) {
            int medium = (left + right) / 2;
            if (arr[medium] == target) {
                return medium;
            } else if (arr[medium] > target) {
                right = binaryCur(arr, left, right - 1, target);
            } else if (arr[medium] < target) {
                right = binaryCur(arr, left + 1, right, target);
            }
        }

        return -1;
    }


}
