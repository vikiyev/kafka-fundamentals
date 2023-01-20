package com.demiglace.kafka.orderproducer;

import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class OrderProducer {
	public static void main(String[] args) {
		// create properties
		Properties props = new Properties();
		props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerSerializer");
		props.setProperty(ProducerConfig.ACKS_CONFIG, "all");
		props.setProperty(ProducerConfig.BUFFER_MEMORY_CONFIG, "123123");
		props.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip");
		props.setProperty(ProducerConfig.RETRIES_CONFIG, "2");
		props.setProperty(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, "500");
		props.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, "1000000");
		props.setProperty(ProducerConfig.LINGER_MS_CONFIG, "500");
		props.setProperty(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, "200");
		
		// create the producer
		KafkaProducer<String,Integer> producer = new KafkaProducer<String, Integer>(props);
		ProducerRecord<String, Integer> record = new ProducerRecord<>("OrderTopic", "Macbook Pro", 10);
		
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
