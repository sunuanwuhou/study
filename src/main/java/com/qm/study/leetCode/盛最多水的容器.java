package com.qm.study.leetCode;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/11/11 8:28
 */
public class 盛最多水的容器 {

    public int maxArea(int[] height) {
        if (null == height) {
            return 0;
        }
        int res = 0;
        int left = 0;
        int right = height.length - 1;
        while (left < right) {
            res = height[left] < height[right] ? Math.max((right - left) * height[left++], res) : Math.max((right - left) * height[right--], res);
        }
        return res;
    }
}
