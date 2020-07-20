package com.aikosolar.bigdata.ct;

import com.aikosolar.bigdata.ct.sink.CTSinkHBase;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.*;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
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
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
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

    public static final Map<String, Method> EQP_CT_SOURCE_METHOD = new HashMap<>();
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
        mapper.setSerializationInclusion(Include.NON_NULL);
        //忽略值为默认值的属性
        mapper.setDefaultPropertyInclusion(Include.NON_DEFAULT);
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
                .setParallelism(3);

        // 5. 打印数据
        SingleOutputStreamOperator<EqpCTSource> eqpCTSourceDS = kafkaDataStream
            .map(s -> mapper.readTree(s))
                .uid("str-to-jsonnode")
                .name("str-to-jsonnode")
            .filter(jn -> {
                JsonNode jsData = jn.get("data");
                String binComment = jsData.get("BIN_Comment").asText();
                return StringUtils.isNotBlank(binComment) && !StringUtils.equalsIgnoreCase(binComment, "cc") && !StringUtils.equals(binComment, "BIN100");
            }).uid("filter-bin_comment")
              .name("filter-bin_comment")
            .map(jn -> {
            //分区字段
            final SimpleDateFormat defaultSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            final SimpleDateFormat utcSdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            final SimpleDateFormat dayDateSdf = new SimpleDateFormat("yyyy-MM-dd");
            final SimpleDateFormat hourSdf = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
            final SimpleDateFormat hourSdf2 = new SimpleDateFormat("yyyy-MM-dd HH:30:00");

            EqpCTSource eqpCTSource = new EqpCTSource();
            JsonNode jnData = jn.get("data");
            // 若没有上传eqp_id，则默认为HALM00A设备
            final String eqpId = jnData.path("eqp_id").asText("Z2-HALM00A");
            final String site = eqpId.split("-")[0];
            eqpCTSource.site = site;

            String testDate = jnData.get("TestDate").asText("01-01-1970");
            String testTimeSource = jnData.get("TestTime").asText("00:00:00");
            String testTime = jnData.get("TestTime").asText("00:00:00");
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
            eqpCTSource.dayDate = dayDateSdf.format(testDateTime);
            eqpCTSource.dayHour = hourSdf.format(testDateTime);
            // 计算两小时的数据
            Calendar testTimeCalendar = Calendar.getInstance();
            testTimeCalendar.setTime(testDateTime);
            if (testTimeCalendar.get(Calendar.HOUR_OF_DAY) % 2 == 1) {
                testTimeCalendar.add(Calendar.HOUR_OF_DAY, -1);
            }
            eqpCTSource.twoHour = defaultSdf.format(testTimeCalendar.getTime());

            // 恢复时间
            testTimeCalendar.setTime(testDateTime);
            if (testTimeCalendar.get(Calendar.MINUTE) > 30) {
                eqpCTSource.halfHour = hourSdf2.format(testDateTime);
            } else {
                eqpCTSource.halfHour = hourSdf.format(testDateTime);
            }
            String comment = jnData.get("Comment").asText("");
            eqpCTSource.comments = comment;
            for (Map.Entry<String, String> entry : ORDER_TYPE_MAP.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (StringUtils.containsIgnoreCase(comment, key)) {
                    eqpCTSource.orderType = value;
                    break;
                }
            }
            String bin = jnData.get("BIN").asText("");
            List<Integer> abnormalBinList = ImmutableList.of(0, 100);
            if (abnormalBinList.stream().anyMatch(bin::equals)) {
                eqpCTSource.binType = bin;
            }
            if (!ImmutableList.of(1, 2, 107).stream().anyMatch(bin::equals)) {
                eqpCTSource.output2 = 1;
                eqpCTSource.uoc = jnData.get("UOC").asDouble(0D);
                eqpCTSource.isc = jnData.get("ISC").asDouble(0D);
                eqpCTSource.ff = jnData.get("FF").asDouble(0D);
                eqpCTSource.eta = jnData.get("ETA").asDouble(0D);
                eqpCTSource.m3Eta = jnData.get("M3_Eta").asDouble(0D);
                eqpCTSource.irev2 = jnData.get("IRev2").asDouble(0D);
                eqpCTSource.rser = jnData.get("Rser").asDouble(0D);
                eqpCTSource.rshunt = jnData.get("Rshunt").asDouble(0D);
                eqpCTSource.tcell = jnData.get("Tcell").asDouble(0D);
                eqpCTSource.tmonicell = jnData.get("Tmonicell").asDouble(0D);
                eqpCTSource.insolM1 = jnData.get("Insol_M1").asDouble(0D);
                eqpCTSource.m3Insol = jnData.get("M3_Insol").asDouble(0D);
            }

            String binComment = jnData.get("BIN_COMMENT").asText();
            if (StringUtils.isBlank(binComment)) {
                eqpCTSource.numA = 1;
            } else if (StringUtils.equalsIgnoreCase(binComment, "zhengmianyichang")) {
                eqpCTSource.numZhengmianyichang = 1;
            } else if (StringUtils.equalsIgnoreCase(binComment, "beimianyichang")) {
                eqpCTSource.numBeimianyichang = 1;
            } else if (StringUtils.containsIgnoreCase(binComment, "yanseyichang") || StringUtils.containsIgnoreCase(binComment, "yanseyichang_1")) {
                eqpCTSource.numYanseyichang = 1;
            } else if (StringUtils.equalsIgnoreCase(binComment, "yanseyichang_2")) {
                eqpCTSource.numYanseyichang2 = 1;
            } else if (StringUtils.equalsIgnoreCase(binComment, "MO5")) {
                eqpCTSource.numMo5 = 1;
            } else if (StringUtils.equalsIgnoreCase(binComment, "IREV2")) {
                eqpCTSource.numIrev2 = 1;
            } else if (StringUtils.equalsIgnoreCase(binComment, "RSH")) {
                eqpCTSource.numRsh = 1;
            } else if (StringUtils.equalsIgnoreCase(binComment, "DX")) {
                eqpCTSource.numDx = 1;
            } else if (StringUtils.equalsIgnoreCase(binComment, "duanshan")) {
                eqpCTSource.numDuanshan = 1;
            } else if (StringUtils.equalsIgnoreCase(binComment, "huashang")) {
                eqpCTSource.numHuashang = 1;
            } else if (StringUtils.equalsIgnoreCase(binComment, "heidian")) {
                eqpCTSource.numHeidian = 1;
            } else if (StringUtils.equalsIgnoreCase(binComment, "kexindu")) {
                eqpCTSource.numKexindu = 1;
            } else if (StringUtils.equalsIgnoreCase(binComment, "yinlie")) {
                eqpCTSource.numYinlie = 1;
            }

            Integer aio2R = jnData.get("AOI_2_R").asInt(0);
            switch (aio2R) {
                case 1:
                    eqpCTSource.numColorC = 1;
                    eqpCTSource.numColorAll = 1;
                    break;
                case 2:
                    eqpCTSource.numColorB = 1;
                    eqpCTSource.numColorAll = 1;
                    break;
                case 3:
                    eqpCTSource.numColorA = 1;
                    eqpCTSource.numColorAll = 1;
                    break;
                default:
                    break;
            }
            Integer el2FingerDefaultCount = jnData.get("EL2FINGERDEFAULTCOUNT").asInt(-1);
            if (el2FingerDefaultCount >= 0) {
                eqpCTSource.numDsAll = 1;
                if (el2FingerDefaultCount == 0) {
                    eqpCTSource.numDs0 = 1;
                } else if (el2FingerDefaultCount == 1) {
                    eqpCTSource.numDs1 = 1;
                } else if (el2FingerDefaultCount == 2) {
                    eqpCTSource.numDs2 = 1;
                } else if (el2FingerDefaultCount == 3) {
                    eqpCTSource.numDs3 = 1;
                } else if (el2FingerDefaultCount == 4) {
                    eqpCTSource.numDs4 = 1;
                } else if (el2FingerDefaultCount == 5) {
                    eqpCTSource.numDs5 = 1;
                } else {
                    eqpCTSource.numDs5p = 1;
                }
            }
            Double eta = jnData.get("ETA").asDouble(-1D);
            for (Map.Entry<String, Range<Double>> entry : ETA_GRADE_MAP.entrySet()) {
                String grade = entry.getKey();
                Range value = entry.getValue();
                if (value.contains(eta)) {
                    Method m = EQP_CT_SOURCE_METHOD.get(String.format("set%s", StringUtils.capitalize(grade)));
                    m.invoke(eqpCTSource, Integer.valueOf(1));
                }
            }

            eqpCTSource.rowKey = String.format("%s|%s|%s", testTimeSource, eqpId, testDateTime);
            return eqpCTSource;
        })
        .name("ct-map-string-to-eqp-ct-source")
        .uid("ct-map-string-to-eqp-ct-source");

        SingleOutputStreamOperator<EqpCTSource> resultSos = eqpCTSourceDS
                .keyBy("site",
                        "shift",
                        "eqpId",
                        "dayDate",
                        "dayHour",
                        "twoHour",
                        "halfHour",
                        "binType",
                        "orderType",
                        "comments")
                .timeWindow(Time.minutes(30))
                .reduce(new ReduceFunction<EqpCTSource>() {
                    @Override
                    public EqpCTSource reduce(EqpCTSource v1, EqpCTSource v2) throws Exception {
                        v1.output += v2.output;
                        v1.output2 += v2.output2;
                        v1.uoc += v2.uoc;
                        v1.isc += v2.isc;
                        v1.ff += v2.ff;
                        v1.eta += v2.eta;
                        v1.m3Eta += v2.m3Eta;
                        v1.irev2 += v2.irev2;
                        v1.rser += v2.rser;
                        v1.rshunt += v2.rshunt;
                        v1.tcell += v2.tcell;
                        v1.tmonicell += v2.tmonicell;
                        v1.insolM1 += v2.insolM1;
                        v1.m3Insol += v2.m3Insol;
                        v1.numA += v2.numA;
                        v1.numZhengmianyichang += v2.numZhengmianyichang;
                        v1.numBeimianyichang += v2.numBeimianyichang;
                        v1.numYanseyichang += v2.numYanseyichang;
                        v1.numYanseyichang2 += v2.numYanseyichang2;
                        v1.numMo5 += v2.numMo5;
                        v1.numIrev2 += v2.numIrev2;
                        v1.numRsh += v2.numRsh;
                        v1.numDx += v2.numDx;
                        v1.numDuanshan += v2.numDuanshan;
                        v1.numHuashang += v2.numHuashang;
                        v1.numHeidian += v2.numHeidian;
                        v1.numKexindu += v2.numKexindu;
                        v1.numYinlie += v2.numYinlie;
                        v1.numColorAll += v2.numColorAll;
                        v1.numColorA += v2.numColorA;
                        v1.numColorB += v2.numColorB;
                        v1.numColorC += v2.numColorC;
                        v1.numDsAll += v2.numDsAll;
                        v1.numDs0 += v2.numDs0;
                        v1.numDs1 += v2.numDs1;
                        v1.numDs2 += v2.numDs2;
                        v1.numDs3 += v2.numDs3;
                        v1.numDs4 += v2.numDs4;
                        v1.numDs5 += v2.numDs5;
                        v1.numDs5p += v2.numDs5p;
                        v1.num213 += v2.num213;
                        v1.num214 += v2.num214;
                        v1.num215 += v2.num215;
                        v1.num216 += v2.num216;
                        v1.num217 += v2.num217;
                        v1.num218 += v2.num218;
                        v1.num219 += v2.num219;
                        v1.num220 += v2.num220;
                        v1.num221 += v2.num221;
                        v1.num222 += v2.num222;
                        v1.num223 += v2.num223;
                        v1.num224 += v2.num224;
                        v1.num225 += v2.num225;
                        v1.num226 += v2.num226;
                        v1.num227 += v2.num227;
                        v1.num228 += v2.num228;
                        v1.num229 += v2.num229;
                        v1.num230 += v2.num230;
                        v1.num231 += v2.num231;
                        v1.num232 += v2.num232;
                        v1.num233 += v2.num233;
                        v1.num234 += v2.num234;
                        v1.num235 += v2.num235;
                        v1.num236 += v2.num236;
                        v1.num237 += v2.num237;
                        v1.num238 += v2.num238;
                        v1.num239 += v2.num239;
                        v1.num240 += v2.num240;
                        return v1;
                    }
                })
                .name("eqp-ct-reduce-oee-halm-sum")
                .uid("eqp-ct-reduce-oee-halm-sum");

        resultSos.print();

        resultSos.map(new RichMapFunction<EqpCTSource, Map<String, Object>>() {
            @Override
            public Map<String, Object> map(EqpCTSource v) throws Exception {
                byte[] jsonBytes = mapper.writeValueAsBytes(v);
                TypeReference ref = new TypeReference<HashMap<String, Object>>() {};
                return mapper.readValue(jsonBytes, ref);
            }
        }).name("EqpCTSource-to-map")
            .uid("EqpCTSource-to-map")
            .addSink(new CTSinkHBase(onLineHbaseTable))
            .name("ct-online-save-to-hbase")
            .uid("ct-online-save-to-hbase");

        logger.info("job start...");
        // 6.执行任务
        env.execute(parameterTool.get("job.name"));
    }
}
