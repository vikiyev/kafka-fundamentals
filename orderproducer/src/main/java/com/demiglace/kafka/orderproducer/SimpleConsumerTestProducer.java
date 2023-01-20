package com.demiglace.kafka.orderproducer;

import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class SimpleConsumerTestProducer {
	public static void main(String[] args) {
		// create properties
		Properties props = new Properties();
		props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerSerializer");
		
		// create the producer
		KafkaProducer<String,Integer> producer = new KafkaProducer<String, Integer>(props);
		ProducerRecord<String, Integer> record = new ProducerRecord<>("SimpleConsumerTopic", "Macbook Pro", 10);
		
		try {
			// send message
//			Future<RecordMetadata> future = producer.send(record); // synchronous
//			RecordMetadata recordMetadata = future.get();
//			System.out.println("Message sent!");
//			System.out.println(recordMetadata.partition());
//			System.out.println(recordMetadata.offset());
			producer.send(record, new OrderCallback()); // async send
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			producer.close();
		}
	}
}
