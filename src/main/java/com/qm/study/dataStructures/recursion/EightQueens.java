package com.qm.study.dataStructures.recursion;

/**
 * 八皇后问题
 *
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/7/2 14:43
 */
public class EightQueens {

    int max = 8;
    //用来存放皇后 下标：行 值：列 (精华)
    int[] arr = new int[max];

    public static int count = 0;

    public static void main(String[] args) {

        EightQueens eightQueens = new EightQueens();
        eightQueens.setArr(0);
        System.out.println("解法" + count);
    }

    //打印皇后的位置
    public void list() {
        count++;
        for (int i : arr) {
            System.out.print(i + "");
        }
        System.out.println();
    }

    //判断当前皇后和之前皇后是否冲突

    public boolean judge(int n) {

        //一维数组本身就避免了同一行的情况
        for (int i = 0; i < n; i++) {
            if (
                    arr[i] == arr[n] //列是否相等
                            || Math.abs(n - i) == Math.abs(arr[n] - arr[i])//行差=列差
            ) {
                return false;
            }
        }
        return true;
    }


    public void setArr(int n) {
        if (n == max) {//n=8 8个皇后已经放好
            list();
            return;
        }
        for (int i = 0; i < max; i++) {
            //先将皇后放在 改行的第一列
            arr[n] = i;
            if (judge(n)) {//不冲突 下一个皇后
                setArr(n + 1);
            }
        }

    }

}
