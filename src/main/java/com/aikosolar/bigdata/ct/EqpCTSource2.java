package com.aikosolar.bigdata.ct;

/**
 * @author xiaowei.song
 * @version v1.0.0
 * @description TODO
 * @date 2020/06/30 16:24
 */
public class EqpCTSource2 extends EqpCTSource {
    public String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DFTube2{");
        sb.append("state='").append(state).append('\'');
        sb.append(", rowkey='").append(rowkey).append('\'');
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
        sb.append(", endTime='").append(endTime).append('\'');
        sb.append(", ct=").append(ct);
        sb.append(", firstStatus=").append(firstStatus);
        sb.append('}');
        return sb.toString();
    }
}
