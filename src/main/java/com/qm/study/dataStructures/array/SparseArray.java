package com.qm.study.dataStructures.array;

/**
 * 稀疏数组
 *
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/6/18 15:09
 */
public class SparseArray {


    public static void main(String[] args) {


        //定义一个10*4的二维数组
        int a[][] = new int[11][11];
        a[1][3] = 1;
        a[1][4] = 2;
        a[5][6] = 2;

        //转为稀疏数组
        int sum = 0;
        for (int[] ints : a) {
            for (int anInt : ints) {
                if (0 != anInt) {
                    sum++;
                }
            }
        }

        int b[][] = new int[sum + 1][3];
        b[0][0] = 11;
        b[0][1] = 11;
        b[0][2] = sum;

        int count = 0;
        for (int i = 0; i < 11; i++) {
            for (int j=0; j < 11; j++) {
                if(a[i][j]!=0){
                    count++;
                    b[count][0]=i;
                    b[count][1]=j;
                    b[count][2]=a[i][j];
                }
            }
        }
        for (int[] ints : b) {
            for (int anInt : ints) {
                System.out.print(anInt + "  ");
            }
            System.out.println();
        }


    }


}
