# Table of Contents

* [sql相关](#sql相关)
  * [exists](#exists)
  * [Group By](#group-by)
* [死锁问题](#死锁问题)





# sql相关

## exists
exists存在主要是代替in不走索引的问题

```mysql
select *
from tt_send_place_box a
where exists(select a.send_job_task_id
             from tt_send_place_box b
             where b.box_no = 'PAG40005O3'
               and b.send_job_task_id = '3202111282356099'
              #  查询完 是要匹配的
               and a.send_job_task_id = b.send_job_task_id
          );
```



## Group By


分组后，取第一条或者最后一天数据数据，

+ 方法一

```mysql
select send_task_id, max(operation_flag) as operation_flag
from ti_send_get_task
where icreate_tm > '2021-12-01'
group by send_task_id;
```

+ 方法二

```mysql
select *
from (select *
      from ti_send_get_task
      where icreate_tm > '2021-11-01'
      order by icreate_tm desc) tt
group by tt.send_task_id;

```



>  问题来了，如果我要取倒数第二条数据呢？



提供一种解决思路，用limit来解决，不过不知道效率怎么样。

```java
//取第一条数据后的 1条数据
select *
from ti_send_get_task where send_task_id='2202110312317272' limit 1,1;

```







# 死锁问题

[死锁问题](死锁问题分析.md)
