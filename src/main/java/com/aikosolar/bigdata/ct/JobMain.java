package com.aikosolar.bigdata.ct;

import com.aikosolar.bigdata.ct.sink.CTSinkHBase;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;
import net.sf.cglib.beans.BeanCopier;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.*;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicPartition;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicPartitionAssigner;
import org.apache.flink.streaming.connectors.kafka.internals.KeyedSerializationSchemaWrapper;
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema;
import org.apache.flink.util.OutputTag;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.fasterxml.jackson.annotation.JsonInclude.*;

/**
 * 按照新改的数据格式，做etl清洗
 * @author xiaowei.song
 */
public class JobMain {

    private static final Logger logger = LoggerFactory.getLogger(JobMain.class);

    public static final String PROPERTIES_FILE_PATH = "application.properties";

    public static ParameterTool parameterTool = null;

    /**
     * 加载属性配置文件
     **/
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

    public static final ObjectMapper mapper = new ObjectMapper();

    /**
     * site factory转换
     **/
    public static final Map<String, String> SITE2FACTORY_MAP = ImmutableMap.<String, String>builder()
            .put("G1", "1")
            .put("Z1", "2")
            .put("T1", "3")
            .put("Z2", "4")
            .put("Z3", "4")
            .build();

    /**
     * 订单类型映射表
     **/
    public static final Map<String, String> ORDER_TYPE_MAP = ImmutableMap.<String, String>builder()
            .put("yp", "YP")
            .put("pl", "PL")
            .put("by", "BY")
            .put("yz", "YZ")
            .put("ly", "LY")
            .put("cfx", "CFX")
            .put("cc", "CC")
            .put("gsq", "GSQ")
            .put("gsh", "GSH")
            .put("cid", "CID")
            .build();

    public static final Map<String, Range<Double>> ETA_GRADE_MAP = ImmutableMap.<String, Range<Double>>builder()
            .put("num213", Range.closedOpen(00.00D, 00.00D))
            .put("num214", Range.closedOpen(00.00D, 00.00D))
            .put("num215", Range.closedOpen(00.00D, 00.00D))
            .put("num216", Range.closedOpen(00.00D, 00.00D))
            .put("num217", Range.closedOpen(00.00D, 00.00D))
            .put("num218", Range.closedOpen(00.00D, 21.95D))
            .put("num219", Range.closedOpen(21.95D, 22.06D))
            .put("num220", Range.closedOpen(22.06D, 22.16D))
            .put("num221", Range.closedOpen(22.16D, 22.27D))
            .put("num222", Range.closedOpen(22.27D, 22.37D))
            .put("num223", Range.closedOpen(22.37D, 22.48D))
            .put("num224", Range.closedOpen(22.48D, 22.58D))
            .put("num225", Range.closedOpen(22.58D, 22.69D))
            .put("num226", Range.closedOpen(22.69D, 22.79D))
            .put("num227", Range.closedOpen(22.79D, 22.79D))
            .put("num228", Range.closedOpen(22.79D, 22.79D))
            .put("num229", Range.closedOpen(00.00D, 00.00D))
            .put("num230", Range.closedOpen(00.00D, 00.00D))
            .put("num231", Range.closedOpen(00.00D, 00.00D))
            .put("num232", Range.closedOpen(00.00D, 00.00D))
            .put("num233", Range.closedOpen(00.00D, 00.00D))
            .put("num234", Range.closedOpen(00.00D, 00.00D))
            .put("num235", Range.closedOpen(00.00D, 00.00D))
            .put("num236", Range.closedOpen(00.00D, 00.00D))
            .put("num237", Range.closedOpen(00.00D, 00.00D))
            .put("num238", Range.closedOpen(00.00D, 00.00D))
            .put("num239", Range.closedOpen(00.00D, 00.00D))
            .put("num240", Range.closedOpen(00.00D, 00.00D))
            .build();

    public static final Map<String, Range<Double>> ETA_GRADE_08_06_MAP = ImmutableMap.<String, Range<Double>>builder()
            .put("num213", Range.closedOpen(00.00D, 00.00D))
            .put("num214", Range.closedOpen(00.00D, 00.00D))
            .put("num215", Range.closedOpen(00.00D, 00.00D))
            .put("num216", Range.closedOpen(00.00D, 00.00D))
            .put("num217", Range.closedOpen(00.00D, 00.00D))
            .put("num218", Range.closedOpen(00.00D, 21.95D))
            .put("num219", Range.closedOpen(21.95D, 22.06D))
            .put("num220", Range.closedOpen(22.06D, 22.16D))
            .put("num221", Range.closedOpen(22.16D, 22.27D))
            .put("num222", Range.closedOpen(22.27D, 22.37D))
            .put("num223", Range.closedOpen(22.37D, 22.48D))
            .put("num224", Range.closedOpen(22.48D, 22.58D))
            .put("num225", Range.closedOpen(22.58D, 22.69D))
            .put("num226", Range.closedOpen(22.69D, 22.79D))
            .put("num227", Range.closedOpen(22.79D, 22.90D))
            .put("num228", Range.closedOpen(22.90D, 99.99D))
            .put("num229", Range.closedOpen(00.00D, 00.00D))
            .put("num230", Range.closedOpen(00.00D, 00.00D))
            .put("num231", Range.closedOpen(00.00D, 00.00D))
            .put("num232", Range.closedOpen(00.00D, 00.00D))
            .put("num233", Range.closedOpen(00.00D, 00.00D))
            .put("num234", Range.closedOpen(00.00D, 00.00D))
            .put("num235", Range.closedOpen(00.00D, 00.00D))
            .put("num236", Range.closedOpen(00.00D, 00.00D))
            .put("num237", Range.closedOpen(00.00D, 00.00D))
            .put("num238", Range.closedOpen(00.00D, 00.00D))
            .put("num239", Range.closedOpen(00.00D, 00.00D))
            .put("num240", Range.closedOpen(00.00D, 00.00D))

            .build();

    public static final Map<String, Method> EQP_CT_SOURCE_METHOD = new HashMap<>();

    public static final boolean DEBUG = false;
    static {
        // 获去对象对应的类
        Class clazz = EqpCTSource.class;
        Method[] invokableSourceList = clazz.getMethods();
        for (Method method : invokableSourceList) {
            EQP_CT_SOURCE_METHOD.put(method.getName(), method);
        }
    }

    /**
     * 配置jackson配置
     **/
    static {
        // 该特性决定了当遇到未知属性（没有映射到属性，没有任何setter或者任何可以处理它的handler），是否应该抛出一个JsonMappingException异常
        mapper.configure(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS, false);
        //在序列化时日期格式默认为 yyyy-MM-dd'T'HH:mm:ss.SSSZ
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        //在序列化时忽略值为 null 的属性
        mapper.setSerializationInclusion(Include.ALWAYS);
        //忽略值为默认值的属性
        mapper.setDefaultPropertyInclusion(Include.ALWAYS);
        //设置JSON时间格式
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        //对于日期类型为 java.time.LocalDate，还需要添加代码 mapper.registerModule(new JavaTimeModule())，同时添加相应的依赖 jar 包
        mapper.registerModule(new JavaTimeModule());
    }

    public static Map<String, Map<Integer, Long>> tubeRunCountTestTimeMap = new ConcurrentHashMap<>(6);


    public static void main(String[] args) throws Exception {
        if (parameterTool.getNumberOfParameters() < 1) {
            return;
        }

        // 1. 创建流式环境
        StreamExecutionEnvironment env;
        if (!DEBUG) {
            // 正式环境
            env = StreamExecutionEnvironment.getExecutionEnvironment();
        } else {
            //本地调试模式，pom文件中scope需要改为compile
            env = StreamExecutionEnvironment.createLocalEnvironment();
        }

        // 修改并行度
        env.setParallelism(3);

        // CheckPoint 配置,15分钟开启一次checkpoint
        env.enableCheckpointing(900000);
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(30000);
        env.getCheckpointConfig().setCheckpointTimeout(10000);
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
//        env.setStateBackend(new RocksDBStateBackend)
        // 默认采用TimeCharacteristic.ProcessingTime策略
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        //9da7661ca7ce81df7672427136a174bf
        StateBackend stateBackend;
        if (!DEBUG) {
            stateBackend = new FsStateBackend(" hdfs:///user/flink/checkpoints");
        } else {
            stateBackend = new FsStateBackend(" file:///user/flink/checkpoints");
        }
        env.setStateBackend(stateBackend);

        //2 .指定kafak相关信息
        final String bootstrapServers = parameterTool.get("bootstrap.servers");
        final String etlSourceTopic = parameterTool.get("job.etl.source.topic");
        String etlSourceGroupID = parameterTool.get("job.etl.source.group.id");
        if (DEBUG) {
            etlSourceGroupID = parameterTool.get("job.etl.source.test.group.id");
        }
        final String etlSourceCommitIntervalMS = parameterTool.get("job.etl.source.auto.commit.interval.ms");
        final String etlSourceOffsetReset = parameterTool.get("job.etl.source.auto.offset.reset");
        String onLineHbaseTable = parameterTool.get("job.online.hbase.table");
        if (DEBUG) {
            onLineHbaseTable = parameterTool.get("job.online.test.hbase.table");
        }

        // 3. 创建Kafka数据流
        Properties kafkaConsumerProps = new Properties();
        kafkaConsumerProps.setProperty("bootstrap.servers", bootstrapServers);
        kafkaConsumerProps.setProperty("group.id", etlSourceGroupID);
        kafkaConsumerProps.setProperty("auto.commit.interval.ms", etlSourceCommitIntervalMS);
        kafkaConsumerProps.setProperty("auto.offset.reset", etlSourceOffsetReset);
        FlinkKafkaConsumer011<String> flinkKafkaConsumer = new FlinkKafkaConsumer011<>(
                etlSourceTopic,
                new SimpleStringSchema(),
                kafkaConsumerProps);

        // 创建kafka生产者
        String tmpTargetTopic = parameterTool.get("tmp.target.topic");
        if (DEBUG) {
            tmpTargetTopic = parameterTool.get("tmp.target.test.topic");
        }

        Properties kafkaProducer = new Properties();
        kafkaProducer.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        FlinkKafkaProducer010<String> kafkaProducerSink = new FlinkKafkaProducer010<String>(
                tmpTargetTopic,
                new SimpleStringSchema(),
                kafkaProducer,
                new ProducerPartitioner());

        if (DEBUG) {
            flinkKafkaConsumer.setStartFromSpecificOffsets(new HashMap<KafkaTopicPartition, Long>() {{
                put(new KafkaTopicPartition(etlSourceTopic, 0), 4921649L);
                put(new KafkaTopicPartition(etlSourceTopic, 1), 5989022L);
                put(new KafkaTopicPartition(etlSourceTopic, 2), 5004190L);
            }});
        } else {
            flinkKafkaConsumer.setStartFromGroupOffsets();
            // 使用checkPoint的offset状态保持
            flinkKafkaConsumer.setCommitOffsetsOnCheckpoints(true);
        }

        //4 .设置数据源
        DataStream<String> kafkaDataStream = env.addSource(flinkKafkaConsumer)
                .uid(etlSourceTopic)
                .name(etlSourceTopic);

        // 5. 打印数据
        SingleOutputStreamOperator<JsonNode> jnCTSourceDS = kafkaDataStream
            .map(s -> mapper.readTree(s))
                .uid("str-to-jsonnode")
                .name("str-to-jsonnode");
//            .filter(jn -> {
//                JsonNode jsData = jn.path("data");
//                String binComment = jsData.path("BIN_Comment").asText();
//                return StringUtils.isNotBlank(binComment) && !StringUtils.equalsIgnoreCase(binComment, "cc") && !StringUtils.equals(binComment, "BIN100");
//            }).uid("filter-bin_comment")
//              .name("filter-bin_comment");
//        jnCTSourceDS.print();

//        jnCTSourceDS
//                .map(jn -> {
//                    JsonNode jnData = jn.path("data");
//                    // 若没有上传eqp_id，则默认为HALM00A设备
//                    final String eqpId = jnData.path("eqp_id").asText("Z2-HALM00A");
//                    return new Tuple2<>(eqpId, jnData);
//                }).returns(new TypeHint<Tuple2<String, JsonNode>>(){})
//                .name("jsonNode2Tuple-EqpId-and-jsonNode")
//                .uid("jsonNode2Tuple-EqpId-and-jsonNode")
//                .keyBy(0)
//                .countWindow(2, 1)
//                .reduce((t1, t2) -> {
////                    Tuple2<String, JsonNode> jnNull = null;
//                    Tuple2<String, JsonNode> jnNull = new Tuple2<>(null, null);
//                    final SimpleDateFormat defaultSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    final SimpleDateFormat utcSdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//                    final SimpleDateFormat dayDateSdf = new SimpleDateFormat("yyyy-MM-dd");
//                    final SimpleDateFormat hourSdf = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
//                    final SimpleDateFormat hourSdf2 = new SimpleDateFormat("yyyy-MM-dd HH:30:00");
//                    Integer batchId1 = t1.f1.path("BatchID").asInt();
//                    Integer batchId2 = t2.f1.path("BatchID").asInt();
//                    if ((batchId1 + 1) == batchId2) {
//                        JsonNode jn1 = t1.f1;
//                        JsonNode jn2 = t2.f1;
//                        String testTime = jn1.path("TestTime").asText();
//                        String testDate = jn1.path("TestDate").asText();
//                        String temp = String.format("%s %s", testDate, testTime);
//                        Date testDateDate = utcSdf.parse(temp);
//                        testTime = defaultSdf.format(testDateDate);
//                        testDate = dayDateSdf.format(testDateDate);
//
//                        String endTime = jn2.path("TestTime").asText();
//                        String endDate = jn2.path("TestDate").asText();
//                        temp = String.format("%s %s", endDate, endTime);
//                        Date endDateDate = utcSdf.parse(temp);
//                        endTime = defaultSdf.format(endDateDate);
//                        endDate = defaultSdf.format(endDateDate);
//                        ((ObjectNode)jn1).put("TestTime", testTime);
//                        ((ObjectNode)jn1).put("EndTime", endTime);
//                        // eqpid格式转换，之前是Z2-HALM01A -> Z2-Halm01A
//                        ((ObjectNode)jn1).put("Eqpid", t1.f0.replace("HALM", "Halm"));
//                        String nullStr = null;
//                        ((ObjectNode)jn1).put("Tmonicell_1", nullStr);
//                        ((ObjectNode)jn1).put("M2_Ssh", nullStr);
//                        ((ObjectNode)jn1).put("M2_SserLf", nullStr);
//                        ((ObjectNode)jn1).put("M2_SshuntLf", nullStr);
//                        ((ObjectNode)jn1).put("M2_Rsh", nullStr);
//                        ((ObjectNode)jn1).put("M2_RserLf", nullStr);
//                        ((ObjectNode)jn1).put("M2_RshuntLf", nullStr);
//                        if (jn1.path("AOI_1_Q").isMissingNode()) {
//                            ((ObjectNode)jn1).put("AOI_1_Q", nullStr);
//                        }
//                        if (jn1.path("AOI_1_R").isMissingNode()) {
//                            ((ObjectNode)jn1).put("AOI_1_R", nullStr);
//                        }
//                        if (jn1.path("AOI_2_Q").isMissingNode()) {
//                            ((ObjectNode)jn1).put("AOI_2_Q", nullStr);
//                        }
//                        if (jn1.path("AOI_2_R").isMissingNode()) {
//                            ((ObjectNode)jn1).put("AOI_2_R", nullStr);
//                        }
//                        return t1;
//                    } else if (batchId1.equals(batchId2)) {
//                        // 重复数据
//                        return jnNull;
//                    } else {
//                        // 乱序数据
//                        return jnNull;
//                    }
//                })
//                .uid("get-eqp-end-time")
//                .name("get-eqp-end-time")
//                .filter(e -> !Objects.isNull(e.f0))
//                .uid("delete-repeat-and-out-of-order-record")
//                .name("delete-repeat-and-out-of-order-record")
//                .map(e -> {
//                    return mapper.writeValueAsString(e.f1);
//                })
//                .uid("serializable-result2string")
//                .name("serializable-result2string")
//                .addSink(kafkaProducerSink)
//                .uid("save-result2kafka")
//                .name("save-result2kafka");

        SingleOutputStreamOperator<EqpCTSource>  eqpCTSourceDS = jnCTSourceDS.map(jn -> {
            //分区字段
            final SimpleDateFormat defaultSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            final SimpleDateFormat utcSdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            final SimpleDateFormat dayDateSdf = new SimpleDateFormat("yyyy-MM-dd");
            final SimpleDateFormat hourSdf = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
            final SimpleDateFormat hourSdf2 = new SimpleDateFormat("yyyy-MM-dd HH:30:00");

            EqpCTSource eqpCTSource = new EqpCTSource();
            JsonNode jnData = jn.path("data");
            // 若没有上传eqp_id，则默认为HALM00A设备
            final String eqpId = jnData.path("eqp_id").asText("Z2-HALM00A");
            final String site = eqpId.split("-")[0];
            eqpCTSource.site = site;
            // site转换成基地
            eqpCTSource.factory = SITE2FACTORY_MAP.getOrDefault(site, "0");
            eqpCTSource.eqpId = eqpId;

            String testDate = jnData.path("TestDate").asText("01-01-1970");
            String testTimeSource = jnData.path("TestTime").asText("00:00:00");
            String testTime = jnData.path("TestTime").asText("00:00:00");
            testTime = String.format("%s %s", testDate, testTime);
            Date testDateTime = utcSdf.parse(testTime);
            // 广东7:30-19:30为白班，浙江和添加8:00-20:00为白班，其余为晚班
            // 班次时间=testTime-8小时
            Calendar calendar = Calendar.getInstance();
            Date shiftDateTime = null;
            if (StringUtils.startsWith(site, "G")) {
                calendar.setTime(testDateTime);
                calendar.add(Calendar.HOUR_OF_DAY, -7);
                calendar.add(Calendar.MINUTE, -30);
                shiftDateTime = calendar.getTime();
            } else {
                calendar.setTime(testDateTime);
                calendar.add(Calendar.HOUR_OF_DAY, -8);
                shiftDateTime = calendar.getTime();
            }
            if (calendar.get(Calendar.HOUR_OF_DAY) < 12) {
                eqpCTSource.shift = String.format("%s-D", dayDateSdf.format(shiftDateTime));
            } else {
                eqpCTSource.shift = String.format("%s-N", dayDateSdf.format(shiftDateTime));
            }
            testTime = defaultSdf.format(testDateTime);
            eqpCTSource.createTime = testTime;
            eqpCTSource.dayDate = dayDateSdf.format(shiftDateTime);
            eqpCTSource.dayHour = hourSdf.format(testDateTime);
            // 计算两小时的数据
            Calendar testTimeCalendar = Calendar.getInstance();
            testTimeCalendar.setTime(testDateTime);
            if (testTimeCalendar.get(Calendar.HOUR_OF_DAY) % 2 == 1) {
                testTimeCalendar.add(Calendar.HOUR_OF_DAY, -1);
            }
            eqpCTSource.twoHour = hourSdf.format(testTimeCalendar.getTime());

            // 恢复时间
            testTimeCalendar.setTime(testDateTime);
            if (testTimeCalendar.get(Calendar.MINUTE) >= 30) {
                eqpCTSource.halfHour = hourSdf2.format(testDateTime);
            } else {
                eqpCTSource.halfHour = hourSdf.format(testDateTime);
            }
            String comment = jnData.path("Comment").asText("");
            eqpCTSource.comments = comment;
            for (Map.Entry<String, String> entry : ORDER_TYPE_MAP.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (StringUtils.containsIgnoreCase(comment, key)) {
                    eqpCTSource.orderType = value;
                    break;
                }
            }
            Integer bin = jnData.path("BIN").asInt(-1);
            List<Integer> abnormalBinList = ImmutableList.of(0, 100);
            if (abnormalBinList.stream().anyMatch(bin::equals)) {
                eqpCTSource.binType = String.valueOf(bin);
            }
            if (!ImmutableList.of(1, 2, 107).stream().anyMatch(bin::equals)) {
                DecimalFormat decimalFormat = new DecimalFormat("0.000000");
                eqpCTSource.outputQty2 = "1";
                eqpCTSource.uoc = decimalFormat.format(jnData.path("Uoc").asDouble(0D));
                eqpCTSource.isc = decimalFormat.format(jnData.path("Isc").asDouble(0D));
                eqpCTSource.ff = decimalFormat.format(jnData.path("FF").asDouble(0D));
                eqpCTSource.eta = decimalFormat.format(jnData.path("Eta").asDouble(0D));
                eqpCTSource.m3Eta = decimalFormat.format(jnData.path("M3_Eta").asDouble(0D));
                eqpCTSource.irev2 = decimalFormat.format(jnData.path("IRev2").asDouble(0D));
                eqpCTSource.rser = decimalFormat.format(jnData.path("Rser").asDouble(0D));
                eqpCTSource.rshunt = decimalFormat.format(jnData.path("Rshunt").asDouble(0D));
                eqpCTSource.tcell = decimalFormat.format(jnData.path("Tcell").asDouble(0D));
                eqpCTSource.tmonicell = decimalFormat.format(jnData.path("Tmonicell").asDouble(0D));
                eqpCTSource.insolM1 = decimalFormat.format(jnData.path("Insol_M1").asDouble(0D));
                eqpCTSource.m3Insol = decimalFormat.format(jnData.path("M3_Insol").asDouble(0D));
            }

            String binComment = jnData.path("BIN_Comment").asText();
            if (StringUtils.equalsIgnoreCase(binComment, "null")) {
                eqpCTSource.numA = "1";
            } else if (StringUtils.equalsIgnoreCase(binComment, "zhengmianyichang")) {
                eqpCTSource.numZhengmianyichang = "1";
            } else if (StringUtils.equalsIgnoreCase(binComment, "beimianyichang")) {
                eqpCTSource.numBeimianyichang = "1";
            } else if (StringUtils.containsIgnoreCase(binComment, "yanseyichang") || StringUtils.containsIgnoreCase(binComment, "yanseyichang_1")) {
                eqpCTSource.numYanseyichang = "1";
            } else if (StringUtils.equalsIgnoreCase(binComment, "yanseyichang_2")) {
                eqpCTSource.numYanseyichang2 = "1";
            } else if (StringUtils.equalsIgnoreCase(binComment, "MO5")) {
                eqpCTSource.numMo5 = "1";
            } else if (StringUtils.equalsIgnoreCase(binComment, "IREV2")) {
                eqpCTSource.numIrev2 = "1";
            } else if (StringUtils.equalsIgnoreCase(binComment, "RSH")) {
                eqpCTSource.numRsh = "1";
            } else if (StringUtils.equalsIgnoreCase(binComment, "DX")) {
                eqpCTSource.numDx = "1";
            } else if (StringUtils.equalsIgnoreCase(binComment, "duanshan")) {
                eqpCTSource.numDuanshan = "1";
            } else if (StringUtils.equalsIgnoreCase(binComment, "huashang")) {
                eqpCTSource.numHuashang = "1";
            } else if (StringUtils.equalsIgnoreCase(binComment, "heidian")) {
                eqpCTSource.numHeidian = "1";
            } else if (StringUtils.equalsIgnoreCase(binComment, "kexindu")) {
                eqpCTSource.numKexindu = "1";
            } else if (StringUtils.equalsIgnoreCase(binComment, "yinlie")) {
                eqpCTSource.numYinlie = "1";
            }

            Integer aio2R = jnData.path("AOI_2_R").asInt(0);
            switch (aio2R) {
                case 1:
                    eqpCTSource.numColorC = "1";
                    eqpCTSource.numColorAll = "1";
                    break;
                case 2:
                    eqpCTSource.numColorB = "1";
                    eqpCTSource.numColorAll = "1";
                    break;
                case 3:
                    eqpCTSource.numColorA = "1";
                    eqpCTSource.numColorAll = "1";
                    break;
                default:
                    break;
            }
            Integer el2FingerDefaultCount = jnData.path("EL2FingerDefaultCount").asInt(-1);
            if (el2FingerDefaultCount >= 0) {
                eqpCTSource.numDsAll = "1";
                if (el2FingerDefaultCount == 0) {
                    eqpCTSource.numDs_0 = "1";
                } else if (el2FingerDefaultCount == 1) {
                    eqpCTSource.numDs_1 = "1";
                } else if (el2FingerDefaultCount == 2) {
                    eqpCTSource.numDs_2 = "1";
                } else if (el2FingerDefaultCount == 3) {
                    eqpCTSource.numDs_3 = "1";
                } else if (el2FingerDefaultCount == 4) {
                    eqpCTSource.numDs_4 = "1";
                } else if (el2FingerDefaultCount == 5) {
                    eqpCTSource.numDs_5 = "1";
                } else {
                    eqpCTSource.numDs_5p = "1";
                }
            }
            Double eta = jnData.path("Eta").asDouble(-1D);
            Map<String, Range<Double>> etaGradeMap = ETA_GRADE_MAP;
            // 若测试时间在2020-08-06 08:00:00之后，执行08-06测试分档标准
            if ("2020-08-06 08:00:00".compareTo(eqpCTSource.createTime) <= 0) {
                etaGradeMap = ETA_GRADE_08_06_MAP;
            }
            for (Map.Entry<String, Range<Double>> entry : etaGradeMap.entrySet()) {
                String grade = entry.getKey();
                Range value = entry.getValue();
                if (value.contains(eta)) {
                    Method m = EQP_CT_SOURCE_METHOD.get(String.format("set%s", StringUtils.capitalize(grade)));
                    m.invoke(eqpCTSource, "1");
                }
            }
            eqpCTSource.rowkey = String.format("%s|%s|%s", DigestUtils.md5Hex(String.format("%s|%s", eqpId, testTime)).substring(0, 2), eqpId, testTime);
            return eqpCTSource;
        })
        .name("ct-map-string-to-eqp-ct-source")
        .uid("ct-map-string-to-eqp-ct-source");

        OutputTag<EqpCTSource> outputTag = new OutputTag<>("late-data", TypeInformation.of(EqpCTSource.class));

//        eqpCTSourceDS.print();

        SingleOutputStreamOperator<EqpCTSource> resultSos = eqpCTSourceDS
                .assignTimestampsAndWatermarks(new CustomerAssignerWithPunctuatedWatermarks())
//                .assignTimestampsAndWatermarks(new TimeLagWatermarkGenerator())
                .keyBy("eqpId", "comments", "orderType", "binType")
                .timeWindow(Time.minutes(30))
                .sideOutputLateData(outputTag)
                .reduce(new ReduceFunction<EqpCTSource>() {
                    @Override
                    public EqpCTSource reduce(EqpCTSource v1, EqpCTSource v2) throws Exception {
                        DecimalFormat decimalFormat = new DecimalFormat("0.000000");
                        v1.outputQty = String.valueOf(Integer.parseInt(v1.outputQty) + Integer.parseInt(v2.outputQty));
                        v1.outputQty2 = String.valueOf(Integer.parseInt(v1.outputQty2) + Integer.parseInt(v2.outputQty2));
                        v1.numA = String.valueOf(Integer.parseInt(v1.numA) + Integer.parseInt(v2.numA));
                        v1.numZhengmianyichang = String.valueOf(Integer.parseInt(v1.numZhengmianyichang) + Integer.parseInt(v2.numZhengmianyichang));
                        v1.numBeimianyichang = String.valueOf(Integer.parseInt(v1.numBeimianyichang) + Integer.parseInt(v2.numBeimianyichang));
                        v1.numYanseyichang = String.valueOf(Integer.parseInt(v1.numYanseyichang) + Integer.parseInt(v2.numYanseyichang));
                        v1.numYanseyichang2 = String.valueOf(Integer.parseInt(v1.numYanseyichang2) + Integer.parseInt(v2.numYanseyichang2));
                        v1.numMo5 = String.valueOf(Integer.parseInt(v1.numMo5) + Integer.parseInt(v2.numMo5));
                        v1.numIrev2 = String.valueOf(Integer.parseInt(v1.numIrev2) + Integer.parseInt(v2.numIrev2));
                        v1.numRsh = String.valueOf(Integer.parseInt(v1.numRsh) + Integer.parseInt(v2.numRsh));
                        v1.numDx = String.valueOf(Integer.parseInt(v1.numDx) + Integer.parseInt(v2.numDx));
                        v1.numDuanshan = String.valueOf(Integer.parseInt(v1.numDuanshan) + Integer.parseInt(v2.numDuanshan));
                        v1.numHuashang = String.valueOf(Integer.parseInt(v1.numHuashang) + Integer.parseInt(v2.numHuashang));
                        v1.numHeidian = String.valueOf(Integer.parseInt(v1.numHeidian) + Integer.parseInt(v2.numHeidian));
                        v1.numKexindu = String.valueOf(Integer.parseInt(v1.numKexindu) + Integer.parseInt(v2.numKexindu));
                        v1.numYinlie = String.valueOf(Integer.parseInt(v1.numYinlie) + Integer.parseInt(v2.numYinlie));
                        v1.numColorAll = String.valueOf(Integer.parseInt(v1.numColorAll) + Integer.parseInt(v2.numColorAll));
                        v1.numColorA = String.valueOf(Integer.parseInt(v1.numColorA) + Integer.parseInt(v2.numColorA));
                        v1.numColorB = String.valueOf(Integer.parseInt(v1.numColorB) + Integer.parseInt(v2.numColorB));
                        v1.numColorC = String.valueOf(Integer.parseInt(v1.numColorC) + Integer.parseInt(v2.numColorC));
                        v1.numDsAll = String.valueOf(Integer.parseInt(v1.numDsAll) + Integer.parseInt(v2.numDsAll));
                        v1.numDs_0 = String.valueOf(Integer.parseInt(v1.numDs_0) + Integer.parseInt(v2.numDs_0));
                        v1.numDs_1 = String.valueOf(Integer.parseInt(v1.numDs_1) + Integer.parseInt(v2.numDs_1));
                        v1.numDs_2 = String.valueOf(Integer.parseInt(v1.numDs_2) + Integer.parseInt(v2.numDs_2));
                        v1.numDs_3 = String.valueOf(Integer.parseInt(v1.numDs_3) + Integer.parseInt(v2.numDs_3));
                        v1.numDs_4 = String.valueOf(Integer.parseInt(v1.numDs_4) + Integer.parseInt(v2.numDs_4));
                        v1.numDs_5 = String.valueOf(Integer.parseInt(v1.numDs_5) + Integer.parseInt(v2.numDs_5));
                        v1.numDs_5p = String.valueOf(Integer.parseInt(v1.numDs_5p) + Integer.parseInt(v2.numDs_5p));
                        v1.num213 = String.valueOf(Integer.parseInt(v1.num213) + Integer.parseInt(v2.num213));
                        v1.num214 = String.valueOf(Integer.parseInt(v1.num214) + Integer.parseInt(v2.num214));
                        v1.num215 = String.valueOf(Integer.parseInt(v1.num215) + Integer.parseInt(v2.num215));
                        v1.num216 = String.valueOf(Integer.parseInt(v1.num216) + Integer.parseInt(v2.num216));
                        v1.num217 = String.valueOf(Integer.parseInt(v1.num217) + Integer.parseInt(v2.num217));
                        v1.num218 = String.valueOf(Integer.parseInt(v1.num218) + Integer.parseInt(v2.num218));
                        v1.num219 = String.valueOf(Integer.parseInt(v1.num219) + Integer.parseInt(v2.num219));
                        v1.num220 = String.valueOf(Integer.parseInt(v1.num220) + Integer.parseInt(v2.num220));
                        v1.num221 = String.valueOf(Integer.parseInt(v1.num221) + Integer.parseInt(v2.num221));
                        v1.num222 = String.valueOf(Integer.parseInt(v1.num222) + Integer.parseInt(v2.num222));
                        v1.num223 = String.valueOf(Integer.parseInt(v1.num223) + Integer.parseInt(v2.num223));
                        v1.num224 = String.valueOf(Integer.parseInt(v1.num224) + Integer.parseInt(v2.num224));
                        v1.num225 = String.valueOf(Integer.parseInt(v1.num225) + Integer.parseInt(v2.num225));
                        v1.num226 = String.valueOf(Integer.parseInt(v1.num226) + Integer.parseInt(v2.num226));
                        v1.num227 = String.valueOf(Integer.parseInt(v1.num227) + Integer.parseInt(v2.num227));
                        v1.num228 = String.valueOf(Integer.parseInt(v1.num228) + Integer.parseInt(v2.num228));
                        v1.num229 = String.valueOf(Integer.parseInt(v1.num229) + Integer.parseInt(v2.num229));
                        v1.num230 = String.valueOf(Integer.parseInt(v1.num230) + Integer.parseInt(v2.num230));
                        v1.num231 = String.valueOf(Integer.parseInt(v1.num231) + Integer.parseInt(v2.num231));
                        v1.num232 = String.valueOf(Integer.parseInt(v1.num232) + Integer.parseInt(v2.num232));
                        v1.num233 = String.valueOf(Integer.parseInt(v1.num233) + Integer.parseInt(v2.num233));
                        v1.num234 = String.valueOf(Integer.parseInt(v1.num234) + Integer.parseInt(v2.num234));
                        v1.num235 = String.valueOf(Integer.parseInt(v1.num235) + Integer.parseInt(v2.num235));
                        v1.num236 = String.valueOf(Integer.parseInt(v1.num236) + Integer.parseInt(v2.num236));
                        v1.num237 = String.valueOf(Integer.parseInt(v1.num237) + Integer.parseInt(v2.num237));
                        v1.num238 = String.valueOf(Integer.parseInt(v1.num238) + Integer.parseInt(v2.num238));
                        v1.num239 = String.valueOf(Integer.parseInt(v1.num239) + Integer.parseInt(v2.num239));
                        v1.num240 = String.valueOf(Integer.parseInt(v1.num240) + Integer.parseInt(v2.num240));
                        v1.uoc = decimalFormat.format(Double.parseDouble(v1.uoc) + Double.parseDouble(v2.uoc));
                        v1.isc = decimalFormat.format(Double.parseDouble(v1.isc) + Double.parseDouble(v2.isc));
                        v1.ff = decimalFormat.format(Double.parseDouble(v1.ff) + Double.parseDouble(v2.ff));
                        v1.eta = decimalFormat.format(Double.parseDouble(v1.eta) + Double.parseDouble(v2.eta));
                        v1.m3Eta = decimalFormat.format(Double.parseDouble(v1.m3Eta) + Double.parseDouble(v2.m3Eta));
                        v1.irev2 = decimalFormat.format(Double.parseDouble(v1.irev2) + Double.parseDouble(v2.irev2));
                        v1.rser = decimalFormat.format(Double.parseDouble(v1.rser) + Double.parseDouble(v2.rser));
                        v1.rshunt = decimalFormat.format(Double.parseDouble(v1.rshunt) + Double.parseDouble(v2.rshunt));
                        v1.tcell = decimalFormat.format(Double.parseDouble(v1.tcell) + Double.parseDouble(v2.tcell));
                        v1.tmonicell = decimalFormat.format(Double.parseDouble(v1.tmonicell) + Double.parseDouble(v2.tmonicell));
                        v1.insolM1 = decimalFormat.format(Double.parseDouble(v1.insolM1) + Double.parseDouble(v2.insolM1));
                        v1.m3Insol = decimalFormat.format(Double.parseDouble(v1.m3Insol) + Double.parseDouble(v2.m3Insol));

                        return v1;
                    }
                })
                .name("eqp-ct-reduce-oee-halm-sum")
                .uid("eqp-ct-reduce-oee-halm-sum");

//        resultSos.print();

        DataStream<EqpCTSource> sideOutput = resultSos.getSideOutput(outputTag);
//        sideOutput.filter(e -> {
//            String site = e.site;
//            // 只保留当班的数据
//            final Date now = new Date();
//            final SimpleDateFormat dayDateSdf = new SimpleDateFormat("yyyy-MM-dd");
//            // 广东7:30-19:30为白班，浙江和添加8:00-20:00为白班，其余为晚班
//            // 班次时间=testTime-8小时
//            final Calendar calendar = Calendar.getInstance();
//            Date shiftDateTime = null;
//            if (StringUtils.startsWith(site, "G")) {
//                calendar.setTime(now);
//                calendar.add(Calendar.HOUR_OF_DAY, -7);
//                calendar.add(Calendar.MINUTE, -30);
//                shiftDateTime = calendar.getTime();
//            } else {
//                calendar.setTime(now);
//                calendar.add(Calendar.HOUR_OF_DAY, -8);
//                shiftDateTime = calendar.getTime();
//            }
//
//            String shift;
//            if (calendar.get(Calendar.HOUR_OF_DAY) < 12) {
//                shift = String.format("%s-D", dayDateSdf.format(shiftDateTime));
//            } else {
//                shift = String.format("%s-N", dayDateSdf.format(shiftDateTime));
//            }
//            return StringUtils.equals(shift, e.shift);
//        }).keyBy("");

        sideOutput.print();

        SingleOutputStreamOperator<Map<String, Object>> hBaseStream = resultSos.map(new RichMapFunction<EqpCTSource, Map<String, Object>>() {
            @Override
            public Map<String, Object> map(EqpCTSource v) throws Exception {
                byte[] jsonBytes = mapper.writeValueAsBytes(v);
                TypeReference ref = new TypeReference<HashMap<String, Object>>() {};
                return mapper.readValue(jsonBytes, ref);
            }
        }).name("EqpCTSource-to-map")
            .uid("EqpCTSource-to-map");

//        hBaseStream.print();

        hBaseStream.addSink(new CTSinkHBase(onLineHbaseTable))
            .name("ct-online-save-to-hbase")
            .uid("ct-online-save-to-hbase");

        logger.info("job start...");
        // 6.执行任务
        env.execute(parameterTool.get("job.name"));
    }
}
