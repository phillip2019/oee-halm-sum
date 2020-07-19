package com.aikosolar.bigdata.ct;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * @author xiaowei.song
 * @version v1.0.0
 * @description TODO
 * @date 2020/06/22 15:48
 */
public class EqpCTSourceWithCT extends EqpCTSource {

    private static final long serialVersionUID = 1L;

    public EqpCTSourceWithCT() {
    }

    /**
     * 默认构造方法，直接由dfTube填充
     **/
    public EqpCTSourceWithCT(EqpCTSource eqpCTSource) throws InvocationTargetException, IllegalAccessException {
        BeanUtils.copyProperties(this, eqpCTSource);
    }

    public String endTime = "1970-01-01 01:01:00";
    public Long ct = -1L;

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Long getCt() {
        return ct;
    }

    public void setCt(Long ct) {
        this.ct = ct;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DFTubeWithCT{");
        sb.append("endTime='").append(endTime).append('\'');
        sb.append(", ct=").append(ct);
        sb.append(", id='").append(id).append('\'');
        sb.append(", eqpID='").append(eqpID).append('\'');
        sb.append(", site='").append(site).append('\'');
        sb.append(", clock='").append(clock).append('\'');
        sb.append(", tubeID='").append(tubeID).append('\'');
        sb.append(", text1='").append(text1).append('\'');
        sb.append(", text2='").append(text2).append('\'');
        sb.append(", text3='").append(text3).append('\'');
        sb.append(", text4='").append(text4).append('\'');
        sb.append(", boatID='").append(boatID).append('\'');
        sb.append(", gasPOClBubbLeve=").append(gasPOClBubbLeve);
        sb.append(", gasN2_POCl3VolumeAct=").append(gasN2_POCl3VolumeAct);
        sb.append(", gasPOClBubbTempAct=").append(gasPOClBubbTempAct);
        sb.append(", recipe='").append(recipe).append('\'');
        sb.append(", dataVarAllRunCount=").append(dataVarAllRunCount);
        sb.append(", dataVarAllRunNoLef=").append(dataVarAllRunNoLef);
        sb.append(", vacuumDoorPressure='").append(vacuumDoorPressure).append('\'');
        sb.append(", dataVarAllRunTime='").append(dataVarAllRunTime).append('\'');
        sb.append(", timeSecond=").append(timeSecond);
        sb.append(", ds='").append(ds).append('\'');
        sb.append(", testTime='").append(testTime).append('\'');
        sb.append(", firstStatus=").append(firstStatus);
        sb.append('}');
        return sb.toString();
    }
}
