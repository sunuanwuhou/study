package com.qm.study.leetCode;

import java.util.Arrays;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/11/10 22:16
 */
public class 最接近的三数之和 {

    public int threeSumClosest(int[] nums, int target) {
        if (null == nums || nums.length < 3) {
            return 0;
        }
        Arrays.sort(nums);
        int result = nums[0] + nums[1] + nums[2];
        for (int i = 0; i <= nums.length - 1; i++) {
            if (result == target) {
                break;
            }
            int left = i + 1;
            int right = nums.length - 1;

            while (left < right) {
                int sum = nums[left] + nums[right] + nums[i];


                if (Math.abs(sum - target) < Math.abs(result - target)){
                    result = sum;
                }
                if (sum < target) {
                    left++;
                } else if (sum > target) {
                    right--;
                } else {
                    result = target;
                    left++;
                    right--;
                }
            }
        }
        return result;
    }
}
