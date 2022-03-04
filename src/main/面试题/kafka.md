# Table of Contents

* [什么是Rebalance](#什么是rebalance)
  * [什么时候发生Rebalance](#什么时候发生rebalance)
  * [造成的影响](#造成的影响)
  * [如何解决](#如何解决)
* [kafka什么时候会重复消费](#kafka什么时候会重复消费)



[AREADME](../学习/I.分布式/kafka/AREADME.md)



# 什么是Rebalance



**rebalance（重平衡）其实就是重新进行 partition 的分配，从而使得 partition 的分配重新达到平衡状态。**


## 什么时候发生Rebalance

- 订阅 Topic 的分区数发生变化。
- 订阅的 Topic 个数发生变化。
- 消费组内成员个数发生变化。例如有新的 consumer 实例加入该消费组或者离开组。
  

## 造成的影响

1. 数据重复消费: 消费过的数据由于提交offset任务也会失败，在partition被分配给其他消费者的时候，会造成重复消费，数据重复且增加集群压力
2. Rebalance扩散到整个ConsumerGroup的所有消费者，因为一个消费者的退出，导致整个Group进行了Rebalance，并在一个比较慢的时间内达到稳定状态，影响面较大
3. 频繁的Rebalance反而降低了消息的消费速度，大部分时间都在重复消费和Rebalance
4. 数据不能及时消费，会累积lag，在Kafka的TTL之后会丢弃数据



## 如何解决

简单来说，会导致崩溃的几个点是：

- 消费者心跳超时，导致 rebalance。
- 消费者处理时间过长，导致 rebalance。



对于 rebalance 类问题，简单总结就是：**处理好心跳超时问题和消费处理超时问题**。

- 对于心跳超时问题。一般是调高心跳超时时间（session.timeout.ms），调整超时时间（session.timeout.ms）和心跳间隔时间（heartbeat.interval.ms）的比例。阿里云官方文档建议超时时间（session.timeout.ms）设置成 25s，最长不超过 30s。那么心跳间隔时间（heartbeat.interval.ms）就不超过 10s。
- 对于消费处理超时问题。一般是增加消费者处理的时间（max.poll.interval.ms），减少每次处理的消息数（max.poll.records）。阿里云官方文档建议 max.poll.records 参数要远小于当前消费组的消费能力（records < 单个线程每秒消费的条数 x 消费线程的个数 x session.timeout的秒数）。




# kafka什么时候会重复消费

导致kafka的重复消费问题原因在于，已经消费了数据，但是offset没来得及提交（比如Kafka没有或者不知道该数据已经被消费）



+ 设置为自动提交，kafka没有提交，被强制关闭
+ 发生Rebalance
