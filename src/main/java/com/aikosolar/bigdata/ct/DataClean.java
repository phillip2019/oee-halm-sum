package com.aikosolar.bigdata.ct;

import com.aikosolar.bigdata.ct.sink.MyRedisSource;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoFlatMapFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;
import org.apache.flink.streaming.connectors.kafka.internals.KeyedSerializationSchemaWrapper;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Properties;

/**
 * @author xiaowei.song
 * @version v1.0.0
 * @description TODO
 * @date 2020/06/30 13:20
 */
public class DataClean {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //修改并行度
        env.setParallelism(5);
        // CheckPoint 配置
        env.enableCheckpointing(60000);
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(30000);
        env.getCheckpointConfig().setCheckpointTimeout(10000);
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        //设置 StateBackend
//        env.setStateBackend(new RocksDBStateBackend("hdfs://hadoop01:9000/flink/checkpoints", true));
        //指定 KafkaSource
        String topic = "allData";
        Properties prop = new Properties();
        prop.setProperty(" bootstrap.servers", " kafka01:9092,kafka02:9092");
        prop.setProperty(" group.id", " con1");
        FlinkKafkaConsumer010<String> myConsumer = new FlinkKafkaConsumer010<>(topic, new SimpleStringSchema(), prop);
        //获取 Kafka 中的 数据 //{"dt":" 2019- 01- 01 11: 11: 11"," countryCode":" US"," data":[{" type":" s1"," score": 0. 3," level":" A"},{" type":" s2"," score": 0. 1," level":" B"}]}
        DataStreamSource<String> data = env.addSource(myConsumer);
        //最新的国家码和大区的映射关系
        DataStream<HashMap<String, String>> mapData = env.addSource(new MyRedisSource()).broadcast();
        //可以把数据发送到后的算子的所有并行实例中
        DataStream<String> resData = data
                .connect(mapData)
                .flatMap(new CoFlatMapFunction<String, HashMap<String, String>, String>() {
                    //存储国家和大区的映射关系
                    private HashMap<String, String> allMap = new HashMap<>();

                    //flatMap1处理的是Kafka中的数据
                    public void flatMap1(String value, Collector<String> out) throws Exception {
                        JSONObject jsonObject = JSONObject.parseObject(value);
                        String dt = jsonObject.getString(" dt");
                        String countryCode = jsonObject.getString(" countryCode"); //获取 大区
                        String area = allMap.get(countryCode);
                        JSONArray jsonArray = jsonObject.getJSONArray(" data");
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            jsonObject1.put(" area", area);
                            jsonObject1.put(" dt", dt);
                            out.collect(jsonObject1.toJSONString());
                        }
                    }

                    //flatMap2处理的是Redis返回的Map类型的数据
                    public void flatMap2(HashMap<String, String> value, Collector<String> out) throws Exception {
                        this.allMap = value;
                    }
                })
                .name("补充地址的flat map")
                .uid("fill over");
        String outTopic = "allDataClean";
        Properties outprop = new Properties();
        outprop.setProperty(" bootstrap.servers", " kafka01:9092,kafka02:9092");
        //第一 种 解决 方案， 设置 FlinkKafkaProducer010 中的 事务 超时 时间
//        prop.setProperty(" transaction.timeout.ms", 60000 * 15 + "");
        //第二 种 解决 方案， 设置 Kafka 的 最大 事务 超时 时间
        FlinkKafkaProducer010<String> myProducer = new FlinkKafkaProducer010<>(outTopic, new KeyedSerializationSchemaWrapper<>(new SimpleStringSchema()), outprop);
        resData.addSink(myProducer);
        env.execute(" DataClean");
    }
}