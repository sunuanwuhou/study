package com.qm.study.leetCode;

/**
 * [3]无重复字符的最长子串
 *
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2021/7/5 14:36
 */
public class Lc无重复字符的最长子串 {


    public static void main(String[] args) {
        String s = "pwwkew";
        System.out.println(maxStr(s));

    }

    private static int maxStr(String str) {
        int n = str.length();
        if (n <= 1) return n;
        int maxLen = 1;

        int left = 0, right = 0;

        // 记录字符出现的上一次位置。
        int[] dp = new int[128];
        while (right < n) {
            char rightChar = str.charAt(right);
            int rightCharIndex = dp[rightChar];
            left = Math.max(left, rightCharIndex);
            maxLen = Math.max(maxLen, right - left);
            dp[rightChar] = right ;
            right++;
        }
        return maxLen;
    }

}
