package com.qm.study.arithmetic.kmp;

import java.util.Arrays;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/8/20 9:54
 */
public class KmpMatch {

    public static void main(String[] args) {
        String s = "BBC ABCDAB ABCDABCDABDE";
        // String p = "ABCDABD";
        String p = "ABCDADD";
        // System.out.println(match(s, p));
        System.out.println(Arrays.toString(getMatch(p)));

    }

    public static int match(String s, String p) {

        int[] next = getMatch(p);
        for (int i = 1, j = 0; i < s.length(); i++) {

            while (j > 0 && s.charAt(i) != p.charAt(j)) {
                j = next[j - 1];
            }
            if (s.charAt(i) == p.charAt(j)) {
                j++;
            }
            if (j == p.length()) {
                return i - j + 1;
            }
        }

        return -1;
    }


    /**
     * 获取部分匹配表
     *
     * @param dest
     * @return
     */
    public static int[] getMatch(String dest) {
        int[] next = new int[dest.length()];
        //只有一个时 数组肯定为0
        next[0] = 0;
        for (int i = 1, j = 0; i < dest.length(); i++) {

            while (j > 0 && dest.charAt(i) != dest.charAt(j)) {
                j = next[j - 1];
            }
            if (dest.charAt(i) == dest.charAt(j)) {
                j++;
            }
            next[i] = j;
        }
        return next;
    }
}
