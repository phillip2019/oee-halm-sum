package com.aikosolar.bigdata.ct.sink;

import com.aikosolar.bigdata.ct.hbase.FactoryConnect;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

public class CTSinkHBase extends RichSinkFunction<Map<String, Object>> {

    public static final Logger logger = LoggerFactory.getLogger(CTSinkHBase.class);

    private static final long serialVersionUID = 1L;

    public static Connection conn = null;

    /**
     * 表名
     */
    private String tableName;

    /**
     * @param tableName    表名
     */
    public CTSinkHBase(String tableName) {
        this.tableName = tableName;
    }

    public CTSinkHBase() {}

    /**
     * 初始化完成连接  当表不存在的时候 新建表和family列
     *
     * @param parameters 调用父类的方法
     * @throws Exception 创建连接失败
     */
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        conn = FactoryConnect.getConnection();
        Admin admin = conn.getAdmin();
        final TableName tableName1 = TableName.valueOf(tableName);
    }

    /**
     * 执行方法 将数据存入hbase
     * @param value 传入的结果
     */
    @Override
    public void invoke(Map<String, Object> value, Context context) throws Exception {
        Table table = FactoryConnect.getConnection().getTable(TableName.valueOf(tableName));
        Put put = new Put(Bytes.toBytes(value.get("rowkey").toString()));
        Long timeStamp = Instant.now().toEpochMilli();
        for (Map.Entry<String, Object> entry : value.entrySet()) {
            String key = entry.getKey();
            Object v = entry.getValue();
            put.addColumn(Bytes.toBytes("cf"), Bytes.toBytes(key), timeStamp, Bytes.toBytes(v.toString()));
        }
        try {
            table.put(put);
        } catch (IOException e) {
            logger.error("存放失败", e);
            throw new RuntimeException("存放失败", e);
        }
    }

    @Override
    public void close() throws Exception {
        super.close();
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}