# Table of Contents

* [映射(map/flatMap)](#映射mapflatmap)
* [**归约(reduce)**](#归约reduce)
* [收集(collect)](#收集collect)
* [参考资料](#参考资料)








# 映射(map/flatMap)

映射，可以将一个流的元素按照一定的映射规则映射到另一个流中。分为`map`和`flatMap`：

- `map`：接收一个函数作为参数，该函数会被应用到每个元素上，并将其映射成一个新的元素。

  

- `flatMap`：接收一个函数作为参数，将流中的每个值都换成另一个流，然后把所有流连接成一个流。

  > 将流中的每个值都换成另一个流,会自动合并成一个流

```java
public class StreamTest {
 public static void main(String[] args) {
  List<String> list = Arrays.asList("m,k,l,a", "1,3,5,7");
  List<String> listNew = list.stream().flatMap(s -> {
   // 将每个元素转换成一个stream
   String[] split = s.split(",");
   Stream<String> s2 = Arrays.stream(split);
   return s2;
  }).collect(Collectors.toList());

  System.out.println("处理前的集合：" + list);
  System.out.println("处理后的集合：" + listNew);
 }
}
处理前的集合：[m-k-l-a, 1-3-5]
处理后的集合：[m, k, l, a, 1, 3, 5]
```



存在一个Map<Integer， ListContainer>，ListContainer中存在一个List<AClass>成员变量。有这样一个需求，讲Map中values中所有的List<AClass>组合成一个List<AClass>

```java
//原来写法
List<AClass> resultAClassList = Lists.newArrayList();
for (ListContainer tmp : map.values()){
    resultAClassList.addAll(tmp.getLst());
}

// flatMap


```





# **归约(reduce)**

归约，也称缩减，顾名思义，是把一个流缩减成一个值，能实现对集合求和、求乘积和求最值操作。





# 收集(collect)






# 参考资料

https://mp.weixin.qq.com/s/K40HSvLVGpLUrdEz0ZTzng
