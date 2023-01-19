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
