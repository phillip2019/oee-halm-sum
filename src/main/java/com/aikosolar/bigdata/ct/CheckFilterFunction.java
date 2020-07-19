package com.aikosolar.bigdata.ct;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.java.tuple.Tuple2;

/**
 *
 * @author xiaowei.song
 * @date 2019/8/12
 */
public class CheckFilterFunction extends RichFilterFunction<Tuple2<String, String>> {
    @Override
    public boolean filter(Tuple2<String, String> stringStringTuple2) throws Exception {
        return false;
    }

//    private JedisResourcePool pool;
//
//    private String url;
//    private String partners;
//
//    public CheckFilterFunction(String url, String partners){
//        this.url = url;
//        this.partners = partners;
//    }
//
//    @Override
//    public void open(Configuration parameters) throws Exception {
//        this.pool = JCache.buildJedisPool(this.url);
//    }
//
//    @Override
//    public void close() throws Exception {
//        JCache.closeJedisPool(this.pool);
//    }
//
//    @Override
//    public boolean filter(Tuple2<String, String> value) throws Exception {
//
//        if (null == value || StringUtils.isBlank(value.f0) || StringUtils.isBlank(value.f1)) {
//            return false;
//        }
//
//        /* Fixed me: 此处判断partners为空，则若配置成空，则全部数据都会通过，应该是为空，则没有一条数据通过 */
//        if (StringUtils.isBlank(partners) || partners.contains(value.f0)) {
//            AndroidDeviceDO device = JSONObject.parseObject(value.f1, AndroidDeviceDO.class);
//            long windowTime = device.getGmtCreate();
//            int date = getIntegerDate(windowTime);
//            String key = device.getDeviceId() + "_" + String.valueOf(date);
//            Long i = JCache.sadd(this.pool, key, device.getPartner(), 0);
//            if (1L == i) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * 获取当前日期
//     *
//     * @param windowTime 计算时间窗口结束时间
//     * @return
//     */
//    public static Integer getIntegerDate(long windowTime) {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
//        String date = simpleDateFormat.format(new Date(windowTime));
//        return Integer.parseInt(date);
//    }
}
