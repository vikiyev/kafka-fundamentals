package com.demiglace.kafka.orderconsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.RoundRobinAssignor;

public class OrderConsumer {

	public static void main(String[] args) {
		Properties props = new Properties();
		props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerDeserializer");
		props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "OrderTopic");
		props.setProperty(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, "1024");
		props.setProperty(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, "200");
		props.setProperty(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "200");
		props.setProperty(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "2000");
		props.setProperty(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, "1MB");
		props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, "OrderConsumer");
		props.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100");
		props.setProperty(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, RoundRobinAssignor.class.getName());
		
		// create the consumer
		KafkaConsumer<String, Integer> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Collections.singletonList("OrderTopic"));
		
		// poll the topic
		ConsumerRecords<String, Integer> orders= consumer.poll(Duration.ofSeconds(20));
		
		for (ConsumerRecord<String, Integer> order : orders) {
			System.out.println("Product Name: " + order.key());
			System.out.println("Product Quantity: " + order.value());
		}
		
		consumer.close();
	}
}
