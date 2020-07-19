package com.aikosolar.bigdata.ct;

import com.aikosolar.bigdata.ct.sink.SinkHbase;
import com.aikosolar.bigdata.ct.util.MapUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.*;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple23;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 按照新改的数据格式，做etl清洗
 * 1. 实时计算ct时间，存储到hbase中 yw_data_df_online_dt
 * 2. 离线计算ct时间，每天晚上凌晨计算，计算完成之后清理 yw_data_df_offline_dt
 *                  每天晚上凌晨1点计算，计算完成之后清理 yw_data_df_offline_backup_dt 备库
 * @author xiaowei.song
 */
public class JobMain {

    private static final Logger logger = LoggerFactory.getLogger(JobMain.class);

    public static final String PROPERTIES_FILE_PATH = "application.properties";

    public static ParameterTool parameterTool = null;

    public static final ObjectMapper mapper = new ObjectMapper();

    static {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classLoader
                .getResourceAsStream(PROPERTIES_FILE_PATH);
        try {
            parameterTool = ParameterTool.fromPropertiesFile(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("加载配置文件失败, properties file path " + PROPERTIES_FILE_PATH);
            System.exit(1);
        }
    }

    public static Map<String, Map<Integer, Long>> tubeRunCountTestTimeMap = new ConcurrentHashMap<>(6);


    public static void main(String[] args) throws Exception {
        if (parameterTool.getNumberOfParameters() < 1) {
            return;
        }

        // 1. 创建流式环境
        // 正式环境
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //本地调试模式，pom文件中scope需要改为compile
//        final StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();

        // 修改并行度
        env.setParallelism(1);

        // CheckPoint 配置
        env.enableCheckpointing(60000);
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(30000);
        env.getCheckpointConfig().setCheckpointTimeout(10000);
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
//        env.setStateBackend(new RocksDBStateBackend)
        // 默认采用TimeCharacteristic.ProcessingTime策略
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        //9da7661ca7ce81df7672427136a174bf
        StateBackend stateBackend = new FsStateBackend(" hdfs:///user/flink/checkpoints");
        env.setStateBackend(stateBackend);

        //2 .指定kafak相关信息
        final String bootstrapServers = parameterTool.get("bootstrap.servers");
        final String etlSourceTopic = parameterTool.get("job.etl.source.topic");
        final String etlSourceGroupID = parameterTool.get("job.etl.source.group.id");
        final String etlSourceCommitIntervalMS = parameterTool.get("job.etl.source.auto.commit.interval.ms");
        final String etlSourceOffsetReset = parameterTool.get("job.etl.source.auto.offset.reset");

        final String onLineHbaseTable = parameterTool.get("job.online.hbase.table");
        final String offlineHbaseTable = parameterTool.get("job.offline.hbase.table");
//        final String offlineHbaseBackupTable = parameterTool.get("job.offline.backup.hbase.table");

        // 3. 创建Kafka数据流
        Properties kafkaConsumerProps = new Properties();
        kafkaConsumerProps.setProperty("bootstrap.servers", bootstrapServers);
        kafkaConsumerProps.setProperty("group.id", etlSourceGroupID);
        kafkaConsumerProps.setProperty("auto.commit.interval.ms", etlSourceCommitIntervalMS);
        kafkaConsumerProps.setProperty("auto.offset.reset", etlSourceOffsetReset);
        FlinkKafkaConsumer010<String> flinkKafkaConsumer = new FlinkKafkaConsumer010<>(
                etlSourceTopic,
                new SimpleStringSchema(),
                kafkaConsumerProps);
//        flinkKafkaConsumer.setStartFromEarliest();
        flinkKafkaConsumer.setStartFromGroupOffsets();
        // 使用checkPoint的offset状态保持
        flinkKafkaConsumer.setCommitOffsetsOnCheckpoints(true);

        //4 .设置数据源
        DataStream<String> kafkaDataStream = env.addSource(flinkKafkaConsumer)
                .uid(etlSourceTopic)
                .name(etlSourceTopic)
                .setParallelism(11);

        // 5. 打印数据
        SingleOutputStreamOperator<EqpCTSource2> tubeChangeStateDS = kafkaDataStream.map(x -> {
            JsonNode jn = mapper.readTree(x);
            EqpCTSource eqpCTSource = new EqpCTSource();
            JsonNode jnData = jn.get("data");
            String testDate = jnData.get("TestDate").asText("01-01-1970");
            String testTime = jnData.get("TestTime").asText("00:00:00");
            testTime = String.format("%s %s", testDate, testTime);
            eqpCTSource.dayHour = jnData.get("").asText();
        })
                .name("df-map-string-to-jsonObject")
                .uid("df-map-string-to-jsonObject")
                .map(new RichMapFunction<JSONObject, EqpCTSource2>() {
                    @Override
                    public EqpCTSource2 map(JSONObject js) throws Exception {
                        Map<String, String> suffixMap = new HashMap<>(7);
                        HashMap<String, String> tube2BoatMap = new HashMap<>(12);
                        //分区字段
                        final SimpleDateFormat defaultSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        final SimpleDateFormat dsSdf = new SimpleDateFormat("yyyyMMdd");

                        EqpCTSource2 tube = new EqpCTSource2();
                        tube.eqpID = js.getOrDefault("EqpID", "").toString();
                        // 默认site填充为浙江义乌二期
                        tube.site = "Z2";
                        if (StringUtils.isNotBlank(tube.eqpID)) {
                            tube.site = StringUtils.split(tube.eqpID, "-")[0];
                        }
                        if (StringUtils.containsIgnoreCase(tube.eqpID, "-B")) {
                            tube.eqpID = StringUtils.replace(tube.eqpID, "-B", "-");
                        }

                        tube.clock = js.getOrDefault("updatetime", "1970-01-01 00:00:00").toString();
                        tube.tubeID = StringUtils.replace(js.getString("TubeID"), "Tube", "0");
                        tube.tubeID = String.format("%02d", Integer.valueOf(tube.tubeID));
                        Iterator<String> iterator = js.keySet().iterator();
                        while (iterator.hasNext()) {
                            String key = iterator.next();
                            String value = js.getOrDefault(key, "").toString();
                            if (StringUtils.startsWith(key, "Tube") && StringUtils.containsIgnoreCase(key, "Text")) {
                                if (StringUtils.endsWith(key, "Text1")) {
                                    tube.text1 = value;
                                } else if (StringUtils.endsWith(key, "Text2")) {
                                    tube.text2 = value;
                                } else if (StringUtils.endsWith(key, "Text3")) {
                                    tube.text3 = value;
                                } else if (StringUtils.endsWith(key, "Text4")) {
                                    tube.text4 = value;
                                }
                            } else if (StringUtils.startsWith(key, "Tube") && StringUtils.containsIgnoreCase(key, "State")) {
                                tube.state = value;
                            } else if (StringUtils.startsWith(key, "Tube") && StringUtils.containsIgnoreCase(key, "@")) {
                                String keyNew = key.substring(key.indexOf("@") + 1);
                                suffixMap.put(keyNew, value);
                            } else if (StringUtils.startsWith(key, "Boat") && StringUtils.endsWith(key, "Tube%String")) {
                                if (StringUtils.containsIgnoreCase(value, "-") && !StringUtils.equals(value, "-1")) {
                                    String boatNo = StringUtils.split(key, "@")[0];
                                    String boatIDKey = String.format("%s@BoatID%%String", boatNo);
                                    String boatID = boatNo.replace("Boat", "");
                                    if (js.containsKey(boatIDKey)) {
                                        boatID = js.getString(boatIDKey);
                                    }
                                    value = StringUtils.split(value, "-")[1];
                                    // 目前左右管区分不了，若之前已经存在舟记录，则逗号分隔，添加舟信息
                                    if (tube2BoatMap.containsKey(value)) {
                                        boatID = String.format("%s,%s", boatID, tube2BoatMap.get(value));
                                    }
                                    tube2BoatMap.put(value, boatID);
                                }
                            }
                        }
                        tube.boatID = tube2BoatMap.getOrDefault(js.getString("TubeID").replace("Tube", ""), "-1");
                        tube.gasN2_POCl3VolumeAct = Double.valueOf(MapUtil.getValueOrDefault( suffixMap, "Gas@N2_POCl3@VolumeAct%Double", "-100"));
                        tube.gasPOClBubbLeve = Double.valueOf(MapUtil.getValueOrDefault( suffixMap, "Gas@POClBubb@Level%Float", "-100"));
                        tube.gasPOClBubbTempAct = Double.valueOf(MapUtil.getValueOrDefault( suffixMap, "Gas@POClBubb@TempAct%Float", "-100"));
                        tube.dataVarAllRunCount = Double.valueOf(MapUtil.getValueOrDefault( suffixMap, "DataVar@All@RunCount%Double", "-100")).intValue();
                        // 兼容runCount单独情况
                        if (suffixMap.get("DataVar@All@RunCount%Double") == null) {
                            String runCountStr = js.getString("RunCount");
                            runCountStr = StringUtils.isBlank(runCountStr)?"-100": runCountStr;
                            tube.dataVarAllRunCount = Double.valueOf(runCountStr).intValue();
                        }
                        tube.dataVarAllRunNoLef = Double.valueOf(MapUtil.getValueOrDefault(suffixMap, "DataVar@All@RunNoLef%Double", "-1")).intValue();
                        tube.vacuumDoorPressure = MapUtil.getValueOrDefault( suffixMap, "Vacuum@Door@Pressure%Float", "-100");
                        tube.dataVarAllRunTime = MapUtil.getValueOrDefault( suffixMap, "DataVar@All@RunTime%Double", "-1");
                        tube.recipe = MapUtil.getValueOrDefault(suffixMap, "DataVar@All@Recipe%String", "");
                        tube.timeSecond = defaultSdf.parse(tube.clock).getTime() / 1000;
                        tube.ds = dsSdf.format(defaultSdf.parse(tube.clock));
                        tube.testTime = tube.clock;
                        tube.id = String.format("%s-%s", tube.eqpID, tube.tubeID);
                        tube.rowkey = String.format("%s|%s", new StringBuffer(String.valueOf(tube.timeSecond)).reverse().toString(), tube.id);
                        return tube;
                    }
                })
                .name("df-map-jsonObject-to-DFTube2")
                .uid("df-map-jsonObject-to-DFTube2");

        List<String> columnFamily = Collections.unmodifiableList(Arrays.asList(
                "main",
                "info"
        ));

        List<String> setColumns = Collections.unmodifiableList(Arrays.asList(
                "main:eqp_id",
                "main:site",
                "main:state",
                "main:clock",
                "main:tube_id",
                "main:text1",
                "main:text2",
                "main:text3",
                "main:text4",
                "main:boat_id",
                "main:gas_poci_bubb_leve",
                "main:gas_n2_poci3_volume_act",
                "main:gas_poci_bubb_temp_act",
                "main:recipe",
                "main:run_count",
                "main:run_no_lef",
                "main:vacuum_door_Pressure",
                "main:run_time",
                "main:time_second",
                "main:test_time",
                "main:end_time",
                "main:ct",
                "main:ds"
        ));

        // hbase中作为离线数据存储数据
        SingleOutputStreamOperator<DFTuple<Tuple23>> offlineSos =  tubeChangeStateDS
                .keyBy("id")
                .map(new RichMapFunction<EqpCTSource2, DFTuple<Tuple23>>() {
                    @Override
                    public DFTuple<Tuple23> map(EqpCTSource2 v) throws Exception {
                        return JobMain.packagingDFTuple(v);
                    }
                })
                .name("df-map-DFTube2-to-DFTuple-offline")
                .uid("df-map-DFTube2-to-DFTuple-offline");


        // 输出离线数据存储
        offlineSos
                .addSink(new SinkHbase<>(offlineHbaseTable, columnFamily, setColumns))
                .name("df-offline-save-to-hbase")
                .uid("df-offline-save-to-hbase");

        SingleOutputStreamOperator<EqpCTSource2> resultSos = tubeChangeStateDS
                .keyBy("id")
                .countWindow(2, 1)
                .reduce(new ReduceFunction<EqpCTSource2>() {
                    @Override
                    public EqpCTSource2 reduce(EqpCTSource2 v1, EqpCTSource2 v2) throws Exception {
                        if (!v2.dataVarAllRunCount.equals(v1.dataVarAllRunCount)) {
                            // 重置状态值，若设置为1，则可利用本条数据和前一条数据进行计算，对于的end_time和ct时间可计算出
                            v2.firstStatus = 0;
                            // 若是顺序数据，不缺数据，则取run count变化这条数据,反之，缺数据，打标为非上一条数据结束时间
                            if (v2.dataVarAllRunCount.equals(v1.dataVarAllRunCount + 1)) {
                                v2.firstStatus = 1;
                            } else if (v2.dataVarAllRunCount < (v1.dataVarAllRunCount + 1)) {
                                return null;
                            }
                            return v2;
                        }
                        return null;
                    }
                })
                .name("df-reduce-DFTube2-run-count-first")
                .uid("df-reduce-DFTube2-run-count-first")
                .filter((FilterFunction<EqpCTSource2>) v -> v != null)
                .name("df-filter-DFTube2-not-null")
                .uid("df-filter-DFTube2-not-null")
                .keyBy("id")
                .countWindow(2, 1)
                .reduce(new ReduceFunction<EqpCTSource2>() {
                    @Override
                    public EqpCTSource2 reduce(EqpCTSource2 v1, EqpCTSource2 v2) throws Exception {
                        if (v2.firstStatus == 1) {
                            final SimpleDateFormat defaultSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            final SimpleDateFormat dsSdf = new SimpleDateFormat("yyyyMMdd");
                            v1.endTime = defaultSdf.format(new Date(v1.timeSecond * 1000 + Double.valueOf(v2.dataVarAllRunTime).longValue() * 1000));
                            v1.ct = Double.valueOf(v2.dataVarAllRunTime).longValue();
                        }
                        return v1;
                    }
                }).name("df-calculate-ct")
                .uid("df-calculate-ct");

        resultSos.print();
        resultSos.map(new RichMapFunction<EqpCTSource2, DFTuple<Tuple23>>() {
            @Override
            public DFTuple<Tuple23> map(EqpCTSource2 v) throws Exception {
                return JobMain.packagingDFTuple(v);
            }
        }).name("df-map-DFTube2-to-DFTuple-online")
          .uid("df-map-DFTube2-to-DFTuple-online")
          .addSink(new SinkHbase<>(onLineHbaseTable, columnFamily, setColumns))
          .name("df-online-save-to-hbase")
          .uid("df-online-save-to-hbase");

        logger.info("job start...");
        // 6.执行任务
        env.execute(parameterTool.get("job.name"));
    }

    public static DFTuple<Tuple23> packagingDFTuple(EqpCTSource2 v) throws Exception {
        DFTuple<Tuple23> dfTuple = new DFTuple();
        dfTuple.data = Tuple23.of(v.eqpID, v.site, v.state, v.clock, v.tubeID, v.text1, v.text2, v.text3, v.text4,
                v.boatID, v.gasPOClBubbLeve, v.gasN2_POCl3VolumeAct, v.gasPOClBubbTempAct,
                v.recipe, v.dataVarAllRunCount, v.dataVarAllRunNoLef, v.vacuumDoorPressure,
                v.dataVarAllRunTime, v.timeSecond, v.testTime, v.endTime, v.ct, v.ds);
        dfTuple.rowKey = v.rowkey;
        return dfTuple;
    }
}
