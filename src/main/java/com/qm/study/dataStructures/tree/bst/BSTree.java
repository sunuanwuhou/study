package com.qm.study.dataStructures.tree.bst;

import lombok.Data;

@Data
public class BSTree<T> {

    private BSTNode<T> mRoot;    // 根结点


    public void preOrder() {
        if (mRoot != null) {
            mRoot.preOrder();
        } else {
            System.out.println("is empty");
        }
    }

    public void infixOrder() {
        if (mRoot != null) {
            mRoot.infixOrder();
        } else {
            System.out.println("is empty");
        }
    }

    public void nextOrder() {
        if (mRoot != null) {
            mRoot.nextOrder();
        } else {
            System.out.println("is empty");
        }
    }


    public void delete(Integer key, BSTNode<Integer> cur) {

        if (cur == null) {
            return;
        }
        if (mRoot.left == null && mRoot.right == null) {
            return;
        }

        BSTNode<Integer> serarch = serarch(key, cur);
        if (null == serarch) {
            return;
        }
        BSTNode<Integer> parent = serarch.parent;


        if (serarch.left == null && serarch.right == null) {
            if (null != parent.left && parent.left == serarch) {
                parent.left = null;
            }
            if (null != parent.right && parent.right == serarch) {
                parent.right = null;
            }
        } else if (serarch.left != null && serarch.right != null) {

            BSTNode<Integer> integerBSTNode = delMin(serarch.right);
            serarch.key = integerBSTNode.key;

        } else {
            if (serarch.left != null) {
                if(parent!=null){
                    if (null != parent.left && parent.left.key == key) {
                        parent.left = serarch.left;
                    } else {
                        parent.right = serarch.left;
                    }
                }else {
                    mRoot.key = (T) serarch.left;
                }

            } else {
                if(parent!=null){
                    if (null != parent.right && parent.right.key == key) {
                        parent.right = serarch.right;
                    } else {
                        parent.left = serarch.right;
                    }
                }else {
                    mRoot.key = (T) serarch.right;
                }

            }
        }

    }


    public BSTNode<Integer> delMin( BSTNode<Integer> cur){
        BSTNode<Integer> target = cur;

        while (target.left!=null){
            target = target.left;
        }
        delete(target.key,cur);
        return target;
    }


    public BSTNode<Integer> serarchParent(Integer key, BSTNode<Integer> cur) {
        if (key == null) {
            return null;
        }

        if (null != cur.left && key == cur.left.key || null != cur.right && key == cur.right.key) {
            return cur;
        } else {
            if (null != cur.left && key <= cur.key) {
                return serarchParent(key, cur.left);
            } else if (null != cur.right && key >= cur.key) {
                return serarchParent(key, cur.right);
            } else {
                return null;
            }
        }


    }


    public BSTNode<Integer> serarch(Integer key, BSTNode<Integer> cur) {
        if (key == null) {
            return null;
        }
        if (key < cur.key) {
            return serarch(key, cur.left);
        } else if (key > cur.key) {
            return serarch(key, cur.right);
        } else {
            return cur;
        }

    }

    public int leftHeight(BSTNode<Integer> node) {
        return null == node.left ? 0 : leftHeight(node.left) + 1;
    }

    public int rightHeight(BSTNode<Integer> node) {
        return null == node.right ? 0 : rightHeight(node.right) + 1;
    }


    public int height(BSTNode<Integer> node) {
        return Math.max(node == null ? 0 : height(node.left), node == null ? 0 : height(node.left)) + 1;
    }


    public void add(BSTNode<Integer> bstNode, BSTNode<Integer> cur) {
        if (null == bstNode) {
            return;
        }
        if (bstNode.key <= cur.key) {
            if (cur.left == null) {
                cur.left = bstNode;
                bstNode.parent = cur;
            } else {
                add(bstNode, cur.left);
            }
        } else {
            if (bstNode.key >= cur.key) {
                if (cur.right == null) {
                    cur.right = bstNode;
                    bstNode.parent = cur;
                } else {
                    add(bstNode, cur.right);
                }
            }
        }
    }


    public static class BSTNode<T> {
        T key;                // 关键字(键值)
        BSTNode<T> left;      // 左孩子
        BSTNode<T> right;     // 右孩子
        BSTNode<T> parent;    // 父结点

        public BSTNode(T key) {
            this.key = key;
        }

        public BSTNode(T key, BSTNode<T> parent, BSTNode<T> left, BSTNode<T> right) {
            this.key = key;
            this.parent = parent;
            this.left = left;
            this.right = right;
        }




        public int height() {
            return Math.max(left == null ? 0 : left.height(), right == null ? 0 : right.height()) + 1;
        }


        public int leftHeight() {
            return null == left ? 0 : left.leftHeight()+1 ;
        }

        public int rightHeight() {
            return null == right ? 0 : right.rightHeight()+1;
        }



        //前序遍历
        public void preOrder() {
            System.out.println(this.key);
            if (this.left != null) {
                this.left.preOrder();
            }
            if (this.right != null) {
                this.right.preOrder();
            }
        }

        //中序遍历
        public void infixOrder() {
            if (this.left != null) {
                this.left.infixOrder();
            }
            System.out.println(this.key);
            if (this.right != null) {
                this.right.infixOrder();
            }
        }
        //后序遍历
        public void nextOrder() {
            if (this.left != null) {
                this.left.nextOrder();
            }
            if (this.right != null) {
                this.right.nextOrder();
            }

            System.out.println(this.key);

        }
    }

}