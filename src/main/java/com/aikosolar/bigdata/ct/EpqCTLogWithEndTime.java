package com.aikosolar.bigdata.ct;

/**
 * 带结束时间
 */
public class EpqCTLogWithEndTime extends EqpCTLog {
    public String endTime;

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EpqCTSourceWithEndTIme{");
        sb.append("endTime='").append(endTime).append('\'');
        sb.append(", rowkey='").append(rowkey).append('\'');
        sb.append(", site='").append(site).append('\'');
        sb.append(", factory='").append(factory).append('\'');
        sb.append(", shift='").append(shift).append('\'');
        sb.append(", dayDate='").append(dayDate).append('\'');
        sb.append(", dayHour='").append(dayHour).append('\'');
        sb.append(", twoHour='").append(twoHour).append('\'');
        sb.append(", halfHour='").append(halfHour).append('\'');
        sb.append(", createTime='").append(createTime).append('\'');
        sb.append(", eqpId='").append(eqpId).append('\'');
        sb.append(", comments='").append(comments).append('\'');
        sb.append(", orderType='").append(orderType).append('\'');
        sb.append(", binType='").append(binType).append('\'');
        sb.append(", outputQty='").append(outputQty).append('\'');
        sb.append(", outputQty2='").append(outputQty2).append('\'');
        sb.append(", uoc='").append(uoc).append('\'');
        sb.append(", isc='").append(isc).append('\'');
        sb.append(", ff='").append(ff).append('\'');
        sb.append(", eta='").append(eta).append('\'');
        sb.append(", m3Eta='").append(m3Eta).append('\'');
        sb.append(", irev2='").append(irev2).append('\'');
        sb.append(", rser='").append(rser).append('\'');
        sb.append(", rshunt='").append(rshunt).append('\'');
        sb.append(", tcell='").append(tcell).append('\'');
        sb.append(", tmonicell='").append(tmonicell).append('\'');
        sb.append(", insolM1='").append(insolM1).append('\'');
        sb.append(", m3Insol='").append(m3Insol).append('\'');
        sb.append(", numA='").append(numA).append('\'');
        sb.append(", numZhengmianyichang='").append(numZhengmianyichang).append('\'');
        sb.append(", numBeimianyichang='").append(numBeimianyichang).append('\'');
        sb.append(", numYanseyichang='").append(numYanseyichang).append('\'');
        sb.append(", numYanseyichang2='").append(numYanseyichang2).append('\'');
        sb.append(", numMo5='").append(numMo5).append('\'');
        sb.append(", numIrev2='").append(numIrev2).append('\'');
        sb.append(", numRsh='").append(numRsh).append('\'');
        sb.append(", numDx='").append(numDx).append('\'');
        sb.append(", numDuanshan='").append(numDuanshan).append('\'');
        sb.append(", numHuashang='").append(numHuashang).append('\'');
        sb.append(", numHeidian='").append(numHeidian).append('\'');
        sb.append(", numKexindu='").append(numKexindu).append('\'');
        sb.append(", numYinlie='").append(numYinlie).append('\'');
        sb.append(", numColorAll='").append(numColorAll).append('\'');
        sb.append(", numColorA='").append(numColorA).append('\'');
        sb.append(", numColorB='").append(numColorB).append('\'');
        sb.append(", numColorC='").append(numColorC).append('\'');
        sb.append(", numDsAll='").append(numDsAll).append('\'');
        sb.append(", numDs_0='").append(numDs_0).append('\'');
        sb.append(", numDs_1='").append(numDs_1).append('\'');
        sb.append(", numDs_2='").append(numDs_2).append('\'');
        sb.append(", numDs_3='").append(numDs_3).append('\'');
        sb.append(", numDs_4='").append(numDs_4).append('\'');
        sb.append(", numDs_5='").append(numDs_5).append('\'');
        sb.append(", numDs_5p='").append(numDs_5p).append('\'');
        sb.append(", num213='").append(num213).append('\'');
        sb.append(", num214='").append(num214).append('\'');
        sb.append(", num215='").append(num215).append('\'');
        sb.append(", num216='").append(num216).append('\'');
        sb.append(", num217='").append(num217).append('\'');
        sb.append(", num218='").append(num218).append('\'');
        sb.append(", num219='").append(num219).append('\'');
        sb.append(", num220='").append(num220).append('\'');
        sb.append(", num221='").append(num221).append('\'');
        sb.append(", num222='").append(num222).append('\'');
        sb.append(", num223='").append(num223).append('\'');
        sb.append(", num224='").append(num224).append('\'');
        sb.append(", num225='").append(num225).append('\'');
        sb.append(", num226='").append(num226).append('\'');
        sb.append(", num227='").append(num227).append('\'');
        sb.append(", num228='").append(num228).append('\'');
        sb.append(", num229='").append(num229).append('\'');
        sb.append(", num230='").append(num230).append('\'');
        sb.append(", num231='").append(num231).append('\'');
        sb.append(", num232='").append(num232).append('\'');
        sb.append(", num233='").append(num233).append('\'');
        sb.append(", num234='").append(num234).append('\'');
        sb.append(", num235='").append(num235).append('\'');
        sb.append(", num236='").append(num236).append('\'');
        sb.append(", num237='").append(num237).append('\'');
        sb.append(", num238='").append(num238).append('\'');
        sb.append(", num239='").append(num239).append('\'');
        sb.append(", num240='").append(num240).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
