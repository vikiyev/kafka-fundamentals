package com.demiglace.kafka.avro.deserializers;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.demiglace.kafka.avro.Order;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;

public class OrderConsumer {
	public static void main(String[] args) {
		Properties props = new Properties();
		props.setProperty("bootstrap.servers", "localhost:9092");
		props.setProperty("key.deserializer", KafkaAvroDeserializer.class.getName());
		props.setProperty("value.deserializer", KafkaAvroDeserializer.class.getName());
		props.setProperty("group.id", "OrderGroup");
		props.setProperty("schema.registry.url", "http://localhost:8081");
		// props.setProperty("specific.avro.reader", "true");
		
		// create the consumer
		KafkaConsumer<String, GenericRecord> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Collections.singletonList("OrderAvroGRTopic"));
		
		// poll the topic
		ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofSeconds(20));
		
		for (ConsumerRecord<String, GenericRecord> record : records) {
			String customerName = record.key();
			GenericRecord order = record.value();
			System.out.println("Product: " +  order.get("product"));
		}
		
		consumer.close();
	}
}
