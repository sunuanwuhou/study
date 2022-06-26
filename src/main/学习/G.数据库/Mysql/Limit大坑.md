# Table of Contents

* [**两种limit的执行过程**](#两种limit的执行过程)
* [如何优化呢](#如何优化呢)
* [总结](#总结)
* [参考资料](#参考资料)




建表语句

![image-20220626190030985](.images/image-20220626190030985.png)

第一页就是下面这样的sql语句。

```
select * from page order by id limit 0, 10;
```

第一百页就是

```
select * from page order by id limit 990, 10;
```

那么问题来了。

用这种方式，**同样都是拿10条数据，查第一页和第一百页的查询速度是一样的吗？为什么？**

# **两种limit的执行过程**

上面的两种查询方式。对应 `limit offset, size` 和 `limit size` 两种方式。

而其实 `limit size` ，相当于  `limit 0, size`。也就是从0开始取size条数据。

也就是说，两种方式的**区别在于offset是否为0。**

执行这条sql。

```
select * from page order by id limit 0, 10;
```

上面select后面带的是**星号***，也就是要求获得行数据的**所有字段信息。**

server层会调用innodb的接口，在innodb里的主键索引中获取到第0到10条**完整行数据**，依次返回给server层，并放到server层的结果集中，返回给客户端。

而当我们把offset搞离谱点，比如执行的是

```
select * from page order by id limit 6000000, 10;
```

server层会调用innodb的接口，由于这次的offset=6000000，会在innodb里的主键索引中获取到第0到（6000000 + 10）条**完整行数据**，**返回给server层之后根据offset的值挨个抛弃，最后只留下最后面的size条**，也就是10条数据，放到server层的结果集中，返回给客户端。

> 这一段是重点哦

可以看出，当offset非0时，server层会从引擎层获取到**很多无用的数据**，而获取的这些无用数据都是要耗时的。

因此，我们就知道了文章开头的问题的答案，**mysql查询中 limit 1000,10 会比 limit 10 更慢。原因是 limit 1000,10 会取出1000+10条数据，并抛弃前1000条，这部分耗时更大**



# 如何优化呢

因为前面的offset条数据最后都是不要的，就算将完整字段都拷贝来了又有什么用呢，所以我们可以将sql语句修改成下面这样。

```mysql
select * from page  where id >=(select id from page  order by id limit 6000000, 1) order by id limit 10;
```

这个id也可以由前台直接传入

```mysql
select * from page  where id >=(6000000) order by id limit 10;
```





# 总结

1. limit offest,size,mysql调取引擎获取数据时，会获取(offest+size)数据，然后在抛弃offest条数据。

   ```mysql
   limit 10  查到10条就返回
   limit 10，20 查到10+20，舍弃10条。这里就可以看出优化，主要就是快读定位10这个的起始位置。
   ```

2. 当offset过大，会引发**深度分页**问题，目前不管是mysql还是es都没有很好的方法去解决这个问题。只能通过限制查询数量或分批获取的方式进行规避。

3. 遇到深度分页的问题，多思考其原始需求，大部分时候是不应该出现深度分页的场景的，必要时多去影响产品经理。

4. 优化方向：

   + 产品上绕过，使用上一页 下一页的功能。

   + 类似抖音：做成瀑布流

     

     

# 参考资料

+ https://mp.weixin.qq.com/s/BG1b6qB2K9qcaAaXMkRjYQ
+ https://mp.weixin.qq.com/s?__biz=MzUxODAzNDg4NQ==&mid=2247493642&idx=2&sn=715241c1519eba973d0086e0d27244aa&scene=21#wechat_redirect
