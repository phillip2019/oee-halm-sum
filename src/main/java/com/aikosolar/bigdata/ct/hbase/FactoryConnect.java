package com.aikosolar.bigdata.ct.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;
import java.io.Serializable;

/**
 * 单例模式 安全的拿到连接
 * Date : 16:45 2018/3/16
 */
public class FactoryConnect implements Serializable {

    private static volatile Connection connection;

    public static final Configuration conf = HBaseConfiguration.create();

    private FactoryConnect() {}

    public static Connection getConnection() throws IOException {
        if (null == connection) {
            synchronized (FactoryConnect.class) {
                try {
                    if (null == connection) {
                        connection = ConnectionFactory.createConnection(conf);
                    }
                } catch (Exception e) {
                    System.err.println("读取配置文件异常");
                }
            }
        }
        return connection;
    }
}

