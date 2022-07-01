



# **什么是分表？**

分表是将**一个大表按照一定的规则分解成多张具有独立存储空间的实体表**，我们可以称为子表，每个表都对应三个文件，MYD数据文件，.MYI索引文件，.frm表结构文件。这些子表可以分布在同一块磁盘上，也可以在不同的机器上。app读写的时候根据事先定义好的规则得到对应的子表名，然后去操作它。



# **什么是分区？**

分区和分表相似，都是按照规则分解表。**不同在于分表将大表分解为若干个独立的实体表，而分区是将数据分段划分在多个位置存放**，可以是同一块磁盘也可以在不同的机器。分区后，表面上还是一张表，但数据散列到多个位置了。app读写的时候操作的还是大表名字，db自动去组织分区的数据。



## 水平分区

这种形式分区是**对表的行**进行分区，通过这样的方式不同分组里面的物理列分割的数据集得以组合，从而进行个体分割（单分区）或集体分割（1个或多个分区）。所有在表中定义的列在每个数据集中都能找到，

所以表的特性依然得以保持。

举个简单例子：一个包含十年发票记录的表可以被分区为十个不同的分区，每个分区包含的是其中一年的记录。

## 垂直分区

> 一般来说，垂直分区都是业务层面来划分的。

这种分区方式一般来说是通过**对表的垂直**划分来减少目标表的宽度，使某些特定的列被划分到特定的分区，每个分区都包含了其中的列所对应的行。

举个简单例子：一个包含了大text和BLOB列的表，这些text和BLOB列又不经常被访问，这时候就要把这些不经常使用的text和BLOB了划分到另一个分区，在保证它们数据相关性的同时还能提高访问速度。



## 分区表原理

分区表是由多个相关的底层表实现，这些底层表也是由句柄对象表示，所以我们也可以直接访问各个分区，存储引擎管理分区的各个底层表和管理普通表一样（所有的底层表都必须使用相同的存储引擎），分区表的索引只是在各个底层表上各自加上一个相同的索引，从存储引擎的角度来看，底层表和一个普通表没有任何不同，存储引擎也无须知道这是一个普通表还是一个分区表的一部分。

在分区表上的操作按照下面的操作逻辑进行：

select查询：

当查询一个分区表的时候，分区层先打开并锁住所有的底层表，优化器判断是否可以过滤部分分区，然后再调用对应的存储引擎接口访问各个分区的数据

insert操作：

当写入一条记录时，分区层打开并锁住所有的底层表，然后确定哪个分区接受这条记录，再将记录写入对应的底层表

delete操作：

当删除一条记录时，分区层先打开并锁住所有的底层表，然后确定数据对应的分区，最后对相应底层表进行删除操作

update操作：

当更新一条数据时，分区层先打开并锁住所有的底层表，mysql先确定需要更新的记录在哪个分区，然后取出数据并更新，再判断更新后的数据应该放在哪个分区，然后对底层表进行写入操作，并对原数据所在的底层表进行删除操作

虽然每个操作都会打开并锁住所有的底层表，但这并不是说分区表在处理过程中是锁住全表的，如果存储引擎能够自己实现行级锁，如：[innodb](https://www.zhihu.com/search?q=innodb&search_source=Entity&hybrid_search_source=Entity&hybrid_search_extra={"sourceType"%3A"answer"%2C"sourceId"%3A579911085})，则会在分区层释放对应的表锁，这个加锁和解锁过程与普通Innodb上的查询类似。



# 分区相关命令

+ 新增分区命令

  + 建表语句为分区

  ```mysql
  CREATE TABLE `test`
  (
      `id`          BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
      `biz_time`    YEAR(4)             NOT NULL COMMENT '业务年份',
      `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
      PRIMARY KEY (`id`, `biz_time`)
  ) ENGINE = InnoDB
    AUTO_INCREMENT = 1
    DEFAULT CHARSET = utf8mb4 COMMENT ='test'
      PARTITION BY RANGE (`biz_time`)
          (PARTITION p2021 VALUES LESS THAN (2021) ENGINE = InnoDB,
          PARTITION p2022 VALUES LESS THAN (2022) ENGINE = InnoDB,
          PARTITION p2023 VALUES LESS THAN (2023) ENGINE = InnoDB,
          PARTITION p2024 VALUES LESS THAN (2024) ENGINE = InnoDB);
  
  ```
  + 修改表为分区表

    ```mysql
    
    ALTER TABLE 表名 PATITION BY RANGE COLUMNS (字段名)
        
        (PARTITION pxxx VALUES LESS THAN (aaa),
    PARTITION pxxx VALUES LESS THAN (bbb));
    ```

+ 删除分区

  ```mysql
  
  ALTER TABLE test
  DROP PARTITION pxxx
  ```

  



# 参考资料

https://www.zhihu.com/question/38418707/answer/579911085