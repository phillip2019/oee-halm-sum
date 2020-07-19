package com.aikosolar.bigdata.ct;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.streaming.util.serialization.KeyedSerializationSchema;

/**
 * @author xiaowei.song
 * @date 2020-06-21 21:57
 */
public class DFTubeSchema implements KeyedSerializationSchema<EqpCTSource> {

    @Override
    public byte[] serializeKey(EqpCTSource tube) {
        return tube.id.getBytes();
    }

    @Override
    public byte[] serializeValue(EqpCTSource tube) {
        return JSONObject.toJSONBytes(tube);
    }

    @Override
    public String getTargetTopic(EqpCTSource tube) {
        return null;
    }
}
