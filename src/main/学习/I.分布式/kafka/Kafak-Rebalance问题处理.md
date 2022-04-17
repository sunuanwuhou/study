# Table of Contents

* [rebalance问题处理思路](#rebalance问题处理思路)
  * [参数配置](#参数配置)
  * [导致崩溃的点](#导致崩溃的点)
    * [消费者心跳超时](#消费者心跳超时)
    * [消费者处理时间过长](#消费者处理时间过长)
* [总结](#总结)
* [那如果参数都配置好了，线上突发崩溃，怎么处理？](#那如果参数都配置好了线上突发崩溃怎么处理)
* [参考资料](#参考资料)


# rebalance问题处理思路

前面我们讲过 rebalance 一般会有 3 种情况，分别是：

+ 新成员加入
+ 组成员主动离开
+ 组成员崩溃

对于「新成员加入」、「组成员主动离开」都是我们主动触发的，能比较好地控制。但是「组成员崩溃」则是我们预料不到的，遇到问题的时候也比较不好排查。

但对于「组成员崩溃」也是有一些通用的排查思路的，下面我们就来聊聊「rebalance问题的处理思路」。



##  参数配置

要学会处理 rebalance 问题，我们需要先搞清楚 kafaka 消费者配置的四个参数：

+ session.timeout.ms 设置了超时时间

  session.timeout.ms 表示 consumer 向 broker 发送心跳的超时时间。例如 session.timeout.ms = 180000 表示在最长 180 秒内 broker 没收到 consumer 的心跳，那么 broker 就认为该 consumer 死亡了，会启动 rebalance。

+ heartbeat.interval.ms 心跳时间间隔

  heartbeat.interval.ms 表示 consumer 每次向 broker 发送心跳的时间间隔。heartbeat.interval.ms = 60000 表示 consumer 每 60 秒向 broker 发送一次心跳。一般来说，session.timeout.ms 的值是 heartbeat.interval.ms 值的 3 倍以上。

+ max.poll.interval.ms 每次消费的处理时间

  max.poll.interval.ms 表示 consumer 每两次 poll 消息的时间间隔。简单地说，其实就是 consumer 每次消费消息的时长。如果消息处理的逻辑很重，那么市场就要相应延长。否则如果时间到了 consumer 还么消费完，broker 会默认认为 consumer 死了，发起 rebalance。

+ max.poll.records 每次消费的消息数

  max.poll.records 表示每次消费的时候，获取多少条消息。获取的消息条数越多，需要处理的时间越长。所以每次拉取的消息数不能太多，需要保证在 max.poll.interval.ms 设置的时间内能消费完，否则会发生 rebalance。


## 导致崩溃的点

简单来说，会导致崩溃的几个点是：

+ 消费者心跳超时，导致 rebalance。
+ 消费者处理时间过长，导致 rebalance。


### 消费者心跳超时

我们知道消费者是通过心跳和协调者保持通讯的，如果协调者收不到心跳，那么协调者会认为这个消费者死亡了，从而发起 rebalance。

而 kafka 的消费者参数设置中，跟心跳相关的两个参数为：

session.timeout.ms 设置了超时时间
heartbeat.interval.ms 心跳时间间隔
这时候需要调整 session.timeout.ms 和 heartbeat.interval.ms 参数，使得消费者与协调者能保持心跳。一般来说，超时时间应该是心跳间隔的 3 倍时间。即 session.timeout.ms 如果设置为 180 秒，那么 heartbeat.interval.ms 最多设置为 60 秒。

为什么要这么设置超时时间应该是心跳间隔的 3 倍时间？因为这样的话，在一个超时周期内就可以有多次心跳，避免网络问题导致偶发失败。

### 消费者处理时间过长

如果消费者处理时间过长，那么同样会导致协调者认为该 consumer 死亡了，从而发起重平衡。

而 kafka 的消费者参数设置中，跟消费处理的两个参数为：

max.poll.interval.ms 每次消费的处理时间
max.poll.records 每次消费的消息数
对于这种情况，一般来说就是增加消费者处理的时间（即提高 max.poll.interval.ms 的值），减少每次处理的消息数（即减少 max.poll.records 的值）。

除此之外，超时时间参数（session.timeout.ms）与 消费者每次处理的时间（max.poll.interval.ms）也是有关联的。max.poll.interval.ms 时间不能超过 session.timeout.ms 时间。 因为在 kafka 消费者的实现中，其是单线程去消费消息和执行心跳的，如果线程卡在处理消息，那么这时候即使到时间要心跳了，还是没有线程可以去执行心跳操作。很多同学在处理问题的时候，明明设置了很长的 session.timeout.ms 时间，但最终还是心跳超时了，就是因为没有处理好这两个参数的关联。



# 总结



对于 rebalance 类问题，简单总结就是：

+ 对于新成员的加入和离开需要事情规划好。
+ 对于成员崩溃的问题：**处理好心跳超时问题和消费处理超时问题。**
  + 对于心跳超时问题。**一般是调高心跳超时时间**（session.timeout.ms），调整超时时间（session.timeout.ms）和心跳间隔时间（heartbeat.interval.ms）的比例。阿里云官方文档建议超时时间（session.timeout.ms）设置成 25s，最长不超过 30s。那么心跳间隔时间（heartbeat.interval.ms）就不超过 10s。
  + 对于消费处理超时问题。**一般是增加消费者处理的时间**（max.poll.interval.ms），减少每次处理的消息数（max.poll.records）。阿里云官方文档建议 max.poll.records 参数要远小于当前消费组的消费能力（records < 单个线程每秒消费的条数 x 消费线程的个数 x session.timeout的秒数）。





# 那如果参数都配置好了，线上突发崩溃，怎么处理？





# 参考资料

https://cloud.tencent.com/developer/article/1631633
