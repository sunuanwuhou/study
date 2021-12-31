# Table of Contents

* [什么是`KafkaAdminClient`](#什么是kafkaadminclient)
* [基本使用](#基本使用)
* [创建主题](#创建主题)
  * [createTopics](#createtopics)
  * [主题合法性验证](#主题合法性验证)


# 什么是`KafkaAdminClient`



+ kafka-topics.sh脚本创建的**方式一般由运维人员操作，普通用户无权过问**。

+ TopicCommand创建了一个主题，当然我们也可以用它来实现主题的删除、修改、查看等操作，实质上与使用 kafka-config.sh 脚本的方式无异。这种方式与

  应用程序之间的交互性非常差，且不说它的编程模型类似于拼写字符串，它本身调用的TopicCommand类的main（）方法的返回值是一个void类，**并不能提**

  **供给调用者有效的反馈信息。**

那么`KafkaAdminClient`就为普通用户提供了一个“口子”，或者将其集成到公司内部的资源申请、审核系统中会更加方便。



# 基本使用

```java
public class KafkaAdminClient extends AdminClient {

   public CreateTopicsResult createTopics(Collection<NewTopic> newTopics) {
        return createTopics(newTopics, new CreateTopicsOptions());
    }
  public DeleteTopicsResult deleteTopics(Collection<String> topics) {
        return deleteTopics(topics, new DeleteTopicsOptions());
    }
public ListTopicsResult listTopics() {
        return listTopics(new ListTopicsOptions());
    }
	public DescribeTopicsResult describeTopics(Collection<String> topicNames) {
        return describeTopics(topicNames, new DescribeTopicsOptions());
    }
 public DescribeConfigsResult describeConfigs(Collection<ConfigResource> resources) {
        return describeConfigs(resources, new DescribeConfigsOptions());
    }
    //增加分区
  public CreatePartitionsResult createPartitions(Map<String, NewPartitions> newPartitions) {
        return createPartitions(newPartitions, new CreatePartitionsOptions());
    }
}
```

# 创建主题


## createTopics

```java
NewTopic topic = new NewTopic("Topic", 1, (short) 1);
AdminClient adminClient = AdminClient.create(new Properties());
//核心内容
CreateTopicsResult topics = adminClient.createTopics(Collections.singleton(topic));
```

KafkaAdminClient内部使用Kafka 的**一套自定义二进制协议**来实现诸如创建主题的管理功能。它主要的实现步骤如下：

+ 比如创建主题的createTopics方法，其内部就是发送CreateTopicRequest请求。
+ 客户端将请求发送至服务端。
+ 服务端处理相应的请求并返回响应，比如这个与CreateTopicRequest请求对应的就是CreateTopicResponse。
+ 客户端接收相应的响应并进行解析处理。和协议相关的请求和相应的类基本都在org.apache.kafka.common.requests包下，AbstractRequest和AbstractResponse是这些请求和响应类的两个基本父类。



```java
Map<String, KafkaFuture<Void>> values = topics.values();
for (Map.Entry<String, KafkaFuture<Void>> entry : values.entrySet()) {
    String key = entry.getKey();
    KafkaFuture<Void> value = entry.getValue();
}
```



其他方法不过多介绍



## 主题合法性验证



普通用户在创建主题的时候，有可能由于误操作或其他原因而创建**了不符合运维规范的主题，比如命名不规范，副本因子数太低**等，这些都会影响后期的系统运维。

Kafka broker 端有一个这样的参数：create.topic.policy.class.name，默认值为null，它提供了一个入口用来验证主题创建的合法性。

>  使用方式很简单，只需要自定义实现org.apache.kafka.server.policy.CreateTopicPolicy 接口



```java
public interface CreateTopicPolicy extends Configurable, AutoCloseable {
    void validate(CreateTopicPolicy.RequestMetadata var1) throws PolicyViolationException;
}
```

