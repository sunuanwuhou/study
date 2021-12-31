package com.qm.study.dataStructures.tree.bt;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/7/30 14:03
 */
public class ArraayBinaryTreeDemo {


    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 5, 6, 7};
        ArrayBinaryTree arrayBinaryTree = new ArrayBinaryTree(arr);
        arrayBinaryTree.preOrder(0);
    }


    static class ArrayBinaryTree {

        private int[] arr;

        public ArrayBinaryTree(int[] arr) {
            this.arr = arr;
        }

        /**
         * @param index 数组下标
         * @description
         */
        public void preOrder(int index) {

            if (null == arr || arr.length == 0) {
                System.out.println();
                return;
            }

            System.out.println(arr[index]);

            if (index * 2 + 1 < arr.length) {
                preOrder(index * 2 + 1);
            }

            if (index * 2 + 2 < arr.length) {
                preOrder(index * 2 + 2);
            }

        }
    }
}
