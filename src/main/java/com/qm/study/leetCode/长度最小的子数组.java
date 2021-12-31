package com.qm.study.leetCode;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/11/18 8:18
 */
public class 长度最小的子数组 {


    public static void main(String[] args) {

        int[] ints = {
                2, 3, 1, 2, 4, 3};


        System.out.println(minSubArrayLen(7,ints));
    }


    public static int minSubArrayLen(int target, int[] nums) {
        if (nums == null) {
            return 0;
        }
        int length = 0;
        int left = 0;
        int right = 0;
        int sum = 0;
        int result = Integer.MAX_VALUE;
        while (right <= nums.length - 1) {

            sum = sum + nums[right];
            while (sum >= target) {
                length = right - left + 1;
                result = Math.min(result, length);
                sum = sum - nums[left++];
            }
            right++;
        }
        return result ==  Integer.MAX_VALUE ? 0 : result;
    }

}
