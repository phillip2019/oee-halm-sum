package com.aikosolar.bigdata.ct;


import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import javax.annotation.Nullable;

/**
 * @author xiaowei.song
 */
public class TimeLagWatermarkGenerator implements AssignerWithPeriodicWatermarks<EqpCTSource> {

    /**
     * 所有管式设备没有超过1小时的
     * 水位设置成3s, 3s之后开始计算窗口计算，之前的抛弃
     **/
    public static final Long MAX_TIME_LAG = 3000L;

    @Nullable
    @Override
    public Watermark getCurrentWatermark() {
        return new Watermark(System.currentTimeMillis() - MAX_TIME_LAG);
    }

    @Override
    public long extractTimestamp(EqpCTSource e, long l) {
        return e.timeSecond * 1000;
    }
}
