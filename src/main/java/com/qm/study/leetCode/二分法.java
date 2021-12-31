package com.qm.study.leetCode;

/**
 * // int binarySearch(int[] nums, int target) {
 * //     int left = 0, right = ...;
 * //
 * //     while(...) {
 * //         int mid = left + (right - left) / 2;
 * //         if (nums[mid] == target) {
 * //         ...
 * //         } else if (nums[mid] < target) {
 * //             left = ...
 * //         } else if (nums[mid] > target) {
 * //             right = ...
 * //         }
 * //     }
 * //     return ...;
 * // }
 *
 * @author qiumeng
 * @version 1.0
 * @description
 */
public class 二分法 {


    public static void maiqn(String[] args) {

        int[] arr = new int[]{1, 5, 6, 9, 8, 4, 32, 65, 48};

        System.out.println(binarySearch(arr,65));
    }


    public static int binarySearch(int[] arr, int target) {
        if (null == arr) {
            return -1;
        }
        int left = 0;
        int right = arr.length - 1;
        //确定搜索区间【0，length-1】 或者 【0，length)  注意开闭区间
        //确定终止条件，当left>right 时，循环结束。
        while (left <= right) {
            //  left + (right - left) / 2 等同于 (right+left)/2
            //是为了防止溢出
            int mid = left + (right - left) / 2;
            //下面要做的就是比较，‘缩小范围区间’，脑海中要有画面。
            if (arr[mid] == target) {
                return mid;
            } else if (arr[mid] < target) {
                //中位数都小于target 那么left是不是要右移
                left = mid + 1;
            } else if (arr[mid] > target) {
                //中位数都小于target 那么 right 是不是要左移等于中位数
                right = mid;
            }
        }
        return left;
    }

}
