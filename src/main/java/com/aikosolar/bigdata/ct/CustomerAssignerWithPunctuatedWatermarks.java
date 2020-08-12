package com.aikosolar.bigdata.ct;


import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import javax.annotation.Nullable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author xiaowei.song
 */
public class CustomerAssignerWithPunctuatedWatermarks implements AssignerWithPunctuatedWatermarks<EqpCTSource> {
    public Long currentMaxTimestamp = 0L;
    // 5分钟延迟
    public static final Long maxOutOfOrderness = 5 * 60 * 1000L;

    @Nullable
    @Override
    public Watermark checkAndGetNextWatermark(EqpCTSource lastElement, long extractedTimestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long threadId = Thread.currentThread().getId();
        String threadName = Thread.currentThread().getName();
//        System.out.println(String.format("currentThreadId: %s, name: %s ; eqpId: %s,testTime: %s, currentMaxTimestamp: %s, watermark: %s",
//                threadId, threadName, lastElement.getEqpId(), lastElement.getCreateTime(), sdf.format(new Date(currentMaxTimestamp)), sdf.format(new Date((currentMaxTimestamp - maxOutOfOrderness)))));
        // 历史数据不补，直接抛弃
        return new Watermark(currentMaxTimestamp - maxOutOfOrderness);
    }

    @Override
    public long extractTimestamp(EqpCTSource element, long previousElementTimestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            long timestamp = sdf.parse(element.createTime).getTime();
            currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
            return timestamp;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return previousElementTimestamp;
    }
}
