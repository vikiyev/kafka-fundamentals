package com.demiglace.kafka.streamsdemo;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;

public class DataFlowStream {
	public static void main(String[] args) {
		// define props
		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-dataflow");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

		// set up topology
		StreamsBuilder builder = new StreamsBuilder();
		KStream<String, String> stream = builder.stream("streams-dataflow-input");

		// computational logic
		stream.foreach((key, value) -> System.out.println("key " + key + " value " + value));
		stream.filter((key, value) -> value.contains("token"))
			// .mapValues(value->value.toUpperCase())
			.map((key,value) -> new KeyValue<>(value, value.toUpperCase()))
			.to("streams-dataflow-output");

		Topology topology = builder.build();
		System.out.println(topology.describe());

		// starting the stream
		KafkaStreams streams = new KafkaStreams(topology, props);
		streams.cleanUp();
		streams.start();

		// stopping the stream through the current runtime
		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
	}
}
