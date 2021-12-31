package com.qm.study.dataStructures.hash;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/7/21 21:22
 */
public class HashMapDemo {


    public static void main(String[] args) {
        hashTable hashTable = new hashTable(16);

        hashTable.add(new Emp(6));
        hashTable.add(new Emp(87));
        hashTable.list();

    }


    static class hashTable {
        private static linkList[] arr;
        private static int size;

        public hashTable(int size) {
            arr = new linkList[size];
            size = size;
            //要初始化每一条链表
            for (int i = 0; i <= size - 1; i++) {
                arr[i] = new linkList();
            }
        }


        public void add(Emp emp) {

            int hash = hash(emp.getId());
            arr[hash].add(emp);

        }

        public int hash(int num) {
            return num % 6;
        }


        //遍历所有链表
        public void list() {
            for (linkList list : arr) {
                list.list();
            }
        }
    }


    static class linkList {
        private Emp head;
        private Emp tail;

        public void add(Emp emp) {
            if (null == head) {
                head = emp;
                tail = emp;
                return;
            }
            tail.next = emp;
            tail = emp;

        }

        public void list() {
            if (null != head) {
                Emp current = head;
                while (current != null) {
                    System.out.println(current.id);
                    current = current.next;
                }
            }
        }


    }


    static class Emp {
        public int id;
        public String name;
        public Emp next;

        public Emp(int id) {
            this.id = id;
        }

        public Emp(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

}
