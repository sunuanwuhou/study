# Table of Contents

* [什么是Redis](#什么是redis)
* [官方资料](#官方资料)
* [为什么要使用Redis](#为什么要使用redis)
  * [读写性能优异](#读写性能优异)
  * [数据类型丰富](#数据类型丰富)
  * [原子性](#原子性)
  * [丰富特性](#丰富特性)
  * [持久化](#持久化)
  * [发布订阅](#发布订阅)
  * [分布式](#分布式)
* [Redis使用场景](#redis使用场景)
  * [热点数据的缓存](#热点数据的缓存)
  * [限时数据的应用](#限时数据的应用)
  * [分布式锁](#分布式锁)
  * [计数器](#计数器)
  * [排行榜相关问题](#排行榜相关问题)
  * [简单队列](#简单队列)



# 什么是Redis

Redis是一款内存高速缓存数据库。Redis全称为：Remote Dictionary Server（远程数据服务），使用C语言编写，Redis是一个key-value存储系统（键值存储系统），支持丰富的数据类型，如：String、list、set、zset、hash。



Redis是一种支持key-value等多种数据结构的存储系统。可用于<font color=red>缓存，事件发布或订阅，高速队列等场景。</font>支持网络，提供字符串，哈希，列表，队列，集合结构直接存取，基于内存，可持久化。

# 官方资料 



+ Redis官网:http://redis.io/ 
+ Redis官方文档:http://redis.io/documentation 
+ Redis教程:http://www.w3cschool.cn/redis/redis-intro.html
+ Redis下载:http://redis.io/download

# 为什么要使用Redis



## 读写性能优异



+ Redis能读的速度是110000次/s,写的速度是81000次/s 

## 数据类型丰富

+ String、list、set、zset、hash。

## 原子性 

+ Redis所有操作都是<font color=red>原子性</font>的，同时Redis还支持对<font color=red>几个操作合并后的原子操作</font>。


## 丰富特性

- Redis支持 publish/subscribe, 通知, key 过期等特性。

## 持久化

- Redis支持<font color=red>RDB, AOF</font>等持久化方式

## 发布订阅

- Redis支持发布/订阅模式

## 分布式

- Redis Cluster


# Redis使用场景 



## 热点数据的缓存

缓存是Redis最常见的应用场景，之所有这么使用，主要是因为Redis读写性能优异。而且逐渐有取代memcached，成为首选服务端缓存的组件。而且，Redis内部是支持事务的，在使用时候能有效保证数据的一致性。

作为缓存使用时，一般有两种方式保存数据：

- 读取前，先去读Redis，如果没有数据，读取数据库，将数据拉入Redis。
- 插入数据时，同时写入Redis。

方案一：实施起来简单，但是有两个需要注意的地方：

- 避免缓存击穿。（数据库没有就需要命中的数据，导致Redis一直没有数据，而一直命中数据库。）
- 数据的实时性相对会差一点。

方案二：数据实时性强，但是开发时不便于统一处理。

当然，两种方式根据实际情况来适用。如：方案一适用于对于数据实时性要求不是特别高的场景。方案二适用于字典表、数据量不大的数据存储。



## 限时数据的应用


redis中可以使用expire命令设置一个键的生存时间，到时间后redis会删除它。利用这一特性可以运用在限时的优惠活动信息、手机验证码等业务场景。

## 分布式锁

这个主要利用redis的setnx命令进行，setnx："set if not exists"就是如果不存在则成功设置缓存同时返回1，否则返回0

## 计数器 

redis由于incrby命令可以实现原子性的递增，所以可以运用于高并发的秒杀活动、分布式序列号的生成、具体业务还体现在比如限制一个手机号发多少条短信、一个接口一分钟限制多少请求、一个接口一天限制调用多少次等等。



## 排行榜相关问题

系型数据库在排行榜方面查询速度普遍偏慢，所以可以借助redis的SortedSet进行热点数据的排序。

比如点赞排行榜，做一个SortedSet, 然后以用户的openid作为上面的username, 以用户的点赞数作为上面的score, 然后针对每个用户做一个hash, 通过zrangebyscore就可以按照点赞数获取排行榜，然后再根据username获取用户的hash信息，这个当时在实际运用中性能体验也蛮不错的。

## 简单队列

由于Redis有list push和list pop这样的命令，所以能够很方便的执行队列操作

