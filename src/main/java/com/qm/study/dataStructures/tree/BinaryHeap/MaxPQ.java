package com.qm.study.dataStructures.tree.BinaryHeap;

/**
 * 二叉堆
 *
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2021/11/9 16:53
 */
public class MaxPQ<Key extends Comparable<Key>> {

    public static void main(String[] args) {

        MaxPQ<Integer> keyMaxPQ = new MaxPQ<>(7);

        keyMaxPQ.insert(1);
        keyMaxPQ.insert(4);
        keyMaxPQ.insert(7);
        keyMaxPQ.insert(8);
        keyMaxPQ.insert(9);
        keyMaxPQ.insert(10);
        keyMaxPQ.insert(11);

        keyMaxPQ.list();
    }


    // 存储元素的数组
    private Key[] pq;
    // 当前 Priority Queue 中的元素个数
    private int N = 0;


    public MaxPQ(int cap) {
        // 索引 0 不用，所以多分配一个空间
        pq = (Key[]) new Comparable[cap + 1];
    }

    /* 返回当前队列中最大元素 */
    public Key max() {
        return pq[1];
    }

    /* 插入元素 e */
    public void insert(Key e) {
        N++;
        // 先把新元素加到最后
        pq[N] = e;
        // 然后让它上浮到正确的位置
        swim(N);
    }

    /* 删除并返回当前队列中最大元素 */
    public Key delMax() {
        // 最大堆的堆顶就是最大元素
        Key max = pq[1];
        // 把这个最大元素换到最后，删除之
        exch(1, N);
        pq[N] = null;
        N--;
        // 让 pq[1] 下沉到正确位置
        sink(1);
        return max;
    }

    /* 上浮第 k 个元素，以维护最大堆性质 */
    private void swim(int k) {
        while (k > 1 && less(parent(k), k)) {
            exch(k, parent(k));
            //数据是换了 但是还要继续上浮
            k = parent(k);
        }
    }

    /* 下沉第 k 个元素，以维护最大堆性质 */
    private void sink(int k) {

        while (left(k) <= N) {

            int left = left(k);
            int right = right(k);
            //比较 左右子节点
            if (less(left, right)) {
                left = right;
            }
            //不大于父节点
            if (less(left, k)) {
                break;
            }
            //然后交换 父子节点和子节点
            exch(k, left);
            k = left;
        }
    }

    /* 交换数组的两个元素 */
    private void exch(int i, int j) {
        Key temp = pq[i];
        pq[i] = pq[j];
        pq[j] = temp;
    }

    /* pq[i] 是否比 pq[j] 小？ */
    private boolean less(int i, int j) {
        return pq[i].compareTo(pq[j]) < 0;
    }


    // 父节点的索引
    int parent(int root) {
        return root / 2;
    }

    // 左孩子的索引
    int left(int root) {
        return root * 2;
    }

    // 右孩子的索引
    int right(int root) {
        return root * 2 + 1;
    }

    void list(){
        for (Key key : pq) {
            System.out.println(key);
        }
    }
}
