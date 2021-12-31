package com.qm.study.dataStructures.tree.bst;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2021/12/24 9:53
 */
public class BSTUtils {


    /**
     * 翻转二叉树
     *
     * @param bstNode
     */
    public static BSTree.BSTNode reverse(BSTree.BSTNode bstNode) {

        if(null==bstNode){
            return null;
        }
        reverse(bstNode.left);

        //交换位置
        BSTree.BSTNode left = bstNode.left;
        BSTree.BSTNode temp = left;
        bstNode.left = bstNode.right;
        bstNode.right = temp;


        reverse(bstNode.right);

        return bstNode;
    }

}
