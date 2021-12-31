package com.qm.study.leetCode;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/11/8 9:03
 */
public class 最长回文子串 {


    public static void main(String[] args) {

        System.out.println(longestPalindrome("babad"));

    }


    private static String longestPalindrome(String s) {
        String res = "";


        for (int i = 0; i < s.length(); i++) {

            // 以 s[i] 为中心的最长回文子串
            String s1 = palindrome(s, i, i);
            // 以 s[i] 和 s[i+1] 为中心的最长回文子串
            String s2 = palindrome(s, i, i + 1);
            // res = longest(res, s1, s2)
            res = res.length() > s1.length() ? res : s1;
            res = res.length() > s2.length() ? res : s2;

        }
        return res;
    }


    private static String palindrome(String s, int l, int r) {
        while (l >=0  && r < s.length() && s.charAt(l)== s.charAt(r)) {
            l--;
            r++;
        }

        if(l+1>r-l-1){
            return "";
        }
        return s.substring(l + 1, r-l- 1);
    }
}
