package com.qm.study.dataStructures.recursion;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/6/30 14:49
 */
public class MiGong {

    public static void main(String[] args) {

        //创建迷宫 二维数组 1表示墙
        int[][] migong = new int[8][7];
        bulidArray(migong);
        list(migong);
        System.out.println("===========================");
        setWay(migong, 1, 1, 4, 1);
        list(migong);

    }

    private static void list(int[][] migong) {
        for (int[] ints : migong) {
            for (int i : ints) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }

    /**
     * i j代表从哪个位置开始找 1,1
     * m,n表示已经找到出口
     * 0表示可以走 1表示墙 2走过 3走过但是不通
     * 走顺序定为：下->右->上->左
     */
    public static boolean setWay(int[][] map, int i, int j, int m, int n) {
        if (map[m][n] == 2) {
            return true;
        }
        if (0 == map[i][j]) {//表示
            map[i][j] = 2;
            if (setWay(map, i + 1, j, m, n)) {
                return true;
            } else if (setWay(map, i, j + 1, m, n)) {
                return true;
            } else if (setWay(map, i - 1, j, m, n)) {
                return true;
            } else if (setWay(map, i, j - 1, m, n)) {
                return true;
            } else {
                map[i][j] = 3;
                return false;
            }
        } else {//map[i][j]!=0 1不能走 2别人已经走过 3 走不通
            return false;
        }
    }


    private static void bulidArray(int[][] migong) {
        for (int i = 0; i < 7; i++) {
            migong[0][i] = 1;
            migong[7][i] = 1;
        }
        for (int i = 0; i < 8; i++) {
            migong[i][0] = 1;
            migong[i][6] = 1;
        }
        migong[3][1] = 1;
        migong[3][2] = 1;
    }


}
