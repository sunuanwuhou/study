// package com.qm.study.dataStructures.tree.avl;
//
// /**
//  * @author qiumeng
//  * @version 1.0
//  * @description
//  * @date 2021/8/4 20:36
//  */
// public class AvlDemo {
//
//     public static void main(String[] args) {
//         AVlTree<Integer> tbsTree = new AVlTree<>();
//
//
//         AVlTree.AVLNode<Integer> root = new AVlTree.AVLNode<>(10);
//         tbsTree.setMRoot(root);
//
//
//         AVlTree.AVLNode<Integer> integerAVLNode = new AVlTree.AVLNode<>(11);
//         tbsTree.add(integerAVLNode,root);
//         tbsTree.add(new AVlTree.AVLNode<>(7),root);
//         tbsTree.add(new AVlTree.AVLNode<>(6),root);
//
//         tbsTree.add(new AVlTree.AVLNode<>(8),root);
//         // tbsTree.add(new AVlTree.AVLNode<>(9),root);
//
//
//         System.out.println(root.height());
//         System.out.println(root.leftHeight());
//         System.out.println(root.rightHeight());
//
//
//         System.out.println(tbsTree.height(integerAVLNode));
//         // System.out.println(tbsTree.leftHeight(integerAVLNode));
//         // System.out.println(tbsTree.rightHeight(integerAVLNode));
//
//
//         // System.out.println(tbsTree.leftHeight(integerAVLNode));
//         // tbsTree.infixOrder();
//
//     }
// }
