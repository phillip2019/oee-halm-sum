package com.aikosolar.bigdata.ct.hbase;


import org.apache.flink.api.java.tuple.*;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

/**
 * 将tuple中的存放在Hbase中
 */

public class AssignmentTuple {
    /**
     * tuple 为1
     *
     * @param tuple1  传入tuple的值
     * @param rowKey  传入的rowkey的值
     * @param columns 需要赋值的列
     * @param table   put的table对象
     */

    public void setTuple1(Tuple1<Object> tuple1, String rowKey, List<String> columns, Table table) {
        new AssignmentTuple().putData(tuple1, rowKey, columns, table);
    }

    public void setTuple2(Tuple2<Object, Object> tuple2, String rowKey, List<String> columns, Table table) {
        new AssignmentTuple().putData(tuple2, rowKey, columns, table);
    }

    public void setTuple3(Tuple3<Object, Object, Object> tuple3, String rowKey, List<String> columns, Table table) {
        new AssignmentTuple().putData(tuple3, rowKey, columns, table);

    }

    public void setTuple4(Tuple4<Object, Object, Object, Object> tuple4, String rowKey, List<String> columns, Table table) {
        new AssignmentTuple().putData(tuple4, rowKey, columns, table);

    }

    public void setTuple5(Tuple5<Object, Object, Object, Object, Object> tuple5, String rowKey, List<String> columns, Table table) {
        new AssignmentTuple().putData(tuple5, rowKey, columns, table);
    }

    public void setTuple6(Tuple6 tuple6, String rowKey, List<String> columns, Table table) {

        new AssignmentTuple().putData(tuple6, rowKey, columns, table);

    }

    public void setTuple7(Tuple7 tuple7, String rowKey, List<String> columns, Table table) {

        new AssignmentTuple().putData(tuple7, rowKey, columns, table);

    }

    public void setTuple8(Tuple8 tuple8, String rowKey, List<String> columns, Table table) {

        new AssignmentTuple().putData(tuple8, rowKey, columns, table);

    }

    public void setTuple9(Tuple9 tuple9, String rowKey, List<String> columns, Table table) {

        new AssignmentTuple().putData(tuple9, rowKey, columns, table);

    }

    public void setTuple10(Tuple10 tuple10, String rowKey, List<String> columns, Table table) {

        new AssignmentTuple().putData(tuple10, rowKey, columns, table);

    }

    public void setTuple11(Tuple11 tuple11, String rowKey, List<String> columns, Table table) {

        new AssignmentTuple().putData(tuple11, rowKey, columns, table);

    }

    public void setTuple12(Tuple12 tuple12, String rowKey, List<String> columns, Table table) {

        new AssignmentTuple().putData(tuple12, rowKey, columns, table);

    }

    public void setTuple13(Tuple13 tuple13, String rowKey, List<String> columns, Table table) {

        new AssignmentTuple().putData(tuple13, rowKey, columns, table);

    }

    public void setTuple14(Tuple14 tuple14, String rowKey, List<String> columns, Table table) {

        new AssignmentTuple().putData(tuple14, rowKey, columns, table);

    }

    public void setTuple15(Tuple15 tuple15, String rowKey, List<String> columns, Table table) {

        new AssignmentTuple().putData(tuple15, rowKey, columns, table);

    }

    public void setTuple16(Tuple16 tuple16, String rowKey, List<String> columns, Table table) {

        new AssignmentTuple().putData(tuple16, rowKey, columns, table);

    }

    public void setTuple17(Tuple17 tuple17, String rowKey, List<String> columns, Table table) {

        new AssignmentTuple().putData(tuple17, rowKey, columns, table);

    }

    public void setTuple18(Tuple18 tuple18, String rowKey, List<String> columns, Table table) {

        new AssignmentTuple().putData(tuple18, rowKey, columns, table);

    }

    public void setTuple19(Tuple19 tuple19, String rowKey, List<String> columns, Table table) {

        new AssignmentTuple().putData(tuple19, rowKey, columns, table);

    }

    public void setTuple20(Tuple20 tuple20, String rowKey, List<String> columns, Table table) {

        new AssignmentTuple().putData(tuple20, rowKey, columns, table);

    }

    public void setTuple21(Tuple21 tuple21, String rowKey, List<String> columns, Table table) {

        new AssignmentTuple().putData(tuple21, rowKey, columns, table);

    }

    public void setTuple22(Tuple22 tuple22, String rowKey, List<String> columns, Table table) {

        new AssignmentTuple().putData(tuple22, rowKey, columns, table);

    }

    public void setTuple23(Tuple23 tuple23, String rowKey, List<String> columns, Table table) {

        new AssignmentTuple().putData(tuple23, rowKey, columns, table);

    }

    public void setTuple24(Tuple24 tuple24, String rowKey, List<String> columns, Table table) {

        new AssignmentTuple().putData(tuple24, rowKey, columns, table);

    }

    public void setTuple25(Tuple25 tuple25, String rowKey, List<String> columns, Table table) {
        new AssignmentTuple().putData(tuple25, rowKey, columns, table);
    }

    /**
     * 将tuple中的数据一一对应的赋值给列
     *
     * @param tuple   tuple中的数据
     * @param rowKey  设置的行值
     * @param columns 对应的列名
     * @param table   对应的table对象
     */

    public void putData(Tuple tuple, String rowKey, List<String> columns, Table table) {
        Put put = new Put(Bytes.toBytes(rowKey));
        Long timeStamp = Instant.now().toEpochMilli();
        for (int i = 0; i < columns.size(); i++) {
            String[] split = columns.get(i).split(":");
            put.addColumn(Bytes.toBytes(split[0]), Bytes.toBytes(split[1]), timeStamp, Bytes.toBytes(tuple.getField(i).toString()));
        }
        try {
            table.put(put);
        } catch (IOException e) {
            throw new RuntimeException("存放失败", e);
        }
    }
}