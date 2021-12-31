# Table of Contents

  * [总结](#总结)


## 总结

- `InnoDB`以表为单位来收集统计数据，这些统计数据可以是基于磁盘的永久性统计数据，也可以是基于内存的非永久性统计数据。

- `innodb_stats_persistent`控制着使用永久性统计数据还是非永久性统计数据；`innodb_stats_persistent_sample_pages`控制着永久性统计数据的采样页面数量；`innodb_stats_transient_sample_pages`控制着非永久性统计数据的采样页面数量；`innodb_stats_auto_recalc`控制着是否自动重新计算统计数据。

- 我们可以针对某个具体的表，在创建和修改表时通过指定`STATS_PERSISTENT`、`STATS_AUTO_RECALC`、`STATS_SAMPLE_PAGES`的值来控制相关统计数据属性。

- `innodb_stats_method`决定着在统计某个索引列不重复值的数量时如何对待`NULL`值。

  - `nulls_equal`：认为所有`NULL`值都是相等的。这个值也是`innodb_stats_method`的默认值。

    如果某个索引列中`NULL`值特别多的话，这种统计方式会让优化器认为某个列中平均一个值重复次数特别多，所以倾向于不使用索引进行访问。

  - `nulls_unequal`：认为所有`NULL`值都是不相等的。

    如果某个索引列中`NULL`值特别多的话，这种统计方式会让优化器认为某个列中平均一个值重复次数特别少，所以倾向于使用索引进行访问。

  - `nulls_ignored`：直接把`NULL`值忽略掉。
