package com.aikosolar.bigdata.ct.client;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.mortbay.util.ajax.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * @author xiaowei.song
 */
public class HbaseClient {
    private static final Logger logger = LoggerFactory.getLogger(HbaseClient.class);

    private static Admin admin;
    public static Connection conn;

    static {
        Configuration conf = HBaseConfiguration.create();
   /*     conf.set("hbase.rootdir", parameterTool.get("hbase.rootdir"));
        conf.set("hbase.zookeeper.quorum", parameterTool.get("hbase.zookeeper.quorum"));
        conf.set("hbase.client.scanner.timeout.period", parameterTool.get("hbase.client.scanner.timeout.period"));
        conf.set("hbase.rpc.timeout", parameterTool.get("hbase.rpc.timeout"));*/
        try {
            conn = ConnectionFactory.createConnection(conf);
            admin = conn.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createTable(String tableName, String... columnFamilies) throws IOException {
        TableName tablename = TableName.valueOf(tableName);
        if(admin.tableExists(tablename)){
            System.out.println("Table Exists");
        }else{
            System.out.println("Start create table");
            HTableDescriptor tableDescriptor = new HTableDescriptor(tablename);
            for (String columnFamliy : columnFamilies) {
                HTableDescriptor column = tableDescriptor.addFamily(new HColumnDescriptor(columnFamliy));
            }
            admin.createTable(tableDescriptor);
            System.out.println("Create Table success");
        }
    }

    /**
     * 获取一列获取一行数据
     * @param tableName
     * @param rowKey
     * @param famliyName
     * @param column
     * @return
     * @throws IOException
     */
    public static String getData(String tableName, String rowKey, String famliyName, String column) throws IOException {
        Table table = conn.getTable(TableName.valueOf(tableName));
        byte[] row = Bytes.toBytes(rowKey);
        Get get = new Get(row);
        Result result = table.get(get);
        byte[] resultValue = result.getValue(famliyName.getBytes(), column.getBytes());
        if (null == resultValue){
            return null;
        }
        return new String(resultValue);
    }


    /**
     * 获取一行的所有数据 并且排序
     * @param tableName 表名
     * @param rowKey 列名
     * @throws IOException
     */
    public static List<Map.Entry> getRow(String tableName, String rowKey) throws IOException {
        Table table = conn.getTable(TableName.valueOf(tableName));
        byte[] row = Bytes.toBytes(rowKey);
        Get get = new Get(row);
        Result r = table.get(get);

        HashMap<String, Double> rst = new HashMap<>();

        for (Cell cell : r.listCells()){
            String key = Bytes.toString(cell.getQualifierArray(),cell.getQualifierOffset(),cell.getQualifierLength());
            String value = Bytes.toString(cell.getValueArray(),cell.getValueOffset(),cell.getValueLength());
            rst.put(key, new Double(value));
        }

        List<Map.Entry> ans = new ArrayList<>();
        ans.addAll(rst.entrySet());

        Collections.sort(ans, (m1, m2) -> new Double((Double)m1.getValue()-(Double) m2.getValue()).intValue());

        return ans;
    }

    /**
     * 向对应列添加数据
     * @param tableName 表名
     * @param rowKey 行号
     * @param familyName 列族名
     * @param column 列名
     * @param data 数据
     * @throws Exception
     */
    public static void putData(String tableName, String rowKey, String familyName, String column, String data) throws Exception {
        Table table = conn.getTable(TableName.valueOf(tableName));
        Put put = new Put(rowKey.getBytes());
        put.addColumn(familyName.getBytes(), column.getBytes(), data.getBytes());
        try {
            table.put(put);
        } catch (Exception e) {
            logger.error("数据写入HBase失败，数据为: rowKey: {}, cf: {}, column: {}, data: {}, 异常为: ", rowKey, familyName, column, data, e);
        } finally {
            table.close();
        }
    }

    /**
     * 添加map数据
     * @param tableName 表名
     * @param rowKey 行号
     * @param familyName 列族名
     * @param dataMap Map<String, Object> 数据map
     * @throws Exception
     */
    public static void putData(String tableName, String rowKey, String familyName, Map<String, Object> dataMap) throws Exception {
        Table table = conn.getTable(TableName.valueOf(tableName));
        Put put = new Put(rowKey.getBytes());
        dataMap.forEach((k, v) -> {
            put.addColumn(familyName.getBytes(), k.getBytes(), v.toString().getBytes());
        });
        try {
            table.put(put);
            logger.info("批量数据写入HBase成功，数据为: rowKey: {}, cf: {}, data: {}, 异常为: ", rowKey, familyName, JSON.toString(dataMap));
        } catch (Exception e) {
            logger.error("批量数据写入HBase失败，数据为: rowKey: {}, cf: {}, data: {}, 异常为: ", rowKey, familyName, dataMap, e);
        } finally {
            table.close();
        }
    }

    /**
     * 将该单元格加1
     * @param tableName 表名
     * @param rowKey 行号
     * @param familyName 列族名
     * @param column 列名
     * @throws Exception
     */
    public static void increamColumn(String tableName, String rowKey, String familyName, String column) throws Exception {
        String val = getData(tableName, rowKey, familyName, column);
        int res = 1;
        if (val != null) {
            res = Integer.valueOf(val) + 1;
        }
        putData(tableName, rowKey, familyName, column, String.valueOf(res));
    }

    public static void main(String[] args) throws IOException {
//        List<Map.Entry> ps = HbaseClient.getRow("ps", "1");
//        ps.forEach(System.out::println);
        Table table = conn.getTable(TableName.valueOf("YW_DATA_DF_sxw"));
        table.close();
    }


    /**
     * 取出表中所有的key
     * @param tableName
     * @return
     */
    public static List<String> getAllKey(String tableName) throws IOException {
        List<String> keys = new ArrayList<>();
        Scan scan = new Scan();
        Table table = HbaseClient.conn.getTable(TableName.valueOf(tableName));
        ResultScanner scanner = table.getScanner(scan);
        for (Result r : scanner) {
            keys.add(new String(r.getRow()));
        }
        return keys;
    }
}