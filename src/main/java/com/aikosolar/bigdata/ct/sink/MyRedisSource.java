package com.aikosolar.bigdata.ct.sink;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaowei.song
 * @date 2020/06/30 13:06
 */
public class MyRedisSource implements SourceFunction<HashMap<String, String>> {
    private Logger logger = LoggerFactory.getLogger(MyRedisSource.class);
    /**
     * 周期睡眠 60s
     * */
    private final static long SLEEP_MILLION = 60000;
    private boolean isRunning = true;
    private Jedis jedis = null;

    public void run(SourceFunction.SourceContext<HashMap<String, String>> ctx) throws Exception {
        this.jedis = new Jedis(" redis01", 6379);
        //存储 所有 国家 和 大区 的 对应 关系
        HashMap<String, String> keyValueMap = new HashMap<>();
        while (isRunning) {
            try {
                keyValueMap.clear();
                Map<String, String> areas = jedis.hgetAll(" areas");
                for (Map.Entry<String, String> entry : areas.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    String[] splits = value.split(",");
                    for (String split : splits) {
                        keyValueMap.put(split, key);
                    }
                }
                if (keyValueMap.size() > 0) {
                    ctx.collect(keyValueMap);
                } else {
                    logger.warn("从Redis中获取的数据为空！！！");
                }
                Thread.sleep(SLEEP_MILLION);
            } catch (JedisConnectionException e) {
                logger.error(" Redis链接异常，重新获取链接", e.getCause());
                jedis = new Jedis(" redis01", 6379);
            } catch (Exception e) {
                logger.error(" Source数据源异常", e.getCause());
            }
        }
    }

    public void cancel() {
        isRunning = false;
        if (jedis != null) {
            jedis.close();
        }
    }
}