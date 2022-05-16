# Table of Contents

* [一个List中的对象进行唯一值属性去重，属性求和](#一个list中的对象进行唯一值属性去重属性求和)
* [List多个字段进行排序](#list多个字段进行排序)
* [List对某个字段进行去重](#list对某个字段进行去重)






# 一个List中的对象进行唯一值属性去重，属性求和


```java
List<BillsNums> result = list.stream()
    //主要是对key进行分组 然后求和
        .collect(Collectors.toMap({BillsNums::getId}, a -> a, (o1,o2)-> {
			o1.setNums(o1.getNums() + o2.getNums());
			o1.setSums(o1.getSums() + o2.getSums());
			return o1;
		})).values().stream().collect(Collectors.toList());
```



# List多个字段进行排序

排序一般都是默认升序

```java
boardPoolList.stream()
  .sorted(//注意这里的sorted是在一个()内
    Comparator.comparing(BoardPool::getFlightFlow)
    .thenComparing(BoardPool::getStartAreaCode)
    .thenComparing(BoardPool::getFlightFlow,Comparator.reversed())
).collect(Collectors.toList())
 //对1升序
     //对2升序
    //对3降序
```



# List对某个字段进行去重

本质是先转map然后在values

```java
public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
    Map<Object, Boolean> map = new HashMap<>();
    return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
}
List<Book> distinctNameBooks3 = books.stream().filter(distinctByKey(o -> o.getName())).collect(Collectors.toList());
System.out.println(distinctNameBooks3);

```

