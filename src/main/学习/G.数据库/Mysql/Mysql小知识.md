# Table of Contents

* [drop、truncate 和 delete 的区别](#droptruncate-和-delete-的区别)
* [为什么删除了表，表文件的大小还是没变？](#为什么删除了表表文件的大小还是没变)
* [如何理解 MySQL 的边读边发](#如何理解-mysql-的边读边发)
  * [MySQL 的大表查询为什么不会爆内存？](#mysql-的大表查询为什么不会爆内存)
* [为什么 MySQL 会抖一下？](#为什么-mysql-会抖一下)




# drop、truncate 和 delete 的区别

- DELETE 语句执行删除的过程是每次从表中删除一行，并且**同时将该行的删除操作作为事务记录在Undo日志中**保存以便进行进行回滚操作。
	  ```mysql
  delete
  from base_water_meter
  where ;
  ```
- TRUNCATE TABLE  则一次性地从表中删除所有的数据并不把单独的删除操作记录记入日志保存，删除行是不能恢复的。并且在删除的过程中不会激活与表有关的删除触发器。执行速度快。
	   ```mysql
   truncate  base_water_meter
   ```

- drop语句将表所占用的空间全释放掉。

  ```mysql
  drop table  ac_access_token_1
  ```

> 在速度上，一般来说，drop> truncate > delete。



- 如果**想删除部分数据**用 delete，注意带上 where 子句，回滚段要足够大；

- 如果**想删除表**，当然用 drop；如果想保留表而将所有数据删除，如果和事务无关，用 truncate 即可；


- 如果和事务有关，或者想触发 trigger，还是用 delete；如果是整理表内部的碎片，可以用 truncate 跟上 reuse stroage，再重新导入/插入数据。



# 为什么删除了表，表文件的大小还是没变？

- 数据项删除之后 InnoDB 某个页 page A 会被标记为可复用。
- delete 命令把整个表的数据删除，结果就是，所有的数据页都会被标记为可复用。但是磁盘上，文件不会变小。
- 经过大量增删改的表，都是可能是存在空洞的。这些空洞也占空间所以，如果能够把这些空洞去掉，就能达到收缩表空间的目的。
- 重建表，就可以达到这样的目的。可以使用 alter table A engine=InnoDB 命令来重建表。

```mysql
 alter table A engine=InnoDB
```





# 如何理解 MySQL 的边读边发

- 如果客户端接受慢，会导致 MySQL 服务端由于结果发不出去，这个事务的执行时间会很长。
- 服务端并不需要保存一个完整的结果集，取数据和发数据的流程都是通过一个 next_buffer 来操作的。
  + 获取一行，写到 net_buffer 中。这块内存的大小是由参数 net_buffer_length 定义的，默认是 16k。
  + 重复获取行，直到 net_buffer 写满，调用网络接口发出去。
  + 如果发送成功，就清空 net_buffer，然后继续取下一行，并写入 net_buffer。
  + 如果发送函数返回 EAGAIN 或 WSAEWOULDBLOCK，就表示本地网络栈（socket send buffer）写满了，进入等待。直到网络栈重新可写，再继续发送。
- 内存的数据页都是在 Buffer_Pool中操作的。
- InnoDB 管理 Buffer_Pool 使用的是改进的 LRU 算法，使用链表实现，实现上，按照 5:3 的比例把整个 LRU 链表分成了 young 区域和 old 区域。

## MySQL 的大表查询为什么不会爆内存？

- 由于 MySQL 是边读变发，因此对于数据量很大的查询结果来说，不会再 server 端保存完整的结果集，所以，如果客户端读结果不及时，会堵住 MySQL 的查询过程，但是不会把内存打爆。
- InnoDB 引擎内部，由于有淘汰策略，InnoDB 管理 Buffer_Pool 使用的是改进的 LRU 算法，使用链表实现，实现上，按照 5:3 的比例把整个 LRU 链表分成了 young 区域和 old 区域。对冷数据的全扫描，影响也能做到可控制。



> 内部：buffer pool的LRU内存淘汰策略
>
> 发送：边读 边发 





# 为什么 MySQL 会抖一下？



脏页会被后台线程自动 flush，也会由于数据页淘汰而触发 flush，而刷脏页的过程由于会占用资源，可能会让你的更新和查询语句的响应时间长一些。

