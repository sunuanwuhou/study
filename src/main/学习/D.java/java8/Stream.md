# Table of Contents

  * [**# 操作符**](#-操作符)
* [中间操作符](#中间操作符)
  * [映射(map/flatMap)](#映射mapflatmap)
  * [**筛选（filter）**](#筛选filter)
* [终止操作符](#终止操作符)
  * [**归约(reduce)**](#归约reduce)
  * [收集(collect)](#收集collect)
  * [**聚合（max/min/count)**](#聚合maxmincount)
* [参考资料](#参考资料)



相信Java8的Stream 大家都已听说过了，但是可能大家不会用或者用的不熟，文章将带大家从零开始使用，循序渐进，带你走向Stream的巅峰。



## **# 操作符**



什么是操作符呢？操作符就是对数据进行的一种处理工作，一道加工程序；就好像工厂的工人对流水线上的产品进行一道加工程序一样。

Stream的操作符大体上分为两种：中间操作符和终止操作符



# 中间操作符



对于数据流来说，中间操作符在执行制定处理程序后，数据流依然可以传递给下一级的操作符。



中间操作符包含8种(排除了parallel,sequential,这两个操作并不涉及到对数据流的加工操作)：



- map(mapToInt,mapToLong,mapToDouble) 转换操作符，把比如A->B，这里默认提供了转int，long，double的操作符。

- flatmap(flatmapToInt,flatmapToLong,flatmapToDouble) 拍平操作比如把 int[]{2,3,4} 拍平 变成 2，3，4 也就是从原来的一个数据变成了3个数据，这里默认提供了拍平成int,long,double的操作符。

- limit 限流操作，比如数据流中有10个 我只要出前3个就可以使用。

- distint 去重操作，对重复元素去重，底层使用了equals方法。

- filter 过滤操作，把不想要的数据过滤。

- peek 挑出操作，如果想对数据进行某些操作，如：读取、编辑修改等。

- skip 跳过操作，跳过某些元素。

- sorted(unordered) 排序操作，对元素排序，前提是实现Comparable接口，当然也可以自定义比较器

  


## 映射(map/flatMap)

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



## **筛选（filter）**

筛选，是按照一定的规则校验流中的元素，将符合条件的元素提取到新的流中的操作。

```java
public class StreamTest {
 public static void main(String[] args) {
  List<Integer> list = Arrays.asList(6, 7, 3, 8, 1, 2, 9);
  Stream<Integer> stream = list.stream();
  stream.filter(x -> x > 7).forEach(System.out::println);
 }
}
```





#  终止操作符

数据经过中间加工操作，就轮到终止操作符上场了；终止操作符就是用来对数据进行收集或者消费的，数据到了终止操作这里就不会向下流动了，终止操作符只能使用一次。



- collect 收集操作，将所有数据收集起来，这个操作非常重要，官方的提供的Collectors 提供了非常多收集器，可以说Stream 的核心在于Collectors。
- count 统计操作，统计最终的数据个数。
- findFirst、findAny 查找操作，查找第一个、查找任何一个 返回的类型为Optional。
- noneMatch、allMatch、anyMatch 匹配操作，数据流中是否存在符合条件的元素 返回值为bool 值。
- min、max 最值操作，需要自定义比较器，返回数据流中最大最小的值。
- reduce 规约操作，将整个数据流的值规约为一个值，count、min、max底层就是使用reduce。
- forEach、forEachOrdered 遍历操作，这里就是对最终的数据进行消费了。
- toArray 数组操作，将数据流的元素转换成数组。



## **归约(reduce)**

归约，也称缩减，顾名思义，是把一个流缩减成一个值，能实现对集合求和、求乘积和求最值操作。





## 收集(collect)





## **聚合（max/min/count)**

```java
public class StreamTest {
 public static void main(String[] args) {
  List<String> list = Arrays.asList("adnm", "admmt", "pot", "xbangd", "weoujgsd");

  Optional<String> max = list.stream().max(Comparator.comparing(String::length));
  System.out.println("最长的字符串：" + max.get());
 }
}
```






# 参考资料

https://mp.weixin.qq.com/s/K40HSvLVGpLUrdEz0ZTzng

