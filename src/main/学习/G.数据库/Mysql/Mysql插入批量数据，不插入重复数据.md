# Table of Contents

* [insert ignore into](#insert-ignore-into)
* [on duplicate key update](#on-duplicate-key-update)
* [insert … select … where not exist](#insert--select--where-not-exist)
* [replace into](#replace-into)
* [总结](#总结)




​	业务很简单：需要批量插入一些数据，数据来源可能是其他数据库的表，也可能是一个外部excel的导入。

那么问题来了，是不是每次插入之前都要查一遍，看看重不重复，在代码里筛选一下数据，重复的就过滤掉呢？

向大数据数据库中插入值时，还要判断插入是否重复，然后插入。

如何提高效率？

看来这个问题不止我一个人苦恼过。解决的办法有很多种，不同的场景解决方案也不一样，数据量很小的情况下，怎么搞都行，但是数据量很大的时候，这就不是一个简单的问题了。**几百万的数据，不可能查出来去重处理！**



Mysql底层也有对重复数据的处理方式。





# insert ignore into



当插入数据时，如出现错误时，如重复数据，将不返回错误，只以警告形式返回。所以使用ignore请确保【语句本身没有问题】，否则也会被忽略掉。

```mysql
INSERT IGNORE INTO user (name) VALUES ('telami') 
```





# on duplicate key update



```mysql
INSERT INTO user (name) VALUES ('telami') ON duplicate KEY UPDATE id = id 
```



在主键相同唯一键不同时，则使用主键更新，唯一键相同主键不同时使用唯一键更新 主键与唯一索引也为或者关系



# insert … select … where not exist

这个跟同事也讨论过

```mysql
INSERT INTO user (name) SELECT 'telami' FROM dual WHERE NOT EXISTS (SELECT id FROM user WHERE id = 1) 
```

这种方法其实就是使用了`MySQL`的一个临时表的方式，但是里面使用到了**子查询**，效率也会有一点点影响，如果能使用上面的就不使用这个。

![image-20211217095521069](.images/image-20211217095521069.png)



# replace into

如果存在`primary or unique`相同的记录，则先**删除掉**。再插入新记录。

```mysql
REPLACE INTO user SELECT 1, 'telami' FROM books 
```

这种方法就是不管原来有没有相同的记录，都会先删除掉然后再插入。



# 总结

要看业务场景对数据的要求，一般不改变原有数据用 13，3还是比较常用。
