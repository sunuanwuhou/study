package com.qm.study.dataStructures.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/8/9 20:41
 */
public class Graph {


    //顶点
    private ArrayList<String> vertexList;

    //图
    private int[][] edges;

    //边
    private int num;

    private static boolean[] isVisited;

    public static void main(String[] args) {

        int n = 5;

        Graph graph = new Graph(5);

        graph.inserList("A");
        graph.inserList("B");
        graph.inserList("C");
        graph.inserList("D");
        graph.inserList("E");


        graph.inserNum(0, 1, 1);
        graph.inserNum(0, 2, 1);
        graph.inserNum(1, 2, 1);
        graph.inserNum(1, 3, 1);
        graph.inserNum(1, 4, 1);


        // graph.showGRaph();

        graph.bfs();
    }


    /**
     * 初始化
     *
     * @param n
     */
    public Graph(int n) {
        edges = new int[n][n];
        vertexList = new ArrayList<>(n);
        num = 0;
        isVisited = new boolean[n];
    }


    /**
     * @param index 第一个邻接节点下标
     * @return 返回对于下标 否则返回-1
     */
    public int getIndex(int index) {
        for (int i = 0; i <= vertexList.size() - 1; i++) {
            if (edges[index][i] > 0) {
                return i;
            }
        }
        return -1;
    }

    public int getNextIndex(int v1, int v2) {
        for (int i = v2 + 1; i <= vertexList.size() - 1; i++) {
            if (edges[v1][i] > 0) {
                return i;
            }
        }
        return -1;
    }


    public void bfs() {
        for (int i = 0; i <= vertexList.size() - 1; i++) {
            if (!isVisited[i]) {
                bfs(i);
            }
        }
    }

    public void bfs(int i) {

        LinkedList<Integer> queue = new LinkedList<>();
        System.out.print(vertexList.get(i) + "->");
        isVisited[i] = true;

        queue.addLast(i);
        while (!queue.isEmpty()) {
            Integer pop = queue.pop();

            //找到当前节点的第一个邻接节点
            int index = getIndex(pop);

            while (-1 != index) {
                if (!isVisited[index]) {
                    System.out.print(vertexList.get(index) + "->");
                    isVisited[index] = true;
                    queue.addLast(index);
                }
                //找到当前节点的第一个邻接节点的邻接节点
                index = getNextIndex(pop, index);
            }
        }
    }

    public void dfs() {
        for (int i = 0; i <= vertexList.size() - 1; i++) {
            if (!isVisited[i]) {
                dfs(i);
            }
        }
    }

    public void dfs(int i) {
        System.out.print(vertexList.get(i) + "->");
        isVisited[i] = true;
        //获取第一个节点下标
        int index = getIndex(i);
        while (index != -1) {
            if (!isVisited[index]) {
                dfs(index);
            }
            //获取当前节点的下一个邻接节点
            index = getNextIndex(i, index);
        }
    }


    public void inserList(String vertex) {
        vertexList.add(vertex);
    }


    public int getNumOfVertex() {
        return vertexList.size();
    }

    public void showGRaph() {

        for (int[] ints : edges) {
            System.out.println(Arrays.toString(ints));
        }

    }


    public void inserNum(int v1, int v2, int weight) {
        edges[v1][v2] = weight;
        edges[v2][v1] = weight;
        num++;
    }

}
