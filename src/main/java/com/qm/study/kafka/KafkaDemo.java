package com.qm.study.kafka;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/9/4 19:18
 */
public class KafkaDemo {


    public static void main(String[] args) {

        Properties properties=  new Properties();
        try (KafkaProducer kafkaProducer = new KafkaProducer<>(properties)) {
            ProducerRecord ProducerRecord1 = new ProducerRecord<>("topic","这是一条消息");
            ProducerRecord ProducerRecord2 = new ProducerRecord<>("topic","这是一条消息");

            Future<RecordMetadata> send = kafkaProducer.send(ProducerRecord1);
            RecordMetadata recordMetadata = send.get();

            kafkaProducer.send(ProducerRecord1, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {

                }
            });
            kafkaProducer.send(ProducerRecord2, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {

                }
            });

            kafkaProducer.close();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
