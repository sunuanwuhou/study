package com.qm.study.leetCode;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2021/12/20 15:44
 */
public class 旋转图像 {

    public void rotate(int[][] matrix) {


        //对角线折叠
        for (int i = 0; i <= matrix.length - 1; i++) {

            for (int j = i; j <= matrix.length - 1; j++) {

                int temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }
        //行 反转

        for (int[] ints : matrix) {

            int i = 0;
            int j = ints.length - 1;

            while (i < j) {

                int temp = ints[i];
                ints[i] = ints[j];
                ints[j] = temp;
                i++;
                j--;
            }
        }

    }
}
