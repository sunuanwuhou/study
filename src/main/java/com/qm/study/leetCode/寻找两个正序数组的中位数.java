package com.qm.study.leetCode;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/11/3 8:55
 */
public class 寻找两个正序数组的中位数 {


    public static void main(String[] args) {

        int[] a = new int[]{2, 5, 6, 8, 9};
        int[] b = new int[]{1, 6, 9, 11, 15,16};

        System.out.println(findMedianSortedArrays2(a,b));
    }

    public static double findMedianSortedArrays2(int[] nums1, int[] nums2) {
        int m = nums1.length;
        int n = nums2.length;
        //这里为什么定义是偶数的中位数？
        int left = (m + n + 1) / 2;
        int right = (m + n + 2) / 2;
        return (find(nums1, 0, nums2, 0, left) + find(nums1, 0, nums2, 0, right)) / 2.0;
    }


    /**
     * 在nums1和nums2中找出第k小的元素
     *
     * @param nums1 nums1数组
     * @param i     nums1数组的起始位置
     * @param nums2 nums2数组
     * @param j     nums2数组的起始位置
     * @param k     需要找到的元素的序号
     * @return 第k小的元素值
     */
    public static int find(int[] nums1, int i, int[] nums2, int j, int k) {
        if (i >= nums1.length)
            return nums2[j + k - 1]; // nums1数组全部被舍弃
        if (j >= nums2.length)
            return nums1[i + k - 1]; // nums2数组全部被舍弃
        // 当k = 1 的时候，两个数组的布局基本相同，最后只需要找到
        if (k == 1) {
            return Math.min(nums1[i], nums2[j]);
        }

        /*
         * 分别找到两个数组中的第k/2位置的元素，如果不存在就给他赋最大值， 比较两个值，值小的数组则淘汰其前k/2个元素 最后把k也减去k/2，继续递归
         */
        int mid1 = (i + k / 2 - 1 < nums1.length) ? nums1[i + k / 2 - 1] : Integer.MAX_VALUE;
        int mid2 = (j + k / 2 - 1 < nums2.length) ? nums2[j + k / 2 - 1] : Integer.MAX_VALUE;
        if (mid1 < mid2) {
            return find(nums1, i + k / 2, nums2, j, k - k / 2);
        } else {
            return find(nums1, i, nums2, j + k / 2, k - k / 2);
        }

    }

}
