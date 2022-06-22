# Table of Contents

* [使用模板](#使用模板)
* [exists查询方式](#exists查询方式)
* [exists和in的比较](#exists和in的比较)


在开始学mysql或者背八股文的时候，经常听到用`exists`代替`in`,那么背后到底是什么原因？`exists`一定比`in`好吗？

# 使用模板

```mysql
select *
from a
where exists
          (
              select emp_no
              from b
              where a.emp_no = b.emp_no

```

# exists查询方式

> 首先执行外层查询，再执行内层查询，与IN相反。

+ 首先取出外层中的第一元组
+ 再执行内层查询
+ 将外层表的第一元组代入，若内层查询为真，即有结果时.返回外层表中的第一元组
+ 接着取出第二元组，执行相同的算法。一直到扫描完外层整表 。

# exists和in的比较

>  in的查询方式：本质就是 or 

在了解exists和in的查询方式后，就会得出

+ **外表大而子表小时，IN的效率更高，而外表小，子表大时，EXISTS的效率更高，若两表差不多大，则差不多**。

不过网上也有很多争论，网上有个博客用实际数据跑了一波。

https://blog.csdn.net/leisure_life/article/details/120758666

结果缺出人意料。

**个人建以：还是以自己实际工作中环境数据压测的结果最准。**

