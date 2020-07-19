package com.aikosolar.bigdata.ct.util;


import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
  * @author xiaowei.song
  * @version v1.0.0
  */
public class MapUtil {

  /**
    * 定义一个getValueOrDefault的方法
    * 从map中获取默认值，若获取为null或空字符串，则赋予默认值
    * @return
    */
  public static String getValueOrDefault(Map<String, String> m, String key, String defaultValue) {
    String value = m.get(key);
    if (StringUtils.isBlank(value)) {
      value = defaultValue;
    }
    return value;
  }
}
