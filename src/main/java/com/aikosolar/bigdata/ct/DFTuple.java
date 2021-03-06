package com.aikosolar.bigdata.ct;


import com.google.common.collect.ImmutableList;
import org.apache.flink.api.java.tuple.Tuple;

import java.io.Serializable;

public class DFTuple<T extends Tuple> implements Serializable {
    public T data;
    public String rowKey;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DFTube22{");
        sb.append("data=").append(data);
        sb.append(", rowKey='").append(rowKey).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(ImmutableList.of(0, 100).stream().anyMatch(Integer.valueOf(0)::equals));
    }
}
