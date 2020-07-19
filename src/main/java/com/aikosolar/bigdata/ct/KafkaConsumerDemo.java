package com.aikosolar.bigdata.ct;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;


/**
 * kafka消费者demo
 *
 * @author xiaowei.song
 * @date 2020/06/22 08:55
 */
public class KafkaConsumerDemo {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerDemo.class);

    public static final String PROPERTIES_FILE_PATH = "application.properties";

    private static final Properties globalProperties = new Properties();

    static {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classLoader
                .getResourceAsStream(PROPERTIES_FILE_PATH);
        try {
            globalProperties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("加载配置文件失败, properties file path " + PROPERTIES_FILE_PATH);
            System.exit(1);
        }
    }

    public static void main(String[] args) throws IOException {
//        runProducer();
        runConsumer();
    }

    static void runConsumer() throws IOException {
        Consumer<String, String> consumer = createConsumer();
        int noMessageFound = 0;
        int consumerCount = 0;
        List<EqpCTSource> eqpCTSourceList = new ArrayList<>(100);
        while (true) {
            if (consumerCount == 3) {
                break;
            }
            ConsumerRecords<String, String> consumerRecords = consumer.poll(1000);
            // 1000 is the time in milliseconds consumer will wait if no record is found at broker.
            if (consumerRecords.count() == 0) {
                noMessageFound++;
                if (noMessageFound > 100)
                    // If no message found count is reached to threshold exit loop.
                    break;
                else
                    continue;
            }
            consumerCount++;
            //print each record.
            consumerRecords.forEach(record -> {
                EqpCTSource eqpCTSource = JSON.parseObject(record.value(), EqpCTSource.class);
                eqpCTSourceList.add(eqpCTSource);
            });
            // commits the offset of record to broker.
            consumer.commitAsync();
        }
        logger.info("开始写入文件...");
        FileUtils.writeStringToFile(new File(String.format("E:\\df3-%d.json", consumerCount)), JSON.toJSONString(eqpCTSourceList), StandardCharsets.UTF_8, true);
        logger.info("写入文件完成");
        consumer.close();
    }
/*
    static void runProducer() {
        Producer<Long, String> producer = ProducerCreator.createProducer();
        for (int index = 0; index < IKafkaConstants.MESSAGE_COUNT; index++) {
            ProducerRecord<Long, String> record = new ProducerRecord<Long, String>(IKafkaConstants.TOPIC_NAME,
                    "This is record " + index);
            try {
                RecordMetadata metadata = producer.send(record).get();
                System.out.println("Record sent with key " + index + " to partition " + metadata.partition()
                        + " with offset " + metadata.offset());
            } catch (ExecutionException e) {
                System.out.println("Error in sending record");
                System.out.println(e);
            } catch (InterruptedException e) {
                System.out.println("Error in sending record");
                System.out.println(e);
            }
        }
    }*/

//    public static Producer<Long, String> createProducer() {
//        Properties props = new Properties();
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, IKafkaConstants.KAFKA_BROKERS);
//        props.put(ProducerConfig.CLIENT_ID_CONFIG, IKafkaConstants.CLIENT_ID);
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//        //props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, CustomPartitioner.class.getName());
//        return new KafkaProducer<>(props);
//    }

    public static Consumer<String, String> createConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, globalProperties.getProperty("bootstrap.servers"));
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "look_sxw_dev5");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        Consumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(globalProperties.getProperty("job2.etl.source.topic")));
        return consumer;
    }
}