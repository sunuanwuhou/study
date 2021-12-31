package com.qm.study.arithmetic.sort;

import java.util.Arrays;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/7/21 9:34
 */
public class RadixSort {


    public static void main(String[] args) {
        int arr[] = {5, 12, 456, 235, 895, 124, 74, 32, 2, 5, 8, 9, 4};
        radixSort(arr);
        System.out.println(Arrays.toString(arr));

    }

    // public static void radixSort(int[] arr) {
    //
    //     //很明显 空间换时间
    //     int bucket[][] = new int[10][arr.length];
    //
    //     //每个桶的有效数量
    //     int[] bucketCounts = new int[10];
    //
    //
    //     for (int i = 0; i <= arr.length - 1; i++) {
    //
    //         int a = arr[i] % 10;
    //
    //         //放在对应桶的一维数组中
    //         bucket[a][bucketCounts[a]] = arr[i];
    //
    //         bucketCounts[a]++;
    //     }
    //     int index = 0;
    //
    //     for (int i = 0; i <= bucketCounts.length - 1; i++) {
    //         if (bucketCounts[i] != 0) {
    //
    //             for (int j = 0; j < bucketCounts[i]; j++) {
    //                 arr[index++] = bucket[i][j];
    //             }
    //         }
    //         bucketCounts[i] = 0;
    //     }
    //
    //
    //     for (int i = 0; i <= arr.length - 1; i++) {
    //
    //         int a = arr[i] / 10 % 10;
    //
    //         //放在对应桶的一维数组中
    //         bucket[a][bucketCounts[a]] = arr[i];
    //
    //         bucketCounts[a]++;
    //     }
    //
    //     index = 0;
    //
    //     for (int i = 0; i <= bucketCounts.length - 1; i++) {
    //         if (bucketCounts[i] != 0) {
    //
    //             for (int j = 0; j < bucketCounts[i]; j++) {
    //                 arr[index++] = bucket[i][j];
    //             }
    //         }
    //         bucketCounts[i] = 0;
    //
    //     }
    //
    //     // 依次类推
    // }


    public static void radixSort(int[] arr) {
        //选择一个最大数
        int max = arr[0];
        for (int i = 0; i <= arr.length - 1; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }

        int length = String.valueOf(max).length();


        //很明显 空间换时间
        int bucket[][] = new int[10][arr.length];

        //每个桶的有效数量
        int[] bucketCounts = new int[10];

        for (int m = 0, n = 1; m <= length - 1; m++, n = n * 10) {
            for (int i = 0; i <= arr.length - 1; i++) {

                int a = arr[i] / n % 10;

                //放在对应桶的一维数组中
                bucket[a][bucketCounts[a]] = arr[i];

                bucketCounts[a]++;
            }
            int index = 0;

            for (int i = 0; i <= bucketCounts.length - 1; i++) {
                if (bucketCounts[i] != 0) {

                    for (int j = 0; j < bucketCounts[i]; j++) {
                        arr[index++] = bucket[i][j];
                    }
                }
                bucketCounts[i] = 0;
            }
        }


    }
}
