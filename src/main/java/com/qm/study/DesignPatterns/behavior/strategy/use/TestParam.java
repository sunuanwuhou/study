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
        System.out.println(countSubstrings("aaa"));
    }

    public static int countSubstringsa(String s) {
        // 中心扩展法
        int ans = 0;
        for (int center = 0; center < 2 * s.length() - 1; center++) {
            // left和right指针和中心点的关系是？
            // 首先是left，有一个很明显的2倍关系的存在，其次是right，可能和left指向同一个（偶数时），也可能往后移动一个（奇数）
            // 大致的关系出来了，可以选择带两个特殊例子进去看看是否满足。
            int left = center / 2;
            int right = left + center % 2;

            while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
                ans++;
                left--;
                right++;
            }
        }
        return ans;
    }



    //aaa
    public static int countSubstrings(String s) {
        int res = 0;
        for (int i = 0; i < s.length(); i++) {
            //当前元素一 i为中心的回文串
            res+= palindrome(s, i, i);
            //当前元素一 i i+1为中心的回文串
            res+=  palindrome(s, i, i + 1);

        }

        return res;
    }

    public static int palindrome(String s, int left, int right) {
        int count = 0;
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            count++;
            right++;
            left--;
        }
        return count;
    }


    public static double quickPow(double x, long y) {
        double ret = 1.0;
        while (y != 0) {
            if ((y & 1) != 0) {
                ret = ret * x;
            }
            x = x * x;
            y >>= 1;
            System.out.println("x=" + x + "y=" + y + "ret=" + ret);
        }
        return ret;
    }


    public static ListNode rotateRight(ListNode head, int k) {

        //先拼接成环，在从头部向后移动，断开其位置 ，就是旋转后的位置
        if (null == head) {
            return null;
        }
        if (0 == k) {
            return head;
        }

        ListNode temp = head;
        while (null != temp.next) {
            temp = temp.next;
        }
        temp.next = head;
        //现在从head开始 遍历k个节点
        ListNode cur = head;


        for (int i = 0; i <= k - 1; i++) {
            cur = cur.next;
        }
        ListNode newHead = cur.next;
        cur.next = null;
        return newHead;
    }

    public static class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }


    static class Node {
        /**
         * 当前得到的字符串
         */
        private String res;
        /**
         * 剩余左括号数量
         */
        private int left;
        /**
         * 剩余右括号数量
         */
        private int right;

        public Node(String str, int left, int right) {
            this.res = str;
            this.left = left;
            this.right = right;
        }
    }

    public static List<String> generateParenthesis(int n) {
        List<String> res = new ArrayList<>();
        if (n == 0) {
            return res;
        }
        Queue<Node> queue = new LinkedList<>();
        queue.offer(new Node("", n, n));

        while (!queue.isEmpty()) {

            Node curNode = queue.poll();
            if (curNode.left == 0 && curNode.right == 0) {
                res.add(curNode.res);
            }
            if (curNode.left > 0) {
                queue.offer(new Node(curNode.res + "(", curNode.left - 1, curNode.right));
            }
            if (curNode.right > 0 && curNode.left < curNode.right) {
                queue.offer(new Node(curNode.res + ")", curNode.left, curNode.right - 1));
            }
        }
        return res;
    }

    public String longestCommonPrefix(String[] strs) {

        if (strs.length == 0) {
            return "";
        }

        //默认第一个元素
        String res = strs[0];

        //从第二个元素开始 遍历元素的每个字符 与res比较，缩减res的范围
        for (int i = 1; i <= strs.length - 1; i++) {

            int j = 0;

            while (j < strs[i].length() && j < res.length() && strs[i].charAt(j) == res.charAt(j)) {
                j++;
            }
            res = res.substring(0, j);
        }

        return res;
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
        while (k >= 0) sb.append((char) (C[k--] + '0')); //反转
        return sb.toString();
    }


    public static int findPeakElement(int[] nums) {
        int length = nums.length;
        if (1 == length) {
            return 0;
        }

        // 先特判两边情况
        if (nums[0] > nums[1]) return 0;
        if (nums[length - 1] > nums[length - 2]) return length - 1;

        int left = 0;
        int right = length - 1;

        while (left <= right) {

            int mid = (left + right) / 2;

            if (mid >= 1 & mid < length - 1 && nums[mid - 1] < nums[mid] && nums[mid] > nums[mid + 1]) {
                return mid;
            } else if (mid >= 1 && nums[mid - 1] > nums[mid]) {
                right = mid - 1;
            } else if (mid < length - 1 && nums[mid] < nums[mid + 1]) {
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
        int right = length - 1;

        while (left < right) {

            if (chars[left] != chars[right]) {
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
