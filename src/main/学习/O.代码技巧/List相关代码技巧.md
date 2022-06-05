# Table of Contents

* [一个List中的对象进行唯一值属性去重，属性求和](#一个list中的对象进行唯一值属性去重属性求和)
* [List多个字段进行排序](#list多个字段进行排序)
* [List对某个字段进行去重](#list对某个字段进行去重)
* [List之间进行转换](#list之间进行转换)






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



# List之间进行转换

+ 参考资料:https://mp.weixin.qq.com/s/GIljLxPSyTsgHCcbXWclVw
+ 但是有两点要提醒：
  - 此方法依旧不能解决深层次的深拷贝问题，详细的可以 google 一下 BeanUtils 的深拷贝问题。
  - 如果 source 或者 targetSupplier 只要有一个为 null，本工具类不像 BeanUtils 一样抛出异常，而是返回 null，因为笔者认为调用方如果把 null 进行准换，那就是想转换为 null，为不为空应该由调用方自己负责。

+ 使用方法

  ```java
  User user = new User();
  user.setName("11");
  user.setAge(11);
  List<User> userList = Lists.newArrayList();
  userList.add(user);
  List<UserVo> userVos = BeanConvertUtils.convertListTo(
      userList,
      UserVo::new, 
      //回调方法 处理转换的过程
      (s, t) -> {
      t.setAge(s.getAge() + 1);
      t.setName(s.getName() + "1");
  });
  ```



