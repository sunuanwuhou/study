# Table of Contents

* [生产者客户端整体架构](#生产者客户端整体架构)
* [RecordAccumulator](#recordaccumulator)
  * [Buffer pool](#buffer-pool)
  * [ProducerBatch和Batch.size](#producerbatch和batchsize)
* [消息转换](#消息转换)
* [InFlightRequests(重点)](#inflightrequests重点)
  * [leastLoadedNode](#leastloadednode)
* [重要的生产者参数](#重要的生产者参数)
  * [ack(重点)](#ack重点)
  * [max.request.size](#maxrequestsize)
  * [retries和retry.backoff.ms](#retries和retrybackoffms)
  * [compression.type](#compressiontype)
  * [linger.ms](#lingerms)
* [总结](#总结)




# 生产者客户端整体架构

![](.images/epub_25462424_80.jpg)

整个生产者客户端由两个线程协调运行，这两个线程分别为**主线程**和**Sender线程（发送线程）**。

在主线程中由`KafkaProducer`创建消息，然后通过可能的拦截器、序列化器和分区器的作用之后缓存到消息累加器

（RecordAccumulator，也称为消息收集器）中。

`Sender` 线程负责从`RecordAccumulator`中获取消息并将其发送到Kafka中。

> 消息来一条发一条，太浪费性能了。对网络负载比较大



# RecordAccumulator

`RecordAccumulator` 主要用来缓存消息以便 `Sender` 线程可以批量发送，进而减少网络传输的资源消耗以提升性能。

`RecordAccumulator` 缓存的大小可以通过生产者客户端参数buffer.memory 配置，默认值为 33554432B，即 32MB。

如果**生产者发送消息的速度超过发送到服务器的速度，则会导致生产者空间不**

这个时候KafkaProducer的send（）方法调用要么被阻塞，要么抛出异常，这个取决于参数max.block.ms的配置，此参数的默认值为60000，即60秒。



主线程中发送过来的消息都会被追加到RecordAccumulator的某个双端队列（`Deque`）中.

`RecordAccumulator` 的内部为每个分区都维护了一个双端队列，队列中的内容就是`ProducerBatch`，即 Deque＜`ProducerBatch`＞。

+ 消息写入缓存时，追加到双端队列的尾部；

+ Sender读取消息时，从双端队列的头部读取。

注意ProducerBatch不是ProducerRecord，ProducerBatch中可以包含一至多个 ProducerRecord。

> 这里为什么要用deque ,队列不也可以吗？



## Buffer pool

消息在网络上都是以字节（Byte）的形式传输的，在发送之前需要创建一块内存区域来保存对应的消息。

在Kafka生产者客户端中，通过java.io.ByteBuffer实现消息内存的创建和释放。


RecordAccumulator的内部还有一个BufferPool，它主要用来实现ByteBuffer的复用，以实现缓存的高效利用。

不过BufferPool只针对特定大小的ByteBuffer进行管理，而其他大小的ByteBuffer不会缓存进BufferPool中，

**这个特定的大小由batch.size参数来指定**，默认值为16384B，即16KB。我们可以适当地调大batch.size参数

以便多缓存一些消息。



## ProducerBatch和Batch.size

ProducerBatch的大小和batch.size参数也有着密切的关系。

当一条消息（ProducerRecord）流入RecordAccumulator时，会先寻找与消息分区所对应的双端队列（如果没有则新建），再从这个双端队列的尾部获取一个ProducerBatch（如果没有则新建），查看 ProducerBatch 中是否还可以写入这个 ProducerRecord，如果可以则写入，如果不可以则需要创建一个新的ProducerBatch。

在新建ProducerBatch时评估这条消息的大小是否超过batch.size参数的大小，

+ **如果不超过**，那么就以 batch.size 参数的大小来创建ProducerBatch，这样在使用完这段内存区域之后，可以通过BufferPool 的管理来进行复用；
+ **如果超过**，那么就以评估的大小来创建ProducerBatch，这段内存区域不会被复用。





# 消息转换



+ 第一次转换

Sender 从 RecordAccumulator 中获取缓存的消息之后，会进一步将原本＜分区，Deque＜ProducerBatch＞＞

的保存形式转变成＜Node，List＜ProducerBatch＞的形式，其中Node表示Kafka集群的broker节点。

**对于网络连接来说，生产者客户端是与具体的broker节点建立的连接，也就是向具体的broker 节点发送消息，而**

**并不关心消息属于哪一个分区；**

而对于 KafkaProducer的应用逻辑而言，我们只关注向哪个分区中发送哪些消息，所以在这里需要做一个应用逻

辑层面到网络I/O层面的转换。



> ＜分区，Deque＜ProducerBatch＞＞ ----------------->＜Node，List＜ProducerBatch＞
>
> Node(broker)怎么确定呢？还是随机选一个？



+ 第二次转换

在转换成＜Node，List＜ProducerBatch＞＞的形式之后，Sender 还会进一步封装成＜Node，Request＞的形式，这样就可以将Request请求发往各个Node了，这里的Request是指Kafka的各种协议请求，对于消息发送而言就是指具体的ProduceRequest。

> ＜Node，List＜ProducerBatch＞ ----------------->＜Node，Request＞




# InFlightRequests(重点)

请求在从Sender线程发往Kafka之前还会保存到InFlightRequests中，InFlightRequests保存对象的具体形式为 Map＜NodeId，Deque＜Request＞＞，

它的主要作用是**缓存了已经发出去但还没有收到响应的请求**（NodeId 是一个String 类型，表示节点的 id 编号）。

> 注意:这里的`Requet`是之前`ProduceBatch`不是`ProduceRecored`

与此同时，InFlightRequests还提供了许多管理类的方法，并且通过配置参数还可以限制每个连接（也就是客户端与Node之间的连接）最多缓存的请求数。

这个配置参数为max.in.flight.requests.per.connection，**默认值为 5**，即每个连接最多只能缓存5 个未响应的请求，超过该数值之后就不能再向这个连接发送更多的请求了，除非有缓存的请求收到了响应（Response）。

通过比较Deque＜Request＞的size与这个参数的大小来判断对应的Node中是否已经堆积了很多未响应的消息，

如果真是如此，那么说明这个 Node 节点负载较大或网络连接有问题，再继续向其发送请求会增大请求超时的可

能。

> 这个保证了分区消息的顺序行。
> 一般而言，在需要保证消息顺序的场合建议把参数max.in.flight.requests.per.connection配置为1



## leastLoadedNode

InFlightRequests还可以获得leastLoadedNode，即所有Node中负载最小的那一个。

这里的负载最小是通过每个Node在InFlightRequests中还未确认的请求决定的，**未确认的请求越多则认为负载**

**越大。**

对于图中的InFlightRequests 来说，图中展示了三个节点Node0、Node1和Node2，很明显Node1的负载最小。

也就是说，Node1为当前的leastLoadedNode。

选择leastLoadedNode发送请求可以使它能够尽快发出，避免因网络拥塞等异常而影响整体的进度。

leastLoadedNode的概念可以用于多个应用场合，比如元数据请求、消费者组播协议的交互。



# 重要的生产者参数

## ack(重点)

这个参数用来指定分区中**必须要有多少个副本收到这条消息，之后生产者才会认为这条消息是成功写入的**。

acks 是生产者客户端中一个非常重要的参数，它涉及**消息的可靠性和吞吐量之间的权衡**。acks参数有3种类型的值

（**都是字符串类型**）。

> 很多优秀的框架会提供多个应用参数，供开发者选择。



+ **acks=1。默认值即为1。生产者发送消息之后，只要分区的leader副本成功写入消息，那么它就会收到来自服务端的成功响应**。

  如果消息无法写入leader副本，比如在leader 副本崩溃、重新选举新的 leader 副本的过程中，那么生产者就会收到一个错误的响应，为了避免消息丢失，生产者可以选择重发消息。如果消息写入leader副本并返回成功响应给生产者，且在被其他follower副本拉取之前leader副本崩溃，那么此时消息还是会丢失，因为新选举的leader副本中并没有这条对应的消息。**acks设置为1，是消息可靠性和吞吐量之间的折中方案。**

+ **acks=0。生产者发送消息之后不需要等待任何服务端的响应。**

  如果在消息从发送到写入Kafka的过程中出现某些异常，导致Kafka并没有收到这条消息，那么生产者也无从得知，消息也就丢失了。在其他配置环境相同的情况下，**acks 设置为 0 可以达到最大的吞吐量。**

+ ·**acks=-1或acks=all。生产者在消息发送之后，需要等待ISR中的所有副本都成功写入消息之后才能够收到来自服务端的成功响应。**

  在其他配置环境相同的情况下，acks 设置为-1（all）可以达到最强的可靠性。但这并不意味着消息就一定可靠，因为**ISR中可能只有leader副本**，这样就退化成了acks=1的情况。要获得更高的消息可靠性需要配合 min.insync.replicas 等参数的联动

  > 待补充



## max.request.size

这个参数用来限制生产者客户端能发送的消息的最大值，默认值为 1048576B，即1MB

这个参数还涉及一些其他参数的联动，慎改！



## retries和retry.backoff.ms

+ retries：参数用来配置生产者重试的次数，**默认值为0，即在发生异常的时候不进行任何重试动作**

+ retry.backoff.ms：它用来设定两次重试之间的时间间隔，避免无效的频繁重试。



## compression.type

这个参数用来指定消息的压缩方式，默认值为“none”，即默认情况下，消息不会被压缩。

该参数还可以配置为“gzip”“snappy”和“lz4”。对消息进行压缩可以极大地减少网络传输量、降低网络I/O，从而提

高整体的性能。消息压缩是一种使用时间换空间的优化方式，如果对时延有一定的要求，则不推荐对消息进行压

缩。



## linger.ms

这个参数用来指定生产者发送 ProducerBatch 之前等待更多消息（ProducerRecord）加入ProducerBatch 的时间，默认值为 0。

生产者客户端会在 ProducerBatch 被填满或等待时间超过linger.ms 值时发送出去。增大这个参数的值会增加消息的延迟，但是同时能提升一定的吞吐量。

这个linger.ms参数与TCP协议中的Nagle算法有异曲同工之妙。





# 总结

1. 看图理解就好了
