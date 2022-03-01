package com.qm.study.leetCode;

import java.util.LinkedHashMap;

/**
 * 俩数之和
 *
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2021/7/1 9:44
 */
public class Lc1TwoSum {


    public static void main(String[] args) {
        //第一反应是循环数组2边，找到数组中 i+n=target，但是时间复杂度高
        // 2遍哈希表
        // 1遍哈希表
        int[] nums = {3,2,4};
        int target = 6;
        int[] ints = twoSum(nums, target);
        for (int i : ints) {
            System.out.println(i);
        }

    }



    // public static int[] twoSum(int[] nums, int target) {
    //
    //     int[] result = new int[2];
    //
    //     LinkedHashMap<Integer, Integer> linkedHashMap = new LinkedHashMap<>();
    //
    //     for (int i = 0; i <= nums.length - 1; i++) {
    //         linkedHashMap.put(nums[i],i);
    //     }
    //
    //     for (int i = 0; i <= nums.length - 1; i++) {
    //
    //         int temp = target - nums[i];
    //
    //         if(linkedHashMap.containsKey(temp)){
    //             result[0] = nums[i];
    //             result[1] = temp;
    //             break;
    //         }
    //     }
    //
    //     return result;
    //
    // }

    public static int[] twoSum(int[] nums, int target) {
        int[] result = new int[2];
        LinkedHashMap<Integer, Integer> linkedHashMap = new LinkedHashMap<>();
        for (int i = 0; i <= nums.length - 1; i++) {

            int temp = target - nums[i];

            if(linkedHashMap.containsKey(temp)){
                result[0] = linkedHashMap.get(temp);
                result[1] = i;
                break;
            }
            linkedHashMap.put(nums[i],i);
        }
        return result;
    }

}
