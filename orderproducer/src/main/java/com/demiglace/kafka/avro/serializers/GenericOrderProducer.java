package com.demiglace.kafka.avro.serializers;

import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Parser;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.demiglace.kafka.avro.Order;

import io.confluent.kafka.serializers.KafkaAvroSerializer;

public class GenericOrderProducer {
	public static void main(String[] args) {
		// create properties
		Properties props = new Properties();
		props.setProperty("bootstrap.servers", "localhost:9092");
		props.setProperty("key.serializer", KafkaAvroSerializer.class.getName());
		props.setProperty("value.serializer", KafkaAvroSerializer.class.getName());
		props.setProperty("schema.registry.url", "http://localhost:8081");
		
		// create the producer
		KafkaProducer<String, GenericRecord> producer = new KafkaProducer<>(props);
		Parser parser = new Schema.Parser();
		Schema schema = parser.parse("{\r\n"
				+ "  \"namespace\": \"com.demiglace.kafka.avro\",\r\n"
				+ "  \"type\": \"record\",\r\n"
				+ "  \"name\": \"Order\",\r\n"
				+ "  \"fields\": [\r\n"
				+ "    {\r\n"
				+ "      \"name\": \"customerName\",\r\n"
				+ "      \"type\": \"string\"\r\n"
				+ "    },\r\n"
				+ "    {\r\n"
				+ "      \"name\": \"product\",\r\n"
				+ "      \"type\": \"string\"\r\n"
				+ "    },\r\n"
				+ "    {\r\n"
				+ "      \"name\": \"quantity\",\r\n"
				+ "      \"type\": \"int\"\r\n"
				+ "    }\r\n"
				+ "  ]\r\n"
				+ "}\r\n"
				+ "");
		GenericRecord order = new GenericData.Record(schema);
		order.put("customerName", "doge");
		order.put("product", "Macbook");
		order.put("quantity", 100);
		
		ProducerRecord<String, GenericRecord> record = new ProducerRecord<>("OrderAvroGRTopic",
				order.get("customerName").toString(), order);

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
