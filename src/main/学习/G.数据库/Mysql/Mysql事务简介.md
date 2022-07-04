# Table of Contents

* [事务起源](#事务起源)
* [原子性(Atomicity)](#原子性atomicity)
* [一致性（Consistency）](#一致性consistency)
* [隔离性(Isolation)](#隔离性isolation)
* [持久性](#持久性)
* [Mysql事务语法](#mysql事务语法)
  * [开启事务](#开启事务)
  * [提交事务](#提交事务)
  * [手动中止事务](#手动中止事务)
  * [自动提交](#自动提交)
  * [隐式提交](#隐式提交)
  * [保存点](#保存点)
* [总结](#总结)






# 事务起源

对于大部分程序员来说，他们的任务就是把现实世界的业务场景映射到数据库世界。

对于转账来说

比如现在狗哥有`11`元，猫爷只有`2`元，那么现实中的这个情况映射到数据库的`account`表就是这样：

```mysql
+----+--------+---------+
| id | name   | balance |
+----+--------+---------+
|  1 | 狗哥   |      11 |
|  2 | 猫爷   |       2 |
+----+--------+---------+
```

猫爷在问狗哥借10元

```mysql
UPDATE account SET balance = balance - 10 WHERE id = 1;
UPDATE account SET balance = balance + 10 WHERE id = 2;
```

但是这里头有个问题，上述两条语句只执行了一条时忽然服务器断电了咋办？

前面提到，在对某个页面进行读写访问时，都会先把这个页面加载到`Buffer Pool`中，之后如果修改了某个页面，也不会立即把修改同步到磁盘，而只是把这个修改了的页面加到`Buffer Pool`的`flush链表`中，在之后的某个时间点才会刷新到磁盘。

断电了怎么办？断电后怎么恢复？那些数据需要恢复？这些Mysql是怎么解决的？



# 原子性(Atomicity)

现实世界中转账操作是一个不可分割的操作。


事务被视为不可分割的最小单元，事务的所有操作要么全部提交成功，要么全部失败回滚。

<font color=red>MySQL事务的原子性是通过undo log来实现的</font>。undo log是InnoDB存储引擎特有的。具体的实现机制是：将所有对数据的修改（增、删、改）都写入日志（undo log）。

# 一致性（Consistency）

符合现实中的逻辑，我支付了100块，对方就得收到100。

数据库在事务执行前后都保持一致性状态。<font color=red>在一致性状态下，所有事务对一个数据的读取结果都是相同的</font>。 参考分布式事务，容易理解些

<font color=red>可以这么说原子性、隔离性、持久性来保证一致性</font>

# 隔离性(Isolation)


现实世界中的两次状态转换应该是互不影响的

一个事务所做的修改在最终提交以前，对其它事务是不可见的。

<font color=red>MySQL事务的隔离性是通过锁和MVCC来实现的</font>

# 持久性

当现实世界的一个状态转换完成后，这个转换的结果将永久的保留。



一旦事务提交，则其所做的修改将会永远保存到数据库中。即使系统发生崩溃，事务执行的结果也不能丢失。

可以通过数据库备份和恢复来实现，在系统发生崩溃时，使用备份的数据库进行数据恢复。

<font color=red>MySQL事务的持久性是通过redo log来实现的</font>



# Mysql事务语法

## 开启事务

我们可以使用下边两种语句之一来开启一个事务：

- `BEGIN [WORK];`

  `BEGIN`语句代表开启一个事务，后边的单词`WORK`可有可无。开启事务后，就可以继续写若干条语句，这些语句都属于刚刚开启的这个事务。

  ```
  mysql> BEGIN;
  Query OK, 0 rows affected (0.00 sec)
  
  mysql> 加入事务的语句...
  ```

- `START TRANSACTION;`

  `START TRANSACTION`语句和`BEGIN`语句有着相同的功效，都标志着开启一个事务，比如这样：

  ```
  mysql> START TRANSACTION;
  Query OK, 0 rows affected (0.00 sec)
  
  mysql> 加入事务的语句...
  ```

  不过比`BEGIN`语句牛逼一点儿的是，可以在`START TRANSACTION`语句后边跟随几个`修饰符`，就是它们几个：

  - `READ ONLY`：标识当前事务是一个只读事务，也就是属于该事务的数据库操作只能读取数据，而不能修改数据。

    > 小贴士：其实只读事务中只是不允许修改那些其他事务也能访问到的表中的数据，对于临时表来说（我们使用CREATE TMEPORARY TABLE创建的表），由于它们只能在当前会话中可见，所以只读事务其实也是可以对临时表进行增、删、改操作的。

  - `READ WRITE`：标识当前事务是一个读写事务，也就是属于该事务的数据库操作既可以读取数据，也可以修改数据。

  - `WITH CONSISTENT SNAPSHOT`：启动一致性读（先不用关心啥是个一致性读，后边的章节才会唠叨）。

  比如我们想开启一个只读事务的话，直接把`READ ONLY`这个修饰符加在`START TRANSACTION`语句后边就好，比如这样：

  ```
  START TRANSACTION READ ONLY;
  ```

  如果我们想在`START TRANSACTION`后边跟随多个`修饰符`的话，可以使用逗号将`修饰符`分开，比如开启一个只读事务和一致性读，就可以这样写：

  ```
  START TRANSACTION READ ONLY, WITH CONSISTENT SNAPSHOT;
  ```

  或者开启一个读写事务和一致性读，就可以这样写：

  ```
  START TRANSACTION READ WRITE, WITH CONSISTENT SNAPSHOT
  ```

  不过这里需要大家注意的一点是，`READ ONLY`和`READ WRITE`是用来设置所谓的事务`访问模式`的，就是以只读还是读写的方式来访问数据库中的数据，一个事务的访问模式不能同时既设置为`只读`的也设置为`读写`的，所以我们不能同时把`READ ONLY`和`READ WRITE`放到`START TRANSACTION`语句后边。

  **另外，如果我们不显式指定事务的访问模式，那么该事务的访问模式就是`读写`模式。**

## 提交事务

开启事务之后就可以继续写需要放到该事务中的语句了，当最后一条语句写完了之后，我们就可以提交该事务了，提交的语句也很简单：

```
COMMIT [WORK]
```

`COMMIT`语句就代表提交一个事务，后边的`WORK`可有可无。比如我们上边说狗哥给猫爷转10元钱其实对应`MySQL`中的两条语句，我们就可以把这两条语句放到一个事务中，完整的过程就是这样：

```
mysql> BEGIN;
Query OK, 0 rows affected (0.00 sec)

mysql> UPDATE account SET balance = balance - 10 WHERE id = 1;
Query OK, 1 row affected (0.02 sec)
Rows matched: 1  Changed: 1  Warnings: 0

mysql> UPDATE account SET balance = balance + 10 WHERE id = 2;
Query OK, 1 row affected (0.00 sec)
Rows matched: 1  Changed: 1  Warnings: 0

mysql> COMMIT;
Query OK, 0 rows affected (0.00 sec)
```

## 手动中止事务

如果我们写了几条语句之后发现上边的某条语句写错了，我们可以手动的使用下边这个语句来将数据库恢复到事务执行之前的样子：

```
ROLLBACK [WORK]
```

`ROLLBACK`语句就代表中止并回滚一个事务，后边的`WORK`可有可无类似的。比如我们在写狗哥给猫爷转账10元钱对应的`MySQL`语句时，先给狗哥扣了10元，然后一时大意只给猫爷账户上增加了1元，此时就可以使用`ROLLBACK`语句进行回滚，完整的过程就是这样：

```
mysql> BEGIN;
Query OK, 0 rows affected (0.00 sec)

mysql> UPDATE account SET balance = balance - 10 WHERE id = 1;
Query OK, 1 row affected (0.00 sec)
Rows matched: 1  Changed: 1  Warnings: 0

mysql> UPDATE account SET balance = balance + 1 WHERE id = 2;
Query OK, 1 row affected (0.00 sec)
Rows matched: 1  Changed: 1  Warnings: 0

mysql> ROLLBACK;
Query OK, 0 rows affected (0.00 sec)
```

这里需要强调一下，`ROLLBACK`语句是我们程序员手动的去回滚事务时才去使用的，如果事务在执行过程中遇到了某些错误而无法继续执行的话，事务自身会自动的回滚。

> 小贴士： 我们这里所说的开启、提交、中止事务的语法只是针对使用黑框框时通过mysql客户端程序与服务器进行交互时控制事务的语法，如果大家使用的是别的客户端程序，比如JDBC之类的，那需要参考相应的文档来看看如何控制事务。



## 自动提交

`MySQL`中有一个系统变量`autocommit`：

```
mysql> SHOW VARIABLES LIKE 'autocommit';
+---------------+-------+
| Variable_name | Value |
+---------------+-------+
| autocommit    | ON    |
+---------------+-------+
1 row in set (0.01 sec)
```

可以看到它的默认值为`ON`，也就是说默认情况下，如果我们不显式的使用`START TRANSACTION`或者`BEGIN`语句开启一个事务，那么每一条语句都算是一个独立的事务，这种特性称之为事务的`自动提交`。假如我们在狗哥向猫爷转账10元时不以`START TRANSACTION`或者`BEGIN`语句显式的开启一个事务，那么下边这两条语句就相当于放到两个独立的事务中去执行：

```
UPDATE account SET balance = balance - 10 WHERE id = 1;
UPDATE account SET balance = balance + 10 WHERE id = 2;
```

当然，如果我们想关闭这种`自动提交`的功能，可以使用下边两种方法之一：

- 显式的的使用`START TRANSACTION`或者`BEGIN`语句开启一个事务。

  这样在本次事务提交或者回滚前会暂时关闭掉自动提交的功能。

- 把系统变量`autocommit`的值设置为`OFF`，就像这样：

  ```
  SET autocommit = OFF;
  ```

  这样的话，我们写入的多条语句就算是属于同一个事务了，直到我们显式的写出`COMMIT`语句来把这个事务提交掉，或者显式的写出`ROLLBACK`语句来把这个事务回滚掉。

## 隐式提交

当我们使用`START TRANSACTION`或者`BEGIN`语句开启了一个事务，或者把系统变量`autocommit`的值设置为`OFF`时，事务就不会进行`自动提交`，但是如果我们输入了某些语句之后就会`悄悄的`提交掉，就像我们输入了`COMMIT`语句了一样，这种因为某些特殊的语句而导致事务提交的情况称为`隐式提交`，这些会导致事务隐式提交的语句包括：

- 定义或修改数据库对象的数据定义语言（Data definition language，缩写为：`DDL`）。

  所谓的数据库对象，指的就是`数据库`、`表`、`视图`、`存储过程`等等这些东西。当我们使用`CREATE`、`ALTER`、`DROP`等语句去修改这些所谓的数据库对象时，就会隐式的提交前边语句所属于的事务，就像这样：

  ```
  BEGIN;
  
  SELECT ... # 事务中的一条语句
  UPDATE ... # 事务中的一条语句
  ... # 事务中的其它语句
  
  CREATE TABLE ... # 此语句会隐式的提交前边语句所属于的事务
  ```

- 隐式使用或修改`mysql`数据库中的表

  当我们使用`ALTER USER`、`CREATE USER`、`DROP USER`、`GRANT`、`RENAME USER`、`REVOKE`、`SET PASSWORD`等语句时也会隐式的提交前边语句所属于的事务。

- 事务控制或关于锁定的语句

  当我们在一个事务还没提交或者回滚时就又使用`START TRANSACTION`或者`BEGIN`语句开启了另一个事务时，会隐式的提交上一个事务，比如这样：

  ```
  BEGIN;
  
  SELECT ... # 事务中的一条语句
  UPDATE ... # 事务中的一条语句
  ... # 事务中的其它语句
  
  BEGIN; # 此语句会隐式的提交前边语句所属于的事务
  ```

  或者当前的`autocommit`系统变量的值为`OFF`，我们手动把它调为`ON`时，也会隐式的提交前边语句所属的事务。

  或者使用`LOCK TABLES`、`UNLOCK TABLES`等关于锁定的语句也会隐式的提交前边语句所属的事务。

- 加载数据的语句

  比如我们使用`LOAD DATA`语句来批量往数据库中导入数据时，也会隐式的提交前边语句所属的事务。

- 关于`MySQL`复制的一些语句

  使用`START SLAVE`、`STOP SLAVE`、`RESET SLAVE`、`CHANGE MASTER TO`等语句时也会隐式的提交前边语句所属的事务。

- 其它的一些语句

  使用`ANALYZE TABLE`、`CACHE INDEX`、`CHECK TABLE`、`FLUSH`、 `LOAD INDEX INTO CACHE`、`OPTIMIZE TABLE`、`REPAIR TABLE`、`RESET`等语句也会隐式的提交前边语句所属的事务。



## 保存点



```
ROLLBACK [WORK] TO [SAVEPOINT] 保存点名称;
```

不过如果`ROLLBACK`语句后边不跟随保存点名称的话，会直接回滚到事务执行之前的状态。



> 类似于git的提交记录
>
> 如何查看保存点？

# 总结

事务的 ACID 特性概念简单，但不是很好理解，<font color=red>主要是因为这几个特性不是一种平级关系</font>:

- 只有满足一致性，事务的执行结果才是正确的。
- 在无并发的情况下，事务串行执行，隔离性一定能够满足。此时只要能满足原子性，就一定能满足一致性。
- 在并发的情况下，多个事务并行执行，事务不仅要满足原子性，还需要满足隔离性，才能满足一致性。
- 事务满足持久化是为了能应对数据库崩溃的情况。
