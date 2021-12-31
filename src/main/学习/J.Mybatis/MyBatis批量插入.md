# Table of Contents

* [背景](#背景)
* [为什么慢？](#为什么慢)
* [总结](#总结)








# 背景

近日，项目中有一个耗时较长的Job存在CPU占用过高的问题，经排查发现，主要时间消耗在往MyBatis中批量插入数据。mapper configuration是用foreach循环做的，差不多是这样。



```mysql
<insert id="batchInsert" parameterType="java.util.List">
    insert into USER (id, name) values
    <foreach collection="list" item="model" index="index" separator=","> 
        (#{model.id}, #{model.name})
    </foreach>
</insert>
```



这个方法提升批量插入速度的原理是，将传统的：

```mysql
INSERT INTO `table1` (`field1`, `field2`) VALUES ("data1", "data2");
INSERT INTO `table1` (`field1`, `field2`) VALUES ("data1", "data2");
INSERT INTO `table1` (`field1`, `field2`) VALUES ("data1", "data2");
INSERT INTO `table1` (`field1`, `field2`) VALUES ("data1", "data2");
INSERT INTO `table1` (`field1`, `field2`) VALUES ("data1", "data2");
```

转化为：

```mysql
INSERT INTO `table1` (`field1`, `field2`) 
VALUES ("data1", "data2"),
("data1", "data2"),
("data1", "data2"),
("data1", "data2"),
("data1", "data2");
```



# 为什么慢？


官方解答

> 默认执行器类型为Simple，会为每个语句创建一个新的预处理语句，也就是创建一个PreparedStatement对象。在我们的项目中，会不停地使用批量插入这个方法，而因为MyBatis对于含有`<foreach>`的语句，无法采用缓存，那么在每次调用方法时，都会重新解析sql语句。
>
> 耗时就耗在，由于我foreach后有5000+个values，所以这个PreparedStatement特别长，包含了很多占位符，对于占位符和参数的映射尤其耗时。并且，查阅相关资料可知，**values的增长与所需的解析时间，是呈指数型增长的。**




# 总结

总结一下，如果MyBatis需要进行批量插入，**推荐使用 ExecutorType.BATCH 的插入方式**，如果非要使用 `<foreach>`的插入的话，需要将每次插入的记录控制在 20~50 左右。



