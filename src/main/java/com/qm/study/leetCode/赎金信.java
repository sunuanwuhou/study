package com.qm.study.leetCode;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2021/12/3 16:15
 */
public class 赎金信 {


    public static void main(String[] args) {

        String ransomNote = "abc";
        String magazine = "abcf";
        System.out.println(canConstruct(ransomNote,magazine));
    }

    public static boolean canConstruct(String ransomNote, String magazine) {
        int[] arr = new int[26];
        for (int i = 0; i < magazine.toCharArray().length; i++) {
            int temp = magazine.charAt(i) - 'a';
            arr[temp]++;
        }

        for (int i = 0; i < ransomNote.toCharArray().length; i++) {
            int temp = ransomNote.charAt(i) - 'a';
            if (arr[temp] > 0) {
                arr[temp]--;
            } else {
                return false;
            }
        }
        return true;
    }
}
