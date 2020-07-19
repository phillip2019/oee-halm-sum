package com.aikosolar.bigdata.ct.sink;

import com.aikosolar.bigdata.ct.DFTuple;
import com.aikosolar.bigdata.ct.hbase.AssignmentTuple;
import com.aikosolar.bigdata.ct.hbase.FactoryConnect;
import org.apache.flink.api.java.tuple.*;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

public class SinkHbase<T extends DFTuple<V>, V extends Tuple> extends RichSinkFunction<T> {

    public static final Logger logger = LoggerFactory.getLogger(SinkHbase.class);

    private static final long serialVersionUID = 1L;

    public static Connection conn = null;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 列族名
     */
    private List<String> columnFails;

    /**
     * 列名以family:column的形式传入    column与tuple中的值一一对应
     */
    private List<String> columns;

    /**
     * @param tableName    表名
     * @param columnFamily 列族名  当表存在时不用输入
     * @param columns      储存的列名 列族:列名
     */
    public SinkHbase(String tableName, List<String> columnFamily, List<String> columns) {

        this.tableName = tableName;

        this.columnFails = columnFamily;

        this.columns = columns;
    }

    /**
     * @param tableName 表名
     * @param columns   储存的列名 列族:列名
     */
    public SinkHbase(String tableName, List<String> columns) {
        this(tableName, columns, null);
    }


    public SinkHbase() {}

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
        if (!admin.tableExists(tableName1)) {
            HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName1);
            for (String columnFamily : columnFails) {
                hTableDescriptor.addFamily(new HColumnDescriptor(columnFamily));
            }
            admin.createTable(hTableDescriptor);
        }
    }

    /**
     * 执行方法 将数据存入hbase
     * @param value 传入的结果
     */
    @Override
    public void invoke(T value, Context context) throws Exception {
        Map<Class, Method> map = new HashMap<>(25);
        new SinkHbase<T, V>().initMap(map);
        Table table = FactoryConnect.getConnection().getTable(TableName.valueOf(tableName));
        Set<Class> keys = map.keySet();
        for (Class key : keys) {
            if (value.getData().getClass() == key) {
                V data = value.getData();
                String rowKey = value.getRowKey();
                map.get(key).invoke(new AssignmentTuple(), data, rowKey, columns, table);
                return;
            }
        }
    }

    private void initMap(Map<Class, Method> map) {
        try {
            map.put(Tuple1.class, AssignmentTuple.class.getMethod("setTuple1", Tuple1.class, String.class, List.class, Table.class));
            map.put(Tuple2.class, AssignmentTuple.class.getMethod("setTuple2", Tuple2.class, String.class, List.class, Table.class));
            map.put(Tuple3.class, AssignmentTuple.class.getMethod("setTuple3", Tuple3.class, String.class, List.class, Table.class));
            map.put(Tuple4.class, AssignmentTuple.class.getMethod("setTuple4", Tuple4.class, String.class, List.class, Table.class));
            map.put(Tuple5.class, AssignmentTuple.class.getMethod("setTuple5", Tuple5.class, String.class, List.class, Table.class));
            map.put(Tuple6.class, AssignmentTuple.class.getMethod("setTuple6", Tuple6.class, String.class, List.class, Table.class));
            map.put(Tuple7.class, AssignmentTuple.class.getMethod("setTuple7", Tuple7.class, String.class, List.class, Table.class));
            map.put(Tuple8.class, AssignmentTuple.class.getMethod("setTuple8", Tuple8.class, String.class, List.class, Table.class));
            map.put(Tuple9.class, AssignmentTuple.class.getMethod("setTuple9", Tuple9.class, String.class, List.class, Table.class));
            map.put(Tuple10.class, AssignmentTuple.class.getMethod("setTuple10", Tuple10.class, String.class, List.class, Table.class));
            map.put(Tuple11.class, AssignmentTuple.class.getMethod("setTuple11", Tuple11.class, String.class, List.class, Table.class));
            map.put(Tuple12.class, AssignmentTuple.class.getMethod("setTuple12", Tuple12.class, String.class, List.class, Table.class));
            map.put(Tuple13.class, AssignmentTuple.class.getMethod("setTuple13", Tuple13.class, String.class, List.class, Table.class));
            map.put(Tuple14.class, AssignmentTuple.class.getMethod("setTuple14", Tuple14.class, String.class, List.class, Table.class));
            map.put(Tuple15.class, AssignmentTuple.class.getMethod("setTuple15", Tuple15.class, String.class, List.class, Table.class));
            map.put(Tuple16.class, AssignmentTuple.class.getMethod("setTuple16", Tuple16.class, String.class, List.class, Table.class));
            map.put(Tuple17.class, AssignmentTuple.class.getMethod("setTuple17", Tuple17.class, String.class, List.class, Table.class));
            map.put(Tuple18.class, AssignmentTuple.class.getMethod("setTuple18", Tuple18.class, String.class, List.class, Table.class));
            map.put(Tuple19.class, AssignmentTuple.class.getMethod("setTuple19", Tuple19.class, String.class, List.class, Table.class));
            map.put(Tuple20.class, AssignmentTuple.class.getMethod("setTuple20", Tuple20.class, String.class, List.class, Table.class));
            map.put(Tuple21.class, AssignmentTuple.class.getMethod("setTuple21", Tuple21.class, String.class, List.class, Table.class));
            map.put(Tuple22.class, AssignmentTuple.class.getMethod("setTuple22", Tuple22.class, String.class, List.class, Table.class));
            map.put(Tuple23.class, AssignmentTuple.class.getMethod("setTuple23", Tuple23.class, String.class, List.class, Table.class));
            map.put(Tuple24.class, AssignmentTuple.class.getMethod("setTuple24", Tuple24.class, String.class, List.class, Table.class));
            map.put(Tuple25.class, AssignmentTuple.class.getMethod("setTuple25", Tuple25.class, String.class, List.class, Table.class));
        } catch (NoSuchMethodException e) {
            logger.error("反射失败，错误为: ", e);
            throw new RuntimeException("反射失败", e);
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