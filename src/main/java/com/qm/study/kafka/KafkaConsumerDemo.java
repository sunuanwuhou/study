package com.qm.study.kafka;

import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/9/6 21:45
 */
public class KafkaConsumerDemo {


    public static void main(String[] args) {
        Properties properties=  new Properties();
        KafkaConsumer kafkaConsumer = new KafkaConsumer<>(properties);
        kafkaConsumer.subscribe(Arrays.asList("topic1"));
        kafkaConsumer.subscribe(Arrays.asList("topic2"));

        kafkaConsumer.subscribe(Pattern.compile("test-"));
    }
}
