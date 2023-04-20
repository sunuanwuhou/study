# Table of Contents

  * [前言](#前言)
  * [非流式查询](#非流式查询)
  * [流式查询](#流式查询)
  * [流式查询原理](#流式查询原理)
  * [总结](#总结)
* [参考资料](#参考资料)


## 前言

当指定查询数据过大时，我们一般使用分页查询的方式，一页一页的将数据放到内存处理。但有些情况不需要分页的方式查询数据，如果一下子将数据全部加载出来到内存中，很可能会发生OOM。这时我们可以使用流式查询解决问题。

## 非流式查询

非流式查询表里所有数据代码

```java
 List<InfoPO> infoPOs = infoMapper.selectList(new EntityWrapper<>());
```

通过查看idea控制台，很快出现了内存溢出。

![image-20230420210659077](.images/image-20230420210659077.png)

## 流式查询

流式查询表里所有数据代码

```java
@Select("select * from t_iot")
@Options(resultSetType = ResultSetType.FORWARD_ONLY, fetchSize = Integer.MIN_VALUE)
@ResultType(InfoPO.class)
void selectAutoList(ResultHandler<InfoPO> handler);

```



```java
infoMapper.selectAutoList(resultContext -> {
		//每次查询一条数据 进行数据处理
    resultContext.getResultObject();
});
```





通过查看idea控制台，程序运行正常

## 流式查询原理

查看源码可知，我们使用流式查询时，必须要满足以下3个条件

```java
/**
 * We only stream result sets when they are forward-only, read-only, and the
 * fetch size has been set to Integer.MIN_VALUE
 * 
 * @return true if this result set should be streamed row at-a-time, rather
 *         than read all at once.
 */
protected boolean createStreamingResultSet() {
    return ((this.query.getResultType() == Type.FORWARD_ONLY) && (this.resultSetConcurrency == java.sql.ResultSet.CONCUR_READ_ONLY)
            && (this.query.getResultFetchSize() == Integer.MIN_VALUE));
}
```

1. resultSetConcurrency=ResultSet.CONCUR_READ_ONLY 设置只读结果集

2. resultSetType = ResultSetType.FORWARD_ONLY 设置结果集的游标只能向下滚动

3. fetchSize = Integer.MIN_VALUE 设置fetch size为int的最小值，这里和oracle/db2有区别.

   Oracle/db2是从服务器一次取出fetch size 条记录放在客户端，客户端处理完成一个批次后再向服务器取下一个批次，直到所有数据处理完成。

   mysql在执行ResultSet.next()方法时，会通过数据库连接一条一条的返回。MySQL按照自己的节奏不断的把buffer写回网络中。flush buffer的过程是阻塞式的，也就是说如果网络中发生了拥塞，send buffer被填满，会导致buffer一直flush不出去，那MySQL的处理线程会阻塞，从而避免数据把客户端内存撑爆。

设置三个参数之后，断点进入到了流式返回结果集ResultsetRowsStreaming。

## 总结

之前使用过db2处理流式查询，设置的fetch size为100,没有问题。这次使用mysql刚开始时也设置的100，发现内存溢出了，后来在网上看到mysql流式获取数据的坑，debug进去果然没走到ResultsetRowsStreaming类，设置fetch size 参数为Integer.MIN_VALUE后，才进了ResultsetRowsStreaming类。


# 参考资料

https://segmentfault.com/a/1190000022167975
