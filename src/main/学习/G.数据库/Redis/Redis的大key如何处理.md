# Table of Contents

* [如何找出前缀为XX的key](#如何找出前缀为xx的key)
* [什么是big key](#什么是big-key)
* [为什么要优化Big key](#为什么要优化big-key)
* [如何发现Big key](#如何发现big-key)
* [如何优化？](#如何优化)
* [[bigKey优化案例](bigKey优化案例.pdf)](#bigkey优化案例bigkey优化案例pdf)
* [为什么做了优化，内存占用还是很高？](#为什么做了优化内存占用还是很高)


# 如何找出前缀为XX的key

1. keys

keys命令的原理就是扫描整个redis里面所有的db的key数据，然后根据我们的通配的字符串进行模糊查找出来。**一次返回**

**这个命令会阻塞redis多路复用的io主线程**

2. scan

scan命令或者其他的scan如SSCAN ，HSCAN，ZSCAN命令，可以**不用阻塞主线程**，并支持游标按批次迭代返回数据,**多次返回。**

**返回的数据有可能重复**



# 什么是big key

大 key 并不是指 key 的值很大，而是 key 对应的 value 很大。

一般而言，下面这两种情况被称为大 key：

- String 类型的值大于 10 KB；
- Hash、List、Set、ZSet 元素的个数超过 5000个；



# 为什么要优化Big key



1. 内存开销大
2. 对于集群来说，部分大Key的Redis节点会导致QPS高

#  如何发现Big key



1. 使用**RDB快照文件**，进行分析，建议使用

2. 使用scan命令扫描。scan:增量遍历集合中的元素

   scan命令不阻塞主线程，但是大范围扫描keys还是会加重实例的IO，可以闲时再执行。



# 如何优化？



1. 优化改进

   + **更改key的存储方式**

     将每个Key取hash code，然后按照业务对指定的业务分成n片，用hash code对n取模。此时我们使用hset info:{hash code%n} {key} {value}存储，通过修改hash-max-ziplist-entries(用压缩列表保存时哈希集合中的最大元素个数)和hash-max-ziplist-value(用压缩列表保存时哈希集合中单个元素的最大长度)来控制hash使用ziplist存储，节省空间。 这样可以节省dictEntry开销，剩下32b的空间。

     > 其实就是使用压缩列表进行压缩。
     >
     > 举个例子：
     >
     > 原本存储：

     

   + **使用一定的序列化方式压缩存储空间，例如`protobuf`。**

     protobuf:https://zhuanlan.zhihu.com/p/401958878

     Protobuf：谷歌公司新开发的一种数据格式，适合高性能，对响应速度有要求的数据传输场景。因为Protobuf是二进制数据格式，需要编码和解码。数据本身不具有可读性，因此只能反序列化得到可读数据。

2. 直接删除

   对于可以直接删除的keys，建议使用scan命令+unlink命令删除，避免主线程阻塞。我们采用的删除方式是：scan轮流扫描各个实例，匹配需要删除的Keys。

   + 对于占用内存大的，不可直接使用del命令

     采用Redis4.0,unlink命令



#  [bigKey优化案例](bigKey优化案例.pdf)

# 为什么做了优化，内存占用还是很高？



Redis日常的使用是会存在内存碎片的，可在客户端执行info memory命令。如果**mem_fragmentation_ratio**值大于1.5，那么说明内存碎片超过50%，需要进行碎片整理了

**解决方案**：

- 重启Redis实例，暴力解决，不推荐
- 使用 **config set activedefrag yes** 命令，调整以下参数进行自动清理
