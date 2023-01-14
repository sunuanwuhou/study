# Table of Contents

* [Multimap - 多值Map(常用)](#multimap---多值map常用)
  * [转换为Map](#转换为map)
  * [数量问题](#数量问题)
* [双键Map](#双键map)
* [双向Map](#双向map)
* [RangeMap - 范围Map](#rangemap---范围map)
* [参考资料](#参考资料)


# Multimap - 多值Map(常用)

java中的`Map`维护的是键值一对一的关系，如果要将一个键映射到多个值上，那么就只能把值的内容设为集合形式，简单实现如下：

```java
Map<String, List<Integer>> map=new HashMap<>();
List<Integer> list=new ArrayList<>();
list.add(1);
list.add(2);
map.put("day",list);
```

guava中的`Multimap`提供了将一个键映射到多个值的形式，使用起来无需定义复杂的内层集合，可以像使用普通的`Map`一样使用它，定义及放入数据如下：

```java
Multimap<String, Integer> multimap = ArrayListMultimap.create();
multimap.put("day",1);
multimap.put("day",2);
multimap.put("day",8);
multimap.put("month",3);
```

打印这个`Multimap`的内容，可以直观的看到每个`key`对应的都是一个集合：

```properties
{month=[3], day=[1, 2, 8]}
```



## 转换为Map

使用`asMap`方法，可以将`Multimap`转换为`Map<K,Collection>`的形式，同样这个`Map`也可以看做一个关联的视图，在这个`Map`上的操作会作用于原始的`Multimap`。

```java
Map<String, Collection<Integer>> map = multimap.asMap();
for (String key : map.keySet()) {
    System.out.println(key+" : "+map.get(key));
}
map.get("day").add(20);
System.out.println(multimap);
复制代码
```

执行结果：

```properties
month : [3]
day : [1, 2, 8]
{month=[3], day=[1, 2, 8, 20]}
```

## 数量问题

`Multimap`中的数量在使用中也有些容易混淆的地方，先看下面的例子：

```java
System.out.println(multimap.size());
System.out.println(multimap.entries().size());
for (Map.Entry<String, Integer> entry : multimap.entries()) {
    System.out.println(entry.getKey()+","+entry.getValue());
}

```

打印结果：

```properties
4
4
month,3
day,1
day,2
day,8

```

这是因为`size()`方法返回的是所有`key`到单个`value`的映射，因此结果为4，`entries()`方法同理，返回的是`key`和单个`value`的键值对集合。但是它的`keySet`中保存的是不同的`key`的个数，例如下面这行代码打印的结果就会是2。

```java
System.out.println(multimap.keySet().size());

```

再看看将它转换为`Map`后，数量则会发生变化：

```java
Set<Map.Entry<String, Collection<Integer>>> entries = multimap.asMap().entrySet();
System.out.println(entries.size());

```

代码运行结果是2，因为它得到的是`key`到`Collection`的映射关系。



# 双键Map

# 双向Map

# RangeMap - 范围Map





# 参考资料

https://juejin.cn/post/7166504674214805517#heading-8 
