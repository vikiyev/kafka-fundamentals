package com.demiglace.kafka.orderproducer.customserializers;

import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.demiglace.kafka.orderproducer.customserializers.partitioners.VIPPartitioner;

public class OrderProducer {
	public static void main(String[] args) {
		// create properties
		Properties props = new Properties();
		props.setProperty("bootstrap.servers", "localhost:9092");
		props.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.setProperty("value.serializer", "com.demiglace.kafka.orderproducer.customserializers.OrderSerializer");
		props.setProperty("partitioner.class", VIPPartitioner.class.getName());
		
		// create the producer
		KafkaProducer<String,Order> producer = new KafkaProducer<String, Order>(props);
		Order order = new Order();
		order.setCustomerName("Doge");
		order.setProduct("Macbook");
		order.setQuantity(10);
		ProducerRecord<String, Order> record = new ProducerRecord<>("OrderPartitionedTopic", order.getCustomerName(), order);
		
		try {
			// send message
			producer.send(record); // synchronous
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			producer.close();
		}
	}
}
