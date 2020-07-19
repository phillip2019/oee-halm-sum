package com.aikosolar.bigdata.ct;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;

public class JsonToDevice implements MapFunction<String, Tuple2<String, String>> {
  @Override
  public Tuple2<String, String> map(String s) throws Exception {
    return null;
  }

 /* private static final Logger logger = LoggerFactory.getLogger(JsonToDevice.class);

  @Override
  public Tuple2<String, String> map(String input) throws Exception {
    Tuple2<String, String> empty = new Tuple2<>("","");
    if (StringUtils.isBlank(input)) {
      return empty;
    }
    AndroidDeviceDO divice = new AndroidDeviceDO();
    try {
      JSONObject json = JSONObject.parseObject(input);
      divice.setPlat(Platform.ANDROID.getCode());
      divice.setType(json.getString("type"));
      divice.setSessionId(json.getString("sessionId"));
      divice.setTrueIp(json.getString("trueIp"));
      divice.setGmtCreate(json.getLong("gmtCreate"));

      String dataStr = json.getString("data");

      if (StringUtils.isNotEmpty(dataStr)) {
        JSONObject data = JSONObject.parseObject(dataStr);
        divice.setBrand(data.getString("brand"));
        divice.setPartner(data.getString("partner"));
        divice.setDeviceId(data.getString("deviceId"));
        divice.setCarrier(data.getString("carrier"));
        divice.setNetworkType(data.getString("networkType"));
        divice.setInstalledPackages(data.getString("installedPackages"));
        divice.setAbnormalTags(data.getString("abnormalTags"));
        divice.setErrorcore(data.getString("errorCore"));
        divice.setReleaseVersion(data.getString("releaseVersion"));
        divice.setApkVersion(data.getString("apkVersion"));
        divice.setFmVersion(data.getString("fmVersion"));
        divice.setRoot(data.getString("root"));
        divice.setProxyInfo(data.getString("proxyInfo"));
        divice.setVpnIp(data.getString("vpnIp"));
        divice.setSignMD5(data.getString("signMD5"));
        divice.setInstalledDangerApps(data.getString("installedDangerApps"));
        divice.setLocationOfGPS(data.getString("locationOfGPS"));
        divice.setLocationOfWifi(data.getString("locationOfWifi"));
        divice.setLocationOfCell(data.getString("locationOfCell"));
        divice.setEmulatorType(data.getString("emulatorType"));
        divice.setModel(data.getString("model"));
      }
    } catch (Exception e) {
      logger.error("android etl数据:{},异常", input, e);
      return empty;
    }
    if (null == divice || 0l == divice.getGmtCreate()
            || StringUtils.isBlank(divice.getDeviceId())
            || StringUtils.isBlank(divice.getPartner())){
      return empty;
    }
    return new Tuple2<>(divice.getPartner(), JSON.toJSONString(divice));
  }*/
}
