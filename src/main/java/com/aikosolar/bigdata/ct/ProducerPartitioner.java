package com.aikosolar.bigdata.ct;

import org.apache.flink.streaming.connectors.kafka.partitioner.FlinkKafkaPartitioner;

/**
 * @author xiaowei.song
 * @date 2020-06-21 22:03
 */
public class ProducerPartitioner extends FlinkKafkaPartitioner<String> {
    @Override
    public int partition(String key, byte[] bytes, byte[] bytes1, String s, int[] ints) {
        return 0;
    }
}
