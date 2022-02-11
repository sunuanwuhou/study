package com.qm.study.arithmetic.sort.HeapSort;

/**
 * 最小堆
 *
 * @author 01399578
 * @version 1.0
 */
public class MinPQ<key extends Comparable<key>> {

    public static void main(String[] args) {
        MinPQ<Integer> keyMaxPQ = new MinPQ<>(7);

        keyMaxPQ.insert(1);
        keyMaxPQ.insert(4);
        keyMaxPQ.insert(7);
        keyMaxPQ.insert(9);
        keyMaxPQ.insert(8);
        keyMaxPQ.insert(10);
        keyMaxPQ.insert(11);

        keyMaxPQ.deleteMin();
        keyMaxPQ.deleteMin();
        keyMaxPQ.list();
    }


    private key[] pq;
    private int num = 0;

    public MinPQ(int cap) {
        //注意 0 不用 所以空间+1
        pq = (key[]) new Comparable[cap + 1];
    }

    /**
     * 最小的元素
     *
     * @return key
     */
    public key min() {
        return pq[1];
    }


    public int left(int k) {
        return k * 2;
    }

    public int right(int k) {
        return (k * 2) + 1;
    }

    public int parent(int k) {
        return k / 2;
    }


    public boolean less(int k, int i) {
        return pq[k].compareTo(pq[i]) <0;
    }

    public void exch(int k, int i) {
        key temp = pq[k];
        pq[k] = pq[i];
        pq[i] = temp;
    }

    //插入最后 然后上浮
    public void insert(key key) {
        num++;
        pq[num] = key;
        swim(num);
    }

    //将第一个与最后一个交换 然后删除最后一个 下沉第一个
    public key deleteMin() {
        key min = pq[1];
        exch(1, num);
        pq[num] = null;
        num--;
        sink(1);
        return min;
    }

    //上浮
    public void swim(int k) {
        while (k > 1 && less(k,parent(k))) {
            exch(k, parent(k));
            k = parent(k);
        }
    }

    //下沉
    public void sink(int k) {
        while (left(k) <= num) {
            int left = left(k);
            int right = right(k);
            //比较左右节点最小的 与之交换
            if (right <= num && less(right,left)) {
                left = right;
            }
            //左右最大 比k大 没必要下沉了
            if (less(k,left)) {
                break;
            }
            exch(k, left);
            k = left;
        }
    }

    void list() {

        for (key key : pq) {
            if(null!=key){
                System.out.println(key);
            }
        }
    }




}
