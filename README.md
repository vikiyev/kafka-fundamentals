# Kafka Fundamentals

Based on Bharath Thippireddy's udemy course [Kafka Fundamentals](https://www.udemy.com/course/kafka-fundamentals-for-java-developers/).

## Kafka Installation

After downloading the Kafka tgz package, we need to extract the tar file, and then extract it again. The `bin/windows` directory contains the batch files for running Kafka. The `config` folder contains all the necessary configuration properties. To start zookeeper in port 2181 and Kafka broker in port 9092:

```bash
zookeeper-server-start C:\kafka\config\zookeeper.properties
kafka-server-start C:\kafka\config\server.properties
```

We can create a topic using

```bash
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic first-topic
kafka-topics --describe --bootstrap-server localhost:9092 --topic first-topic
```

We can start setting up a consumer using

```bash
kafka-console-consumer --bootstrap-server localhost:9092 --topic first-topic
```

and produce messages using

```bash
kafka-console-producer --broker-list localhost:9092 --topic first-topic
> Hello World!
```

## Running Confluent Platform

https://docs.confluent.io/platform/current/platform-quickstart.html#step-1-download-and-start-cp

1. Create a docker-compose.yml from the [repo](https://github.com/confluentinc/cp-all-in-one/tree/7.3.1-post/cp-all-in-one/docker-compose.yml).
2. Start the Confluent Platform stack

```bash
docker-compose up -d
```

3. Verify that the services are up and running:

```bash
docker-compose ps
```

To restart the containers:

```bash
docker-compose restart name
```

## Concepts

### Event Streaming

- Capturing data in real time from envent sources like databases, sensors, etc.
- Ensures a continuous flow and interpretation of data

### Kafka

- A distributed commit log / streaming platform used to collect process store and integrate data at scale
- Processes streams of data in a scalable and fault tolerant matter
- The data and events can be processed as soon as it is produced making Kafka ideal for analytics
- Supports multiple producers and consumers
- In Kafka, a message is retained and another consumer can process it as well
- Kafka supports consumer groups and partitions for parallel processing
- Kafka ensures that for a consumer group, a message will be consumed only once
- Kafka supports disk based persistence in the event that a consumer is down

### Usecases

- PubSub messaging
- Producer-Consumer pattern
- Activity Tracking
- Metrics and Log Aggregation
- Commit Logging
- Stream Processing
- Data Pipelines
- Big Data

### Kafka Architectural Components

1. Broker

- A Kafka cluster is a collection of Kafka brokers/nodes
- Through these brokers, messages are exchanged between producers and consumers
- Decouples producers and consumers
- Ensures that messages are persisted and durable
- One of the brokers is elected as a cluster leader responsible for managing partitions

2. ZooKeeper

- Responsible for electing a cluster controller/leader
- All broker nodes register themselves to the ZooKeeper
- Maintains the state of the cluster

3. Producers

- Application that produces data
- Communicates with the cluster using TCP
- Can send messages to multiple topics, and a topic can receive from multiple producers

4. Consumers

- Appplication that consumes from one or more topics and processes it
- Coordinates among themselves to balance load

### Kafka Record

The producer applications create and exchange data using [Kafka Records](https://kafka.apache.org/23/javadoc/org/apache/kafka/clients/producer/ProducerRecord.html). It has seven properties:

1. Topic - topic to which this record should be returned to. Each topic can be divided into one or more partitions
2. Partition - zero based index to which the record is to be written. A record is always associated with only one partition. Partitions can be scaled across brokers
3. Offset - 64 bit signed int for locating a record within a partition
4. Timestamp - Timestamp of the record
5. Key - Optional non unique value used for calculating the partition number
6. Headers - Optional key value pair for passing in metadata
7. Value - Where the payload lies

### Consumer Groups

A set of consumers working together to consume a topic. It ensures that each partition is only consumed by one consumer. Assigning a consumer to a partition is called **ownership**.

### Batching

A batch is a collection of messages that should be written to the same topic and partition. Kafka producers batches messages based on the topic and partition.

## Kafka Producer API

To create a Producer, we create an instance of the **KafkaProducer** class which takes a key value and properties on the constructor. The **bootstrap.servers** property is a list of brokers the producer should connect to. The **key.serializer** and **value.serializer** are classes for converting java objects into bytes so that the kafka broker can understand it.

The Record object will represent the message with the key, value and the topic it should be sent to. The send method is returns a Future<RecordMetadata> which we can wait for on a synchronous send.

```java
public class OrderProducer {
	public static void main(String[] args) {
		// create properties
		Properties props = new Properties();
		props.setProperty("bootstrap.servers", "localhost:9092");
		props.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.setProperty("value.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");

		// create the producer
		KafkaProducer<String,Integer> producer = new KafkaProducer<String, Integer>(props);
		ProducerRecord<String, Integer> record = new ProducerRecord<>("OrderTopic", "Macbook Pro", 10);

		try {
			// send message
			Future<RecordMetadata> future = producer.send(record);
			RecordMetadata recordMetadata = future.get();
			System.out.println("Message sent!");
			System.out.println(recordMetadata.partition());
			System.out.println(recordMetadata.offset());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			producer.close();
		}
	}
}
```

If we want to send a message asynchronously, we need to add a callback parameter to the send() method that implements the Callback interface.

```java
		try {
			producer.send(record, new OrderCallback()); // async send
		}
```

```java
public class OrderCallback implements Callback {
	@Override
	public void onCompletion(RecordMetadata recordMetadata, Exception exception) {
		System.out.println("Message sent!");
		System.out.println(recordMetadata.partition());
		System.out.println(recordMetadata.offset());
	}
}
```

## Kafka Consumer API

To create a consumer, we pass the properties to **KafkaConsumer**. The consumer needs the **group.id** property in addition to key.deserializer, value.deserializer and bootstrap.servers. A consumer can subscribe to multiple topics. Once subscribed, we need to invoke the **poll()** method to start polling the topic for messages.

```java
public class OrderConsumer {

	public static void main(String[] args) {
		Properties props = new Properties();
		props.setProperty("bootstrap.servers", "localhost:9092");
		props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.IntegerDeserializer");
		props.setProperty("group.id", "OrderTopic");

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
```

## Custom Serializer and Deserializer

Custom serializer and deserializers are needed when working with custom object types to convert these objects into byte array. We can use jackson-databind **ObjectMapper** to convert from/into string then into/from bytes.

```java
public class OrderSerializer implements Serializer<Order> {

	@Override
	public byte[] serialize(String topic, Order order) {
		byte[] response = null;
		// serialization logic
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			response = objectMapper.writeValueAsString(order).getBytes();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return response;
	}
}
```

```java
public class OrderDeserializer implements Deserializer<Order> {
	@Override
	public Order deserialize(String topic, byte[] data) {
		Order order = null;
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			order = objectMapper.readValue(data, Order.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return order;
	}
}
```

```java
public class OrderProducer {
	public static void main(String[] args) {
		// create properties
		Properties props = new Properties();
		props.setProperty("bootstrap.servers", "localhost:9092");
		props.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.setProperty("value.serializer", "com.demiglace.kafka.orderproducer.customserializers.OrderSerializer");

		// create the producer
		KafkaProducer<String,Order> producer = new KafkaProducer<String, Order>(props);
		Order order = new Order();
		order.setCustomerName("Doge");
		order.setProduct("Macbook");
		order.setQuantity(10);
		ProducerRecord<String, Order> record = new ProducerRecord<>("OrderCSTopic", order.getCustomerName(), order);

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
```

```java
public class OrderConsumer {
	public static void main(String[] args) {
		Properties props = new Properties();
		props.setProperty("bootstrap.servers", "localhost:9092");
		props.setProperty("key.deserializer", StringDeserializer.class.getName());
		props.setProperty("value.deserializer", OrderDeserializer.class.getName());
		props.setProperty("group.id", "OrderTopic");

		// create the consumer
		KafkaConsumer<String, Order> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Collections.singletonList("OrderCSTopic"));

		// poll the topic
		ConsumerRecords<String, Order> records = consumer.poll(Duration.ofSeconds(20));

		for (ConsumerRecord<String, Order> record : records) {
			String customerName = record.key();
			Order order = record.value();
			System.out.println("Product: " +  order.getProduct());
		}

		consumer.close();
	}
}
```

## Avro

Apache Avro is a framework that can serialize and deserialize objects out of the box, we just need to define a JSON schema. We can also use this schema to generate a java class. Each record that is produces will comply to the schema. As different consumers might need different versions of a schema, we can use Schema registries such as Confluent's schema registry at port 8081.

An Avro (.avsc) schema uses JSON syntax. The **namespace** will be used for the package name and the **name** for the class name. The Order POJO will be created by the avro-maven-plugin from this schema, which will include annotations from avro.

```json
{
  "namespace": "com.demiglace.kafka.avro",
  "type": "record",
  "name": "Order",
  "fields": [
    {
      "name": "customerName",
      "type": "string"
    },
    {
      "name": "product",
      "type": "string"
    },
    {
      "name": "quantity",
      "type": "int"
    }
  ]
}
```

To use avro, we need the following plugins. The Order class will be generated after doing a maven generate-sources

```xml
<repositories>
	<repository>
		<id>confluent</id>
		<url>http://packages.confluent.io/maven/</url>
		<releases>
			<enabled>true</enabled>
		</releases>
		<snapshots>
			<enabled>true</enabled>
		</snapshots>
	</repository>
</repositories>

<dependency>
	<groupId>org.apache.avro</groupId>
	<artifactId>avro</artifactId>
	<version>1.10.2</version>
</dependency>
<dependency>
	<groupId>io.confluent</groupId>
	<artifactId>kafka-avro-serializer</artifactId>
	<version>6.2.0</version>
</dependency>

<build>
	<plugins>
		<plugin>
			<groupId>org.apache.avro</groupId>
			<artifactId>avro-maven-plugin</artifactId>
			<version>1.10.2</version>
			<executions>
				<execution>
					<phase>generate-sources</phase>
					<goals>
						<goal>schema</goal>
					</goals>
					<configuration>
						<sourceDirectory>${project.basedir}/src/main/resources/</sourceDirectory>
						<outputDirectory>${project.basedir}/src/main/java</outputDirectory>
					</configuration>
				</execution>
			</executions>
		</plugin>
	</plugins>
</build>
```

Avro Serializers will automatically push the schema into the registry. For the producer properties, we use the following

```java
		props.setProperty("bootstrap.servers", "localhost:9092");
		props.setProperty("key.serializer", KafkaAvroSerializer.class.getName());
		props.setProperty("value.serializer", KafkaAvroSerializer.class.getName());
		props.setProperty("schema.registry.url", "http://localhost:8081");

ProducerRecord<String, Order> record = new ProducerRecord<>("OrderAvroTopic", order.getCustomerName().toString(), order);
```

For the consumer, we use the following. Avro supports specific readers for specific types.

```java
		props.setProperty("bootstrap.servers", "localhost:9092");
		props.setProperty("key.deserializer", KafkaAvroDeserializer.class.getName());
		props.setProperty("value.deserializer", KafkaAvroDeserializer.class.getName());
		props.setProperty("group.id", "OrderGroup");
		props.setProperty("schema.registry.url", "http://localhost:8081");
		props.setProperty("specific.avro.reader", "true");
```

The schema will automatically be uploaded by the producer to `http://localhost:8081/schemas`
