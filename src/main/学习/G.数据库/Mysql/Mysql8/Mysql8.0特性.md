# Table of Contents

* [性能优化](#性能优化)
  * [Hash Join](#hash-join)
    * [如何使用](#如何使用)
  * [倒序索引](#倒序索引)
  * [函数索引/表达式索引](#函数索引表达式索引)
  * [不可见索引](#不可见索引)
* [字段存储](#字段存储)
  * [存储Json](#存储json)
    * [如何查询](#如何查询)
    * [MP使用](#mp使用)
* [快速加列(了解)](#快速加列了解)
* [直方图(了解)](#直方图了解)




# 性能优化

## Hash Join

我们先看看原来的NestLoopJoin算法简单来说，就是双重循环，遍历外表(驱动表)，对于外表的每一行记录，然后遍历内表，然后判断join条件是否符合，进而确定是否将记录吐出给上一个执行节点。从算法角度来说，这是一个M*N的复杂度。



从算法角度来说，这是一个M*N的复杂度。HashJoin是针对equal-join场景的优化，**基本思想是，将外表数据load到内存，并建立hash表，这样只需要遍历一遍内表，就可以完成join操作**，输出匹配的记录。如果数据能全部load到内存当然好，逻辑也简单，一般称这种join为CHJ(Classic Hash Join)

> 其实我们写代码的时候，也经常这么搞，减少与数据库的交互。



原理介绍：https://blog.csdn.net/qq_35423190/article/details/120504960



### 如何使用

Hash join 不需要索引的支持。

```java
EXPLAIN
SELECT *
    FROM t1
    JOIN t2
        ON t1.c1=t2.c1;

```

![image-20221015121250951](.images/image-20221015121250951.png)

表之间没有建立索引，也会走join buffer。



## 倒序索引

MySQL长期以来对索引的建立只允许正向asc存储，就算建立了desc，也是忽略掉。

但是我们业务开发界面查询一般都是根据更新时间进行倒叙查看。就会多一次using filesort。就有点坑。

如何使用：

```mysql
ALTER TABLE A ADD INDEX 'Idx_time_desc'('time' DESC)
```

## 函数索引/表达式索引

这个也很好解决了之前版本，对加索引的字段进行函数，导致索引失效的问题。

不过函数索引也有自己的缺陷，**就是写法很固定，必须要严格按照定义的函数来写，不然优化器不知所措。**



```mysql
SELECT AVG(price) FROM products WHERE MONTH(create_time)=10; //无法利用索引

ALTER TABLE products ADD INDEX((MONTH(create_time)));
```



## 不可见索引

删个索引，想删又不太敢动手，怎么破？不可见索引来救命。改为不可见后，观察一阵子，确认没影响了再放心删除吧。

所谓不可见，指的是**对于查询优化器不可见**，SQL在执行时自然也不会选择，但在查看表结构的时候，索引仍然能够看到，也可以通过information_schema.statistics或者show index来查看索引是否可见的状态。



```mysql
create index flag_index on t1(flag) invisible;

```



引入不可见索引的目的，主要是为了减小对于表上的索引进行调整时的潜在风险。





# 字段存储

## 存储Json

![image-20221015144509240](.images/image-20221015144509240.png)


### 如何查询

https://blog.csdn.net/nangy2514/article/details/98490082

### MP使用

1. 实体类标注

   ```java
   @TableName(value ="tablename",autoResultMap = true)
   public class DictData
   ```

   autoResultMap ：是否自动构建 resultMap 并使用(如果设置 resultMap 则不会进行 resultMap 的自动构建并注入)(@since 3.1.2)

2. 在对应实体的属性值上添加

   ```java
   @TableField(typeHandler = JacksonTypeHandler.class)
   private Map<String,String> dictLabelI18n;
   ```



# 快速加列(了解)

MySQL 8.0.12 的版本中，官方为 Online DDL 操作添加了 instant 算法，使得添加列时不再需要 rebuild 整个表，只需要在表的 metadata 中记录新增列的基本信息即可。



# 直方图(了解)

**通过ANALYZE操作了解到，在数据库中查询优化所需的指标抽取方式。有时候，查询优化器会走不到最优的执行计划，导致花费了更多不必要的时间。直方图就是解决这样的问题.**

传送门：https://blog.csdn.net/dreamyuzhou/article/details/117482483



