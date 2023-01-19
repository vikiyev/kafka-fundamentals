package com.demiglace.kafka.orderproducer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;

public class OrderCallback implements Callback {
	@Override
	public void onCompletion(RecordMetadata recordMetadata, Exception exception) {
		System.out.println("Message sent!");
		System.out.println(recordMetadata.partition());
		System.out.println(recordMetadata.offset());
	}
}
