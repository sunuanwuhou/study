package com.qm.study.DesignPatterns.behavior.strategy.use;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/2/23 21:29
 */
public class TestParam {


    public static void main(String[] args) {

        System.out.println(minWindow("ADOBECODEBANC", "ABC"));

    }




    public int lengthOfLongestSubstring(String s) {


        Map<Character, Integer> windows = new HashMap<>();

        int max = 0;

        char[] chars = s.toCharArray();
        int left = 0;
        int right = 0;

        while (right < chars.length) {

            char c = chars[right];
            right++;
            windows.put(c, windows.getOrDefault(c, 0) + 1);

            while (windows.get(c) > 1) {
                left++;
                char d = chars[left];
                windows.put(d, windows.getOrDefault(d, 0) - 1);
            }

            max = Math.max(max, right - left);
        }
        return max;
    }

    public boolean checkInclusion(String s1, String s2) {

        //首先定义一个滑动窗口
        Map<Character, Integer> windows = new HashMap<>();
        //存放我们需要的数据 也就是t
        Map<Character, Integer> needs = new HashMap<>();
        char[] charss = s1.toCharArray();
        for (char c : charss) {
            needs.put(c, needs.getOrDefault(c, 0) + 1);
        }

        int left = 0, right = 0;
        int valid = 0;
        char[] chars = s2.toCharArray();

        while (right < chars.length) {
            // 开始滑动
            char c = chars[right];
            //右移窗口
            right++;

            // 进行窗口内数据的一系列更新
            if (needs.containsKey(c)) {
                windows.put(c, windows.getOrDefault(c, 0) + 1);
                if (needs.get(c).equals(windows.get(c))) {
                    valid++;
                }
            }

            // 判断左侧窗口是否要收缩
            while (right - left >= charss.length) {
                // 在这里更新最小覆盖子串
                if (valid == needs.size()) {
                    return true;
                }
                // d 是将移出窗口的字符
                char d = chars[left];
                // 左移窗口
                left++;
                // 进行窗口内数据的一系列更新
                if (needs.containsKey(d)) {
                    if (needs.get(d).equals(windows.get(d))) {
                        valid--;
                    }
                    windows.put(d, windows.getOrDefault(d, 0) - 1);
                }
            }

        }

        return false;


    }


    public static String minWindow(String s, String t) {
        //首先定义一个滑动窗口
        Map<Character, Integer> windows = new HashMap<>();
        //存放我们需要的数据 也就是t
        Map<Character, Integer> needs = new HashMap<>();

        for (char c : t.toCharArray()) {
            needs.put(c, needs.getOrDefault(c, 0) + 1);
        }

        int left = 0, right = 0;
        int valid = 0;
        char[] chars = s.toCharArray();

        // 记录最小覆盖子串的起始索引及长度
        int start = 0, len = Integer.MAX_VALUE;


        while (right < chars.length) {
            // 开始滑动
            char c = chars[right];
            //右移窗口
            right++;

            // 进行窗口内数据的一系列更新
            if (needs.containsKey(c)) {
                windows.put(c, windows.getOrDefault(c, 0) + 1);
                if (needs.get(c).equals(windows.get(c))) {
                    valid++;
                }
            }

            // 判断左侧窗口是否要收缩

            while (valid == needs.size()) {
                // 在这里更新最小覆盖子串
                if (right - left < len) {
                    start = left;
                    len = right - left;
                }
                // d 是将移出窗口的字符
                char d = chars[left];
                // 左移窗口
                left++;
                // 进行窗口内数据的一系列更新
                if (needs.containsKey(d)) {
                    if (needs.get(d).equals(windows.get(d))) {
                        valid--;
                    }
                    windows.put(d, windows.getOrDefault(d, 0) - 1);
                }
            }

        }

        // 返回最小覆盖子串
        return start + len > Integer.MAX_VALUE ? "" : s.substring(start, start + len);
    }
}
