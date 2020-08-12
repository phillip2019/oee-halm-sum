package com.aikosolar.bigdata.ct;


import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import javax.annotation.Nullable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author xiaowei.song
 */
public class TimeLagWatermarkGenerator implements AssignerWithPeriodicWatermarks<EqpCTLog> {

    /**
     * 水位设置成,30min之后开始计算窗口计算
     **/
    public static final Long MAX_TIME_LAG = 30 * 60 * 1000L;;

    public static Long currentMaxTimestamp = 0L;

    @Nullable
    @Override
    public Watermark getCurrentWatermark() {
        return new Watermark(currentMaxTimestamp - MAX_TIME_LAG);
    }

    @Override
    public long extractTimestamp(EqpCTLog e, long l) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            long timestamp = sdf.parse(e.createTime).getTime();
            currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
            return timestamp;
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return 0L;
    }
}
