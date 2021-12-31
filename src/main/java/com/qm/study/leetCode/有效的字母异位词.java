package com.qm.study.leetCode;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2021/11/29 7:59
 */
public class 有效的字母异位词 {

    public boolean isAnagram(String s, String t) {

        int[] record = new int[26];
        for (char c : s.toCharArray()) {
            record[c - 'a'] += 1;
        }
        for (char c : t.toCharArray()) {
            record[c - 'a'] -= 1;
        }

        for (int i : record) {
            if(i!=0){
                return false;
            }
        }

        return true;
    }
}
