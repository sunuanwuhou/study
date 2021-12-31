# Table of Contents

* [为什么使用Guava Cache](#为什么使用guava-cache)
* [使用Guava构建缓存](#使用guava构建缓存)
* [参考资料](#参考资料)




# 为什么使用Guava Cache

最近需要用到缓存来存放临时数据，又不想采用Redis，Java自带的Map功能太少，发现Google的Guava提供的Cache模块功能很强大，于是选择使用它。


在guava中默认使用LRU淘汰算法，而且在不修改源码的情况下也不支持自定义淘汰算法

+ 并发安全
+ 内存淘汰策略



# 使用Guava构建缓存

```java
// 通过CacheBuilder构建一个缓存实例
Cache<String, String> cache = CacheBuilder.newBuilder()
                .maximumSize(100) // 设置缓存的最大容量
                .expireAfterWrite(1, TimeUnit.MINUTES) // 设置缓存在写入一分钟后失效
                .concurrencyLevel(10) // 设置并发级别为10
                .recordStats() // 开启缓存统计
                .build();
// 放入缓存
cache.put("key", "value");
// 获取缓存
String value = cache.getIfPresent("key");
```





# 参考资料

+ http://ifeve.com/google-guava/

