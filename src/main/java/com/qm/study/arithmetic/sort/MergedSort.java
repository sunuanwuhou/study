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

        sort(arr, 0, arr.length - 1);


        System.out.println(Arrays.toString(arr));
    }


    public static void sort(int nums[],int lo,int hi){
        //注意 这里是小于 不是小于等于
      if(lo<hi){
          int mid = (lo + hi) / 2;
          sort(nums, lo, mid);
          sort(nums, mid + 1, hi);

          /****** 后序遍历位置 ******/
          // 合并两个排好序的子数组
          merge(nums, lo, mid, hi);
          /************************/
      }
    }

    /**
     * 类似合并有序链表
     * begin-mid
     * mid-end
     * 本身已经是有序的，我们需要合并他们
     */
    public static void merge(int arr[],int begin,int mid,int end){

        int left = begin;
        int right = mid+1;
        int temp[] = new int [arr.length];
        int i = 0;

        while (left<=mid&&right<=end){
            if(arr[left]<arr[right]){
                temp[i++]=arr[left++];
            }else {
                temp[i++]=arr[right++];
            }
        }

        while (left<=mid){
            temp[i++]=arr[left++];
        }

        while (right<=end){
            temp[i++]=arr[right++];
        }
        // 将temp的元素copy到arr中
        int t=0;
        int tempLeft = begin;

        while (tempLeft <= end) {
            arr[tempLeft++] = temp[t++];
        }
    }

}
