package com.qm.study.arithmetic.kmp;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/8/20 8:33
 */
public class ViolenceMath {


    public static void main(String[] args) {
        System.out.println(match("BBC ABCDAB ABCDABCDABDE","ABCDABD"));
    }

    public static int match(String s, String p) {
        char[] s1 = s.toCharArray();
        char[] p1 = p.toCharArray();
        int i = 0;
        int j = 0;
        while (i < s1.length && j < p1.length) {
            if(s1[i]==p1[j]){
                i++;
                j++;
            }else {
                i = i - j + 1;
                j = 0;
            }
        }
        if(j== p1.length){
            return i-j;
        }else {
            return -1;
        }
    }
}
