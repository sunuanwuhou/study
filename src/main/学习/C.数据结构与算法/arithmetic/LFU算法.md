# Table of Contents

* [算法描述](#算法描述)
* [思路分析：多看几遍](#思路分析多看几遍)
* [参考资料](#参考资料)


# 算法描述

要求你写一个类，接受一个`capacity`参数，实现`get`和`put`方法：

```java
class LFUCache {
    // 构造容量为 capacity 的缓存
    public LFUCache(int capacity) {}
    // 在缓存中查询 key
    public int get(int key) {}
    // 将 key 和 val 存入缓存
    public void put(int key, int val) {}
}
```

`get(key)`方法会去缓存中查询键`key`，如果`key`存在，则返回`key`对应的`val`，否则返回 -1。

`put(key, value)`方法插入或修改缓存。如果`key`已存在，则将它对应的值改为`val`；如果`key`不存在，则插入键值对`(key, val)`。

当缓存达到容量`capacity`时，则应该在插入新的键值对之前，删除使用频次（后文用`freq`表示）最低的键值对。如果`freq`最低的键值对有多个，则删除其中最旧的那个。



# 思路分析：多看几遍

一定先从最简单的开始，根据 LFU 算法的逻辑，我们先列举出算法执行过程中的几个显而易见的事实：

1、调用`get(key)`方法时，要返回该`key`对应的`val`。

2、只要用`get`或者`put`方法访问一次某个`key`，该`key`的`freq`就要加一。

3、如果在容量满了的时候进行插入，则需要将`freq`最小的`key`删除，如果最小的`freq`对应多个`key`，则删除其中最旧的那一个。

好的，我们希望能够在 O(1) 的时间内解决这些需求，可以使用基本数据结构来逐个击破：

**1、**使用一个`HashMap`存储`key`到`val`的映射，就可以快速计算`get(key)`。

```
HashMap<Integer, Integer> keyToVal;
```

**2、**使用一个`HashMap`存储`key`到`freq`的映射，就可以快速操作`key`对应的`freq`。

```
HashMap<Integer, Integer> keyToFreq;
```

**3、**这个需求应该是 LFU 算法的核心，所以我们分开说。

**3.1****、**首先，肯定是需要`freq`到`key`的映射，用来找到`freq`最小的`key`。

**3.2、**将`freq`最小的`key`删除，那你就得快速得到当前所有`key`最小的`freq`是多少。想要时间复杂度 O(1) 的话，肯定不能遍历一遍去找，那就用一个变量`minFreq`来记录当前最小的`freq`吧。

**3.3、**可能有多个`key`拥有相同的`freq`，所以 **`freq`对`key`是一对多的关系**，即一个`freq`对应一个`key`的列表。

**3.4、**希望`freq`对应的`key`的列表是**存在时序**的，便于快速查找并删除最旧的`key`。

**3.5、**希望**能够快速删除`key`列表中的任何一个`key`**，因为如果频次为`freq`的某个`key`被访问，那么它的频次就会变成`freq+1`，就应该从`freq`对应的`key`列表中删除，加到`freq+1`对应的`key`的列表中。

```
HashMap<Integer, LinkedHashSet<Integer>> freqToKeys;
int minFreq = 0;
```

介绍一下这个`LinkedHashSet`，它满足我们 3.3，3.4，3.5 这几个要求。你会发现普通的链表`LinkedList`能够满足 3.3，3.4 这两个要求，但是由于普通链表不能快速访问链表中的某一个节点，所以无法满足 3.5 的要求。

`LinkedHashSet`顾名思义，是链表和哈希集合的结合体。链表不能快速访问链表节点，但是插入元素具有时序；哈希集合中的元素无序，但是可以对元素进行快速的访问和删除。

那么，它俩结合起来就兼具了哈希集合和链表的特性，既可以在 O(1) 时间内访问或删除其中的元素，又可以保持插入的时序，高效实现 3.5 这个需求。

综上，我们可以写出 LFU 算法的基本数据结构：

```java
class LFUCache {
    // key 到 val 的映射，我们后文称为 KV 表
    HashMap<Integer, Integer> keyToVal;
    // key 到 freq 的映射，我们后文称为 KF 表
    HashMap<Integer, Integer> keyToFreq;
    // freq 到 key 列表的映射，我们后文称为 FK 表
    HashMap<Integer, LinkedHashSet<Integer>> freqToKeys;
    // 记录最小的频次
    int minFreq;
    // 记录 LFU 缓存的最大容量
    int cap;

    public LFUCache(int capacity) {
        keyToVal = new HashMap<>();
        keyToFreq = new HashMap<>();
        freqToKeys = new HashMap<>();
        this.cap = capacity;
        this.minFreq = 0;
    }

    public int get(int key) {}

    public void put(int key, int val) {}

}
```





# 参考资料




https://mp.weixin.qq.com/s/oXv03m1J8TwtHwMJEZ1ApQ

