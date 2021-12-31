package com.qm.study.dataStructures.tree.bt;

import lombok.Data;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/8/2 8:01
 */
@Data
public class ThreadedBinaryTreeDemo {


    public static void main(String[] args) {

    }

    class BinaryTree {
        private TreeNode root;

        private TreeNode pre;

        /**
         * 对节点进行线索化
         *
         * @param treeNode
         */
        public void threadedNode(TreeNode treeNode) {

            if(null==treeNode){
                return;
            }



        }
    }

    class TreeNode {
        private int no;
        private TreeNode left;
        private TreeNode right;

        /**
         * 0 数 1 前驱节点
         */
        private int leftType;
        private int rightType;

        public TreeNode() {
        }

        public TreeNode(int no) {
            this.no = no;
        }
    }


}
