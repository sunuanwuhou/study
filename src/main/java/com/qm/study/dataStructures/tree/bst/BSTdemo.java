// package com.qm.study.dataStructures.tree.bst;
//
// /**
//  * @author qiumeng
//  * @version 1.0
//  * @description
//  * @date 2021/7/23 17:34
//  */
// public class BSTdemo {
//
//
//     public static void main(String[] args) {
//
//         BSTree<Integer> tbsTree = new BSTree<>();
//
//
//         BSTree.BSTNode<Integer> root = new BSTree.BSTNode<>(4);
//         tbsTree.setMRoot(root);
//         // BSTree.BSTNode<Integer> root2 = new BSTree.BSTNode<>(3);
//         // BSTree.BSTNode<Integer> root3 = new BSTree.BSTNode<>(10);
//         // BSTree.BSTNode<Integer> root4 = new BSTree.BSTNode<>(1);
//         // BSTree.BSTNode<Integer> root5 = new BSTree.BSTNode<>(6);
//         // BSTree.BSTNode<Integer> root6 = new BSTree.BSTNode<>(14);
//
//
//
//         // tbsTree.add(root2,root);
//         // // tbsTree.add(root3,root);
//         // tbsTree.add(root4,root);
//         // tbsTree.add(root5,root);
//         // tbsTree.add(root6,root);
//         tbsTree.add(new BSTree.BSTNode<>(2),root);
//         tbsTree.add(new BSTree.BSTNode<>(7),root);
//         tbsTree.add(new BSTree.BSTNode<>(1),root);
//         tbsTree.add(new BSTree.BSTNode<>(3),root);
//         tbsTree.add(new BSTree.BSTNode<>(6),root);
//         tbsTree.add(new BSTree.BSTNode<>(9),root);
//
//
//         BSTree.BSTNode reverse = BSTUtils.reverse(tbsTree.getMRoot());
//
//
//         reverse.preOrder();
//
//
//         // System.out.println(root.left.height());
//         // System.out.println(root.left.leftHeight());
//         // System.out.println(root.left.rightHeight());
//
//         // System.out.println(tbsTree.height(root.left));
//         // System.out.println(tbsTree.leftHeight(root.left));
//         // System.out.println(tbsTree.rightHeight(root.left));
//
//
//
//         // tbsTree.add(new BSTree.BSTNode<>(13),root);
//
//         // tbsTree.delete(8,root);
//         // tbsTree.infixOrder();
//
//
//         // BSTree.BSTNode<Integer> serarch = tbsTree.serarch(13, root);
//         // BSTree.BSTNode<Integer> serarch = tbsTree.serarchParent(13, root);
//         // System.out.println(serarch.key);
//     }
//
//
//
// }
