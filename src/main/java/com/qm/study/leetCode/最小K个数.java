package com.qm.study.leetCode;

import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * 这是 LeetCode 上的 「面试题 17.14. 最小K个数」 ，难度为 「中等」。
 * <p>
 * Tag : 「优先队列」、「堆」、「排序」
 * <p>
 * 设计一个算法，找出数组中最小的k个数。以任意顺序返回这k个数均可。
 * <p>
 * 示例：
 * <p>
 * 输入： arr = [1,3,5,7,2,4,6,8], k = 4
 * <p>
 * 输出： [1,2,3,4]
 * 提示：
 * <p>
 * 0 <= len(arr) <= 100000
 * 0 <= k <= min(100000, len(arr))
 *
 * @version 1.0
 * @description
 */
public class 最小K个数 {


    public static void main(String[] args) {

        int[] arr = new int[]{1, 3, 5, 7, 2, 4, 6, 8};

        System.out.println(Arrays.toString(test2(arr, 3)));
    }

    public static int[] test1(int[] arr, int k) {
        //最小堆
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>((a, b) -> a - b);
        for (int i : arr) priorityQueue.add(i);
        int[] ans = new int[k];
        for (int i = 0; i < k; i++) ans[i] = priorityQueue.poll();
        return ans;
    }

    public static int[] test2(int[] arr, int k) {
        //最小堆
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>((a, b) -> b - a);
        for (int i : arr) priorityQueue.add(i);
        int[] ans = new int[k];
        for (int i = 0; i < k; i++) ans[i] = priorityQueue.poll();
        return ans;
    }

    public static int[] normal(int[] arr, int k) {
        //正常排序
        Arrays.sort(arr);
        int[] ans = new int[k];
        for (int i = 0; i < k; i++) ans[i] = arr[i];
        return ans;
    }
    public static int[] quickSort(int[] arr, int k) {
        //快排思想

        int[] ans = new int[k];
        return ans;
    }


}
