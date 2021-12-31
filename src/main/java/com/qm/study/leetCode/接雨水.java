package com.qm.study.leetCode;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2021/12/10 16:34
 */
public class 接雨水 {

    public int trap(int[] height) {

        int res = 0;

        int left = 0;
        int right = height.length - 1;

        int l_max = 0;

        int r_max = 0;

        if (left < right) {
            l_max = Math.max(l_max, height[left]);
            r_max = Math.max(r_max, height[right]);

            // res += (r_max - l_max) - height[i];
            if (l_max < r_max) {
                res += l_max - height[left];
                left++;
            } else {
                res += r_max - height[right];
                right--;
            }
        }

        return res;
    }

}
