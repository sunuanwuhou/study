package com.qm.study.leetCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/11/24 7:50
 */
public class 螺旋矩阵 {


    public static void main(String[] args) {

        int [ ][ ]  arr={{1,2,3,4},{5,6,7,8},{9,10,11,12}};

        System.out.println(spiralOrder(arr));


    }

    public static List<Integer> spiralOrder(int[][] matrix) {

        List<Integer> result = new ArrayList();

        if (null == matrix) {
            return result;
        }
        int top = 0;
        int bottom = matrix.length - 1;
        int left = 0;
        int right = matrix[0].length - 1;

        while (true) {

            //从左到右
            for (int i = left; i <= right; i++) {
                result.add(matrix[top][i]);
            }
            top++;
            if (left > right || top > bottom) {
                break;
            }
            //从上到下
            for (int i = top; i <= bottom; i++) {
                result.add(matrix[i][right]);
            }
            right--;
            if (left > right || top > bottom) {
                break;
            }

            //从右到左
            for (int i = right; left <= i; i--) {
                result.add(matrix[bottom][i]);
            }
            bottom--;
            if (left > right || top > bottom) {
                break;
            }

            //从下到上

            for (int i = bottom; top <= i; i--) {
                result.add(matrix[i][left]);
            }
            left++;
            if (left > right || top > bottom) {
                break;
            }
        }

        return result;

    }
}
