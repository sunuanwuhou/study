# Table of Contents

* [**什么是分表？**](#什么是分表)
* [**什么是分区？**](#什么是分区)
* [使用分区的劣势](#使用分区的劣势)
* [使用分区的优势](#使用分区的优势)
* [啥时候适合使用分区表？](#啥时候适合使用分区表)
* [分区相关命令](#分区相关命令)
* [参考资料](#参考资料)






# **什么是分表？**

分表是将**一个大表按照一定的规则分解成多张具有独立存储空间的实体表**，我们可以称为子表，每个表都对应三个文件，MYD数据文件，.MYI索引文件，.frm表结构文件。这些子表可以分布在同一块磁盘上，也可以在不同的机器上。app读写的时候根据事先定义好的规则得到对应的子表名，然后去操作它。



# **什么是分区？**

```mysql
CREATE TABLE `t` (
`ftime` datetime NOT NULL,
`c` int(11) DEFAULT NULL,
KEY (`ftime`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1
PARTITION BY RANGE (YEAR(ftime))
(PARTITION p_2017 VALUES LESS THAN (2017) ENGINE = InnoDB,
PARTITION p_2018 VALUES LESS THAN (2018) ENGINE = InnoDB,
PARTITION p_2019 VALUES LESS THAN (2019) ENGINE = InnoDB,
PARTITION p_others VALUES LESS THAN MAXVALUE ENGINE = InnoDB);
insert into t values('2017-4-1',1),('2018-4-1',1);
```

![image-20220726204735589](.images/image-20220726204735589.png)



我在表t中初始化插入了两行记录，按照定义的分区规则，这两行记录分别落在p_2018和p_2019这两个分区上。

可以看到，这个表包含了一个.frm文件和4个.ibd文件，每个分区对应一个.ibd文件。也就是
说：

+ **对于引擎层来说，这是4个表；**
+ **对于Server层来说，这是1个表 ；**

你可能会觉得这两句都是废话。其实不然，这两句话非常重要，可以帮我们理解分区表的执
行逻辑。  



# 使用分区的劣势

+ MySQL 在**第一次**打开分区表的时候，需要**访问所有的分区**——打开的表较多，性能糟
  糕也可能报打开的表超过设置的问题。  
+ 所有分区公用**一个MDL**锁。
+ 在引擎层，认为这是不同的表，因此MDL锁之后的执行过程，**会根据分区表规则，只访问
  必要的分区**



# 使用分区的优势

分区表的一个显而易见的优势是对业务透明，相对于用户分表来说，使用分区表的业务代码
更简洁。还有，分区表可以很方便的清理历史数据。  



# 啥时候适合使用分区表？  

单表过大时，使用时注意一下两点

+ **分区并不是越细越好。**实际上，单表或者单分区的数据一千万行，只要没有特别大的索
  引，对于现在的硬件能力来说都已经是小表了。
+ **分区也不要提前预留太多，在使用之前预先创建即可。**比如，如果是按月分区，每年年
  底时再把下一年度的 12 个新分区创建上即可。对于没有数据的历史分区，要及时的 drop
  掉。  





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

+  MySQL 查询指定分区数据

    ```mysql
   select *
   from t partition (p_2017,p_2018,p_2019);
    ```

   

# 参考资料

+ https://www.zhihu.com/question/38418707/answer/579911085
+ Mysql45讲
