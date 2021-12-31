package com.qm.study.leetCode;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2021/12/25 15:12
 */
public class 填充每个节点的下一个右侧节点指针 {


    //单节点法
    public Node connect(Node root) {
        if (root == null) {
            return null;
        }
        if (root.left != null) {
            root.left.next = root.right;
            if (root.next != null) {
                root.right.next = root.next.left;
            }
        }
        connect(root.left);
        connect(root.right);
        return root;
    }


    //双节点法
    // public Node connect(Node root) {
    //
    //     if(null==root){
    //         return root;
    //     }
    //
    //     doConnect(root.left,root.right);
    //     return root;
    // }
    //
    // public  void doConnect(Node left,Node right){
    //     if (left == null || right == null) {
    //         return;
    //     }
    //     left.next = right;
    //     doConnect(left.left,left.right);
    //     doConnect(right.left,right.right);
    //     doConnect(left.right,right.left);
    // }
}
