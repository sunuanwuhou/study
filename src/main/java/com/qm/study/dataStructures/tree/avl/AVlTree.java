// package com.qm.study.dataStructures.tree.avl;
//
// import lombok.Data;
//
// @Data
// public class AVlTree<T> {
//
//     private AVLNode<T> mRoot;    // 根结点
//
//
//     public void preOrder() {
//         if (mRoot != null) {
//             mRoot.preOrder();
//         } else {
//             System.out.println("is empty");
//         }
//     }
//
//     public void infixOrder() {
//         if (mRoot != null) {
//             mRoot.infixOrder();
//         } else {
//             System.out.println("is empty");
//         }
//     }
//
//     public void nextOrder() {
//         if (mRoot != null) {
//             mRoot.nextOrder();
//         } else {
//             System.out.println("is empty");
//         }
//     }
//
//
//     public void leftRotate(AVLNode<Integer> root) {
//
//         AVLNode<Integer> node = new AVLNode<Integer>(root.key);
//         node.left = root.left;
//         node.right = root.right.left;
//         if (null != root.right.left) {
//             root.right.left.parent = node.right;
//         }
//         root.key = root.right.key;
//         root.right = root.right.right;
//         root.right.right.parent = root.right;
//         root.left = node;
//         node.parent = root;
//         mRoot = (AVLNode<T>) root;
//     }
//
//
//     public void rightRotate(AVLNode<Integer> root) {
//
//         AVLNode<Integer> node = new AVLNode<Integer>(root.key);
//         node.right = root.right;
//         node.left = root.left.right;
//         root.key = root.left.key;
//         root.left = root.left.left;
//         root.right = node;
//         mRoot = (AVLNode<T>) root;
//     }
//
//
//     public int leftHeight(AVLNode<Integer> node) {
//         return null == node.left ? 0 : leftHeight(node.left) + 1;
//     }
//
//     public int rightHeight(AVLNode<Integer> node) {
//         return null == node.right ? 0 : rightHeight(node.right) + 1;
//     }
//
//
//     public int height(AVLNode<Integer> node) {
//         return Math.max(node == null ? 0 : height(node.left), node == null ? 0 : height(node.left)) + 1;
//     }
//
//     public void delete(Integer key, AVLNode<Integer> cur) {
//
//         if (cur == null) {
//             return;
//         }
//         if (mRoot.left == null && mRoot.right == null) {
//             return;
//         }
//
//         AVLNode<Integer> serarch = serarch(key, cur);
//         if (null == serarch) {
//             return;
//         }
//         AVLNode<Integer> parent = serarch.parent;
//
//
//         if (serarch.left == null && serarch.right == null) {
//             if (null != parent.left && parent.left == serarch) {
//                 parent.left = null;
//             }
//             if (null != parent.right && parent.right == serarch) {
//                 parent.right = null;
//             }
//         } else if (serarch.left != null && serarch.right != null) {
//
//             AVLNode<Integer> integerBSTNode = delMin(serarch.right);
//             serarch.key = integerBSTNode.key;
//
//         } else {
//             if (serarch.left != null) {
//                 if (parent != null) {
//                     if (null != parent.left && parent.left.key == key) {
//                         parent.left = serarch.left;
//                     } else {
//                         parent.right = serarch.left;
//                     }
//                 } else {
//                     mRoot.key = (T) serarch.left;
//                 }
//
//             } else {
//                 if (parent != null) {
//                     if (null != parent.right && parent.right.key == key) {
//                         parent.right = serarch.right;
//                     } else {
//                         parent.left = serarch.right;
//                     }
//                 } else {
//                     mRoot.key = (T) serarch.right;
//                 }
//
//             }
//         }
//
//     }
//
//
//     public AVLNode<Integer> delMin(AVLNode<Integer> cur) {
//         AVLNode<Integer> target = cur;
//
//         while (target.left != null) {
//             target = target.left;
//         }
//         delete(target.key, cur);
//         return target;
//     }
//
//
//     public AVLNode<Integer> serarchParent(Integer key, AVLNode<Integer> cur) {
//         if (key == null) {
//             return null;
//         }
//
//         if (null != cur.left && key == cur.left.key || null != cur.right && key == cur.right.key) {
//             return cur;
//         } else {
//             if (null != cur.left && key <= cur.key) {
//                 return serarchParent(key, cur.left);
//             } else if (null != cur.right && key >= cur.key) {
//                 return serarchParent(key, cur.right);
//             } else {
//                 return null;
//             }
//         }
//
//
//     }
//
//
//     public AVLNode<Integer> serarch(Integer key, AVLNode<Integer> cur) {
//         if (key == null) {
//             return null;
//         }
//         if (key < cur.key) {
//             return serarch(key, cur.left);
//         } else if (key > cur.key) {
//             return serarch(key, cur.right);
//         } else {
//             return cur;
//         }
//
//     }
//
//     public void add(AVLNode<Integer> bstNode, AVLNode<Integer> cur) {
//         if (null == bstNode) {
//             return;
//         }
//         if (bstNode.key <= cur.key) {
//             if (cur.left == null) {
//                 cur.left = bstNode;
//                 bstNode.parent = cur;
//             } else {
//                 add(bstNode, cur.left);
//             }
//         } else {
//             if (bstNode.key >= cur.key) {
//                 if (cur.right == null) {
//                     cur.right = bstNode;
//                     bstNode.parent = cur;
//                 } else {
//                     add(bstNode, cur.right);
//                 }
//             }
//         }
//         if ((cur.rightHeight() - cur.leftHeight()) > 1) {
//             leftRotate((AVLNode<Integer>) mRoot);
//         }
//         if ((cur.leftHeight() - cur.rightHeight()) > 1) {
//
//             if (null != getMRoot().left && getMRoot().left.right.rightHeight() > getMRoot().left.leftHeight()) {
//                 leftRotate((AVLNode<Integer>) getMRoot().left);
//             }
//             rightRotate((AVLNode<Integer>) mRoot);
//
//         }
//
//
//     }
//
//
//
//
//
//     public static class AVLNode<T> {
//         T key;                // 关键字(键值)
//         AVLNode<T> left;      // 左孩子
//         AVLNode<T> right;     // 右孩子
//         AVLNode<T> parent;    // 父结点
//
//         public AVLNode(T key) {
//             this.key = key;
//         }
//
//         public AVLNode(T key, AVLNode<T> parent, AVLNode<T> left, AVLNode<T> right) {
//             this.key = key;
//             this.parent = parent;
//             this.left = left;
//             this.right = right;
//         }
//
//
//         public int height() {
//             return Math.max(left == null ? 0 : left.height(), right == null ? 0 : right.height()) + 1;
//         }
//
//
//         public int leftHeight() {
//             return null == left ? 0 : left.leftHeight()+1 ;
//         }
//
//         public int rightHeight() {
//             return null == right ? 0 : right.rightHeight()+1;
//         }
//
//
//         public void preOrder() {
//             System.out.println(this.key);
//             if (this.left != null) {
//                 this.left.preOrder();
//             }
//             if (this.right != null) {
//                 this.right.preOrder();
//             }
//         }
//
//         public void infixOrder() {
//             if (this.left != null) {
//                 this.left.infixOrder();
//             }
//             System.out.println(this.key);
//             if (this.right != null) {
//                 this.right.infixOrder();
//             }
//         }
//
//         public void nextOrder() {
//             if (this.left != null) {
//                 this.left.nextOrder();
//             }
//             if (this.right != null) {
//                 this.right.nextOrder();
//             }
//
//             System.out.println(this.key);
//
//         }
//     }
//
// }