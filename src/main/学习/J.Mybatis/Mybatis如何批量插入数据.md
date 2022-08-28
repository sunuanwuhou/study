# Table of Contents

* [一般用法](#一般用法)
* [问题](#问题)
* [推荐方式](#推荐方式)


# 一般用法

```MYSQL
<insert id="batchInsert" parameterType="java.util.List">
    insert into USER (id, name) values
    <foreach collection="list" item="model" index="index" separator=",">
        (#{model.id}, #{model.name})
    </foreach>
</insert>
```

这个方法提升批量插入速度的原理是，将传统的：

```MYSQL
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

如果要优化插入速度时，可以将许多小型操作组合到一个大型操作中。

乍看上去这个foreach没有问题，但是经过项目实践发现，当表的列数较多（20+），以及一次性插入的行数较多（5000+）时，整个插入的耗时十分漫长，达到了14分钟，这是不能忍的

# 问题

从资料中可知，默认执行器类型为Simple，会为每个语句创建一个新的预处理语句，也就是创建一个PreparedStatement对象。

在我们的项目中，会不停地使用批量插入这个方法，而因为MyBatis对于含有<foreach>的语句，无法采用缓存，那么在每次调用方法时，都会重新解析sql语句。

从上述资料可知，耗时就耗在，由于我foreach后有5000+个values，所以这个PreparedStatement特别长，包含了很多占位符，对于占位符和参数的映射尤其耗时。并且，查阅相关资料可知，values的增长与所需的解析时间，是呈指数型增长的。

# 推荐方式

重点来了。上面讲的是，如果非要用<foreach>的方式来插入，可以提升性能的方式。而实际上，MyBatis文档中写批量插入的时候，是推荐使用另外一种方法。（可以看 http://www.mybatis.org/mybatis-dynamic-sql/docs/insert.html 中 Batch Insert Support 标题里的内容）

```java
qlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH);
try {
    SimpleTableMapper mapper = session.getMapper(SimpleTableMapper.class);
    List<SimpleTableRecord> records = getRecordsToInsert(); // not shown
 
    BatchInsert<SimpleTableRecord> batchInsert = insert(records)
            .into(simpleTable)
            .map(id).toProperty("id")
            .map(firstName).toProperty("firstName")
            .map(lastName).toProperty("lastName")
            .map(birthDate).toProperty("birthDate")
            .map(employed).toProperty("employed")
            .map(occupation).toProperty("occupation")
            .build()
            .render(RenderingStrategy.MYBATIS3);
 
    batchInsert.insertStatements().stream().forEach(mapper::insert);
 
    session.commit();
} finally {
    session.close();
}
```

即基本思想是将 MyBatis session 的 executor type 设为 Batch ，然后多次执行插入语句。就类似于JDBC的下面语句一样。

