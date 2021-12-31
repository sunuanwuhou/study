package com.qm.study.arithmetic.dp;

import java.util.Arrays;

/**
 * 背包问题  不重复
 *
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/8/18 8:59
 */
public class KnapsackProblem {

    public static void main(String[] args) {
        int[] w = {1, 4, 3};//物品重量
        int[] v = {1500, 3000, 2000};//物品的价值
        int c = 4;//背包容量
        int n = w.length;//物品个数


        // 前i个物品能够装入容量为J的背包的最大价值
        int[][] dp = new int[n + 1][c + 1];

        //初始化第一行和第一列为0  可不处理
        for (int i = 1; i < dp.length; i++) {

            for (int j = 1; j < dp[i].length; j++) {

                if (w[i - 1] > j) {
                    dp[i][j] = dp[i - 1][j];
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], v[i - 1] + dp[i - 1][j - w[i - 1]]);
                }
            }
        }

        for (int[] ints : dp) {
            System.out.println(Arrays.toString(ints));
        }
    }
}
