package com.aikosolar.bigdata.ct;

import org.apache.flink.streaming.connectors.kafka.partitioner.FlinkKafkaPartitioner;

/**
 * @author xiaowei.song
 * @date 2020-06-21 22:03
 */
public class DFTubePartitioner extends FlinkKafkaPartitioner<EqpCTSource> {
    @Override
    public int partition(EqpCTSource tube, byte[] bytes, byte[] bytes1, String s, int[] ints) {
        return tube.eqpID.hashCode() % ints.length;
    }
}
