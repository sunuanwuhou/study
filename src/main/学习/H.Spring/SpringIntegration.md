# Table of Contents

* [概述](#概述)
* [参考资料](#参考资料)




# 概述

提到分布式锁大家都会想到如下两种：

- 基于`Redisson`组件，使用redlock算法实现
- 基于`Apache Curator`，利用Zookeeper的临时顺序节点模型实现

今天我们来说说第三种，使用 `Spring Integration` 实现。

`Spring Integration`在基于Spring的应用程序中实现轻量级消息传递，并支持通过声明适配器与外部系统集成。Spring Integration的主要目标是提供一个简单的模型来构建企业集成解决方案，同时保持关注点的分离，这对于生成可维护，可测试的代码至关重要。我们熟知的 Spring Cloud Stream的底层就是Spring Integration。

官方地址：https://github.com/spring-projects/spring-integration

Spring Integration提供的全局锁目前为如下存储提供了实现：

- Gemfire
- JDBC
- Redis
- Zookeeper

它们使用相同的API抽象，这意味着，不论使用哪种存储，你的编码体验是一样的。试想一下你目前是基于zookeeper实现的分布式锁，哪天你想换成redis的实现，我们只需要修改相关依赖和配置就可以了，无需修改代码。下面是你使用 `Spring Integration` 实现分布式锁时需要关注的方法：

| 方法名                              | 描述                                                         |
| :---------------------------------- | :----------------------------------------------------------- |
| `lock()`                            | `Acquires the lock.` 加锁，如果已经被其他线程锁住或者当前线程不能获取锁则阻塞 |
| `lockInterruptibly()`               | `Acquires the lock unless the current thread is interrupted.` 加锁，除非当前线程被打断。 |
| `tryLock()`                         | `Acquires the lock only if it is free at the time of invocation.` 尝试加锁，如果已经有其他锁锁住，获取当前线程不能加锁，则返回false，加锁失败；加锁成功则返回true |
| `tryLock(long time, TimeUnit unit)` | `Acquires the lock if it is free within the given waiting time and the current thread has not been interrupted.` 尝试在指定时间内加锁，如果已经有其他锁锁住，获取当前线程不能加锁，则返回false，加锁失败；加锁成功则返回true |
| `unlock()`                          | `Releases the lock.` 解锁                                    |







# 参考资料



https://mp.weixin.qq.com/s/-EUIvSElDo_FUmPl64OjKg
