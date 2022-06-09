# Table of Contents

* [Too many connections（连接数过多，导致连接不上数据库，业务无法正常进行）](#too-many-connections连接数过多导致连接不上数据库业务无法正常进行)
* [truncate 删除数据，导致自动清空自增ID，前端返回报错 not found。](#truncate-删除数据导致自动清空自增id前端返回报错-not-found)
* [参考资料](#参考资料)








# Too many connections（连接数过多，导致连接不上数据库，业务无法正常进行）



+  问题还原

```
mysql> show variables like '%max_connection%';
| Variable_name   | Value |
max_connections | 151   | 
mysql> set global max_connections=1;Query OK, 0 rows affected (0.00 sec)
[root@node4 ~]# mysql -uzs -p123456 -h 192.168.56.132
ERROR 1040 (00000): Too many connections
```

+ 解决问题的思路：

  + 首先先要考虑在我们 MySQL 数据库参数文件里面，对应的`max_connections 这个参数值是不是设置的太小了`，导致客户端连接数超过了数据库所承受的最大值。
    该值默认大小是151，我们可以根据实际情况进行调整。
    对应解决办法：set global max_connections=500
    但这样调整会有隐患，因为我们无法确认数据库是否可以承担这么大的连接压力，就好比原来一个人只能吃一个馒头，但现在却非要让他吃 10 个，他肯定接受不了。反应到服务器上面，就有可能会出现宕机的可能。
    所以这又反应出了，我们在新上线一个业务系统的时候，要做好压力测试。保证后期对数据库进行优化调整。
  + 其次可以`限制Innodb 的并发处理数量`，如果 innodb_thread_concurrency = 0（这种代表不受限制） 可以先改成 16或是64 看服务器压力。如果非常大，可以先改的小一点让服务器的压力下来之后,然后再慢慢增大,根据自己的业务而定。个人建议可以先调整为 16 即可。
    [MySQL ](http://mp.weixin.qq.com/s?__biz=MzAwMTE3MDY4MQ==&mid=2652457504&idx=2&sn=a0c3becc0b323cb2d895c884570ee7b0&chksm=813027b6b647aea0b54d0680afec71c2c06ba3771e45c685c87777b0af0ec70ac7794365e031&scene=21#wechat_redirect)随着连接数的增加性能是会下降的，可以让开发配合设置 thread pool，连接复用。在MySQL商业版中加入了thread pool这项功能,另外对于有的监控程序会读取 information_schema 下面的表，可以考虑关闭下面的参数。

  



# truncate 删除数据，导致自动清空自增ID，前端返回报错 not found。

这个问题的出现，就要`考虑下truncate 和 delete 的区别了。`

结果发现truncate把自增初始值重置了，自增属性从1开始记录了。当前端用主键id进行查询时，就会报没有这条数据的错误。
个人建议不要使用truncate对表进行删除操作，虽然可以回收表空间，但是会涉及自增属性问题。这些坑，我们不要轻易钻进去。





# 参考资料




https://mp.weixin.qq.com/s/NAKO0l4UoxenYp5-vqueXw
