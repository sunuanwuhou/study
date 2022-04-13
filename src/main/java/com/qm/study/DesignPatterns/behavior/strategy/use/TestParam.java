package com.qm.study.DesignPatterns.behavior.strategy.use;

import java.util.*;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/2/23 21:29
 */
public class TestParam {


    public static void main(String[] args) {

        // System.out.println(minWindow("ADOBECODEBANC", "ABC"));


        String multiply = multiply("123","456");

    }



        public static String multiply(String num1, String num2) {

            int n = num1.length(), m = num2.length();
            int[] A = new int[n], B = new int[m];
            for (int i = n - 1; i >= 0; i--) A[n - 1 - i] = num1.charAt(i) - '0'; //反向存贮
            for (int i = m - 1; i >= 0; i--) B[m - 1 - i] = num2.charAt(i) - '0';

            int[] C = new int[n + m];
            for (int i = 0; i < n; i++)
                for (int j = 0; j < m; j++)
                    C[i + j] += A[i] * B[j];
            int t = 0; //存贮进位
            for (int i = 0; i < C.length; i++) {
                t += C[i];
                C[i] = t % 10;
                t /= 10;
            }
            int k = C.length - 1;
            while (k > 0 && C[k] == 0) k--;   //去除前导0
            StringBuilder sb = new StringBuilder();
            while (k >= 0) sb.append((char)(C[k--] + '0')); //反转
            return sb.toString();
        }


    public static int findPeakElement(int[] nums) {
        int length = nums.length;
        if (1 == length) {
            return 0;
        }

        // 先特判两边情况
        if(nums[0] > nums[1]) return 0;
        if(nums[length - 1] > nums[length - 2]) return length - 1;

        int left = 0;
        int right = length - 1;

        while (left <= right) {

            int mid = (left + right) / 2;

            if (mid >= 1&mid < length - 1&&nums[mid - 1] < nums[mid] && nums[mid] > nums[mid + 1]) {
                return mid;
            } else if (mid >= 1&& nums[mid - 1] > nums[mid]){
                right = mid - 1;
            } else if (mid < length - 1 && nums[mid] < nums[mid+1]) {
                left = mid + 1;
            }
        }

        return -1;
    }




    static List<List<Integer>> res = new LinkedList<>();

    static LinkedList<Integer> track = new LinkedList<>();

    //为什么要定义一个这个，如果是数组 可以直接用track.contains来判断，链表就不行了。
    static boolean[] used;

    static List<List<Integer>> permute(int[] nums) {
        used = new boolean[nums.length];
        Arrays.sort(nums);
        backtrack(nums);
        return res;
    }


    public boolean isPalindrome(String s) {

        char[] chars = s.toCharArray();
        int length = chars.length;

        int left = 0;
        int right = length-1;

        while (left<right){

            if(chars[left]!=chars[right]){
                return false;
            }

            left++;
            right--;
        }

        return true;
    }


    public void reverseString(char[] s) {

        int length = s.length;

        int left = 0;
        int right = length - 1;

        while (left < right) {

            char temp = s[left];

            s[left] = s[right];

            s[right] = temp;
        }

    }


    public int[] twoSum(int[] numbers, int target) {

        int[] result = new int[2];

        int length = numbers.length;
        if (0 == length) {
            return result;
        }

        int left = 1;
        int right = length;

        while (left < right) {

            int sum = numbers[left] + numbers[right];

            if (sum == target) {
                result[0] = numbers[left];
                result[1] = numbers[right];
                return result;
            } else if (sum > target) {
                right--;
            } else {
                left++;
            }

        }

        return result;

    }


    static void backtrack(int[] nums) {
        //终止条件
        if (track.size() == nums.length) {
            res.add(new LinkedList(track));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            //已经做过决策 跳出
            if (used[i]) {
                continue;
            }
            if (i > 0 && nums[i] == nums[i - 1] && !used[i - 1]) {
                continue;
            }
            //加入路径
            used[i] = true;
            track.add(nums[i]);
            //选择列表
            backtrack(nums);
            //撤出路径
            used[i] = false;
            track.removeLast();
        }
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
