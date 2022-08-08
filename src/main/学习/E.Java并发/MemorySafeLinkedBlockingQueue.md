# Table of Contents

* [参考资料](#参考资料)
* [总结](#总结)




# 参考资料

https://juejin.cn/post/7105968458851942414#heading-1





+ https://github.com/apache/dubbo/pull/9722/files
+ https://github.com/apache/dubbo/pull/10021/files



# 总结

1. 因为线程池的阻塞队列，容易OOM,所以大佬就自己实现了内存安全的队列。
2. Instrumentation 它可以更加方便的做字节码增强操作，允许我们对已经加载甚至还没有被加载的类进行修改的操作，实现类似于性能监控的功能。很多工具都是基于这个玩意来实现的，比如大名鼎鼎的 Arthas。
3. 分2个
   1. MemoryLimitedLBQ
      + 传入数据后，当前队列+当前数据>设置的值。
      + 在 release 方法里面，肯定也是计算当前对象的 size，然后再从 memory 里面减出去：
   2. MemorySafeLBQ
      + 使用的是 ManagementFactory 里面的 MemoryMXBean，来获取当前内存情况。

4. 我们可以借鉴什么呢？思路扩展一下，比如我们有的项目里面用 Map 来做本地缓存，就会放很多元素进去，也会有 OOM 的风险，那么通过前面说的思路，是不是就找到了一个问题的解决方案？
