# Table of Contents

* [为什么引入LinkHashMap](#为什么引入linkhashmap)




# 为什么引入LinkHashMap

> 其实就是想要一个既能快速插入和删除，又能保持顺序的一种数据结构

在使用HashMap的时候，可能会遇到需要按照当时put的顺序来进行哈希表的遍历。通过上篇对HashMap的了解，我们知道HashMap中不存在保存顺序的机制。本篇文章要介绍的LinkedHashMap专为此特性而生。在LinkedHashMap中可以保持两种顺序，分别是插入顺序和访问顺序，这个是可以在LinkedHashMap的初始化方法中进行指定的。相对于访问顺序，按照插入顺序进行编排被使用到的场景更多一些，所以默认是按照插入顺序进行编排。



![](.images/下载.png)
