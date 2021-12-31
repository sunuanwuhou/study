package com.qm.study.leetCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/11/10 9:00
 */
public class 三数之和 {


    public static void main(String[] args) {

        int[] ints = {0, 0, 0, 0};
        List<List<Integer>> lists = threeSum(ints);

        System.out.println(lists);
    }


    public static List<List<Integer>> threeSum(int[] nums) {

        List<List<Integer>> result = new ArrayList<>();
        if (null == nums||nums.length < 3) {
            return result;
        }

        Arrays.sort(nums);

        for (int i = 0; i <= nums.length - 1; i++) {

            if (nums[i] > 0) {
                return result;
            }
            if(i > 0 && nums[i] == nums[i - 1]){
                continue;
            }
            int left = i + 1;
            int right = nums.length - 1;

            while (left < right) {
                int sum = nums[left] + nums[right] + nums[i];
                if (sum < 0) {
                    left++;
                } else if (sum > 0) {
                    right--;
                } else  {
                    result.add(new ArrayList<Integer>(Arrays.asList(nums[left], nums[right], nums[i])));
                    while (left + 1 < right && nums[left] == nums[left + 1]) left++;//[1]
                    while (left < right - 1 && nums[right] == nums[right - 1]) right--;//[2]
                    left++;
                    right--;
                }
            }

        }
        return result;
    }
}
