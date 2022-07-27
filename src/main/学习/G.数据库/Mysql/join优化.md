# Table of Contents

* [Multi-Range Read优化-基础知识](#multi-range-read优化-基础知识)
* [Batched Key Access](#batched-key-access)
* [BNL算法性能问题](#bnl算法性能问题)
* [BNL转BKA](#bnl转bka)
* [join用法](#join用法)
* [总结](#总结)


在上一篇文章中，我和你介绍了join语句的两种算法，分别是Index Nested-Loop Join(NLJ)和Block Nested-Loop Join(BNL)。
我们发现在使用NLJ算法的时候，其实效果还是不错的，比通过应用层拆分成多个语句然后再 拼接查询结果更方便，而且性能也不会差。

但是，BNL算法在大表join的时候性能就差多了，比较次数等于两个表参与join的行数的乘 积，很消耗CPU资源。 当然了，这两个算法都还有继续优化的空间，我们今天就来聊聊这个话题。

# Multi-Range Read优化-基础知识

Multi-Range Read优化(MRR)。这个优化的主要目的是尽量使用顺序读盘。

我们先看一个问题：

回表过程是一行行地查数据，还是批量地查数据？

```mysql
select * from t1 where a>=1 and a<=100;
```

**主键索引是一棵B+树，在这棵树上，每次只能根据一个主键id查到一行数据。因此，回表肯定是一行行搜索主键索引的**。

如果随着a的值递增顺序查询的话，id的值就变成随机的，那么就会出现随机访问，性能相对较差。虽然“按行查”这个机制不能改，但是调整查询的顺序，还是能够加速的。

因为大多数的数据都是按照主键递增顺序插入得到的，所以我们可以认为，如果按照主键的递增顺序查询的话，对磁盘的读比较接近顺序读，能够提升读性能。这，就是MRR优化的设计思路。此时，语句的执行流程变成了这样：

1. 根据索引a，定位到满足条件的记录，将id值放入read_rnd_buffer中;
2. **将read_rnd_buffer中的id进行递增排序；**
3. 排序后的id数组，依次到主键id索引中查记录，并作为结果返回。

另外需要说明的是，如果你想要稳定地使用MRR优化的话，需要设置setoptimizer_switch="mrr_cost_based=off"
。（官方文档的说法，是现在的优化器策略，判断消耗的时候，会更倾向于不使用MRR，把mrr_cost_based设置为off，就是固定使 用MRR了。）

> 也就是MRR默认是不开启的。

# Batched Key Access

NLJ算法执行的逻辑是：从驱动表t1，一行行地取出a的值，再到被驱动表t2去做join。也就是说，对于表t2来说，每次都是匹配一个值。这时，MRR的优势就用不上了。

那怎么才能一次性地多传些值给表t2呢？方法就是，从表t1里一次性地多拿些行出来，一起传给表t2。

既然如此，我们就把表t1的数据取出来一部分，先放到一个临时内存。这个临时内存不是别人，就是join_buffer。

那么，这个BKA算法到底要怎么启用呢？

如果要使用BKA优化算法的话，你需要在执行SQL语句之前，先设置

```mysql
set optimizer_switch='mrr=on,mrr_cost_based=off,batched_key_access=on';
```

**其中，前两个参数的作用是要启用MRR。这么做的原因是，BKA算法的优化要依赖于MRR。**

# BNL算法性能问题

BNL算法对系统的影响主要包括三个方面：

1. 可能会多次扫描被驱动表，占用磁盘IO资源；
2. 判断join条件需要执行M*N次对比（M、N分别是两张表的行数），如果是大表就会占用 非常多的CPU资源；
3. 可能会导致Buffer Pool的热数据被淘汰，影响内存命中率。

# BNL转BKA

+ 一些情况下，我们可以直接在被驱动表上建索引，这时就可以直接转成BKA算法了。

但是，有时候你确实会碰到一些不适合在被驱动表上建索引的情况。

> 不要刚，肯定有

+ 我们可以用临时表的方式，

使用临时表的大致思路是：

1. 把表t2中满足条件的数据放在临时表tmp_t中；
2. 为了让join使用BKA算法，给临时表tmp_t的字段b加上索引；
3. 让表t1和tmp_t做join操作。

```mysql
create temporary table temp_t(id int primary key, a int, b int, index(b))engine=innodb;
insert into temp_t select * from t2 where b>=1 and b<=2000;
select * from t1 join temp_t on (t1.b=temp_t.b);
```

+ 扩展-hash join

  看到这里你可能发现了，其实上面计算10亿次那个操作，看上去有点儿傻。如果join_buffer 里面维护的不是一个无序数组，而是一个哈希表的话，那么就不是10亿次判断，而是100万次
  hash查找。
  
  这样的话，整条语句的执行速度就快多了吧？ 
  
  确实如此。 这，也正是MySQL的优化器和执行器一直被诟病的一个原因：不支持哈希join。并且， MySQL官方的roadmap，也是迟迟没有把这个优化排上议程。
  
  
  
  实际上，这个优化思路，我们可以自己实现在业务端。实现流程大致如下：
  1. select * from t1;取得表t1的全部1000行数据，在业务端存入一个hash结构，比如
  C++里的set、PHP的数组这样的数据结构。
  2. select * from t2 where b>=1 and b<=2000; 获取表t2中满足条件的2000行数
  据。
  3. 把这2000行数据，一行一行地取到业务端，到hash结构的数据表中寻找匹配的数据。满足
  匹配的条件的这行数据，就作为结果集的一行。



# join用法

1. 如果用left join的话，左边的表一定是驱动表吗？

   > 不一定，看sql优化器后的语句。

2.  如果两个表的join包含多个条件的等值匹配，是都要写到on里面呢，还是只把一个条件写
   到on里面，其他条件写到where部分  。

   > on后面是做匹配用的
   >
   > where是匹配完后在过滤

# 总结

1. 回表的时候，一行一行随机读取主键索引，为了顺序化。回表前，将数据放入buffer，按照id排序。再去查找主键索引数据。---MRR
2. **MRR默认不开启。**个人理解，查出数据本身就是连续的就很少。
3. BKA依赖MRR.。本质是将驱动表的数据批量放在buffer中，再去匹配被驱动表数据。
4. BNL转BKA
   1. 加索引
   2. 不适合加索引，可以创建临时表，给临时表加索引，在关联。
   3. mysql不支持hash join。可以业务代码处理。
