package com.aikosolar.bigdata.ct;

/**
  * @author xiaowei.song
  * @version v1.0.0
  */

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
  * ct设备原始数据
  */
public class EqpCTSource implements Serializable {

  private static final long serialVersionUID = 1L;

  public String rowKey = "";
  public String site = "Z2";
  public String shift = "";
  public String dayDate = "";
  public String dayHour = "";
  public String twoHour = "";
  public String halfHour = "";
  public String createTime = "";
  public String eqpId = "";
  public String comments = "";
  public String orderType = "NORMAL";
  public String binType = "OTHER";
  public Integer output = 1;
  public Integer output2 = 0;
  public Double uoc = 0D;
  public Double isc = 0D;
  public Double ff = 0D;
  public Double eta = 0D;
  public Double m3Eta = 0D;
  public Double irev2 = 0D;
  public Double rser = 0D;
  public Double rshunt = 0D;
  public Double tcell = 0D;
  public Double tmonicell = 0D;
  public Double insolM1 = 0D;
  public Double m3Insol = 0D;
  public Integer numA = 0;
  public Integer numZhengmianyichang = 0;
  public Integer numBeimianyichang = 0;
  public Integer numYanseyichang = 0;
  public Integer numYanseyichang2 = 0;
  public Integer numMo5 = 0;
  public Integer numIrev2 = 0;
  public Integer numRsh = 0;
  public Integer numDx = 0;
  public Integer numDuanshan = 0;
  public Integer numHuashang = 0;
  public Integer numHeidian = 0;
  public Integer numKexindu = 0;
  public Integer numYinlie = 0;
  public Integer numColorAll = 0;
  public Integer numColorA = 0;
  public Integer numColorB = 0;
  public Integer numColorC = 0;
  public Integer numDsAll = 0;
  public Integer numDs0 = 0;
  public Integer numDs1 = 0;
  public Integer numDs2 = 0;
  public Integer numDs3 = 0;
  public Integer numDs4 = 0;
  public Integer numDs5 = 0;
  public Integer numDs5p = 0;
  public Integer num213 = 0;
  public Integer num214 = 0;
  public Integer num215 = 0;
  public Integer num216 = 0;
  public Integer num217 = 0;
  public Integer num218 = 0;
  public Integer num219 = 0;
  public Integer num220 = 0;
  public Integer num221 = 0;
  public Integer num222 = 0;
  public Integer num223 = 0;
  public Integer num224 = 0;
  public Integer num225 = 0;
  public Integer num226 = 0;
  public Integer num227 = 0;
  public Integer num228 = 0;
  public Integer num229 = 0;
  public Integer num230 = 0;
  public Integer num231 = 0;
  public Integer num232 = 0;
  public Integer num233 = 0;
  public Integer num234 = 0;
  public Integer num235 = 0;
  public Integer num236 = 0;
  public Integer num237 = 0;
  public Integer num238 = 0;
  public Integer num239 = 0;
  public Integer num240 = 0;


  public String getRowKey() {
    return rowKey;
  }

  public void setRowKey(String rowKey) {
    this.rowKey = rowKey;
  }

  public String getSite() {
    return site;
  }

  public void setSite(String site) {
    this.site = site;
  }

  public String getShift() {
    return shift;
  }

  public void setShift(String shift) {
    this.shift = shift;
  }

  public String getDayDate() {
    return dayDate;
  }

  public void setDayDate(String dayDate) {
    this.dayDate = dayDate;
  }

  public String getDayHour() {
    return dayHour;
  }

  public void setDayHour(String dayHour) {
    this.dayHour = dayHour;
  }

  public String getTwoHour() {
    return twoHour;
  }

  public void setTwoHour(String twoHour) {
    this.twoHour = twoHour;
  }

  public String getHalfHour() {
    return halfHour;
  }

  public void setHalfHour(String halfHour) {
    this.halfHour = halfHour;
  }

  public String getCreateTime() {
    return createTime;
  }

  public void setCreateTime(String createTime) {
    this.createTime = createTime;
  }

  public String getEqpId() {
    return eqpId;
  }

  public void setEqpId(String eqpId) {
    this.eqpId = eqpId;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }

  public String getBinType() {
    return binType;
  }

  public void setBinType(String binType) {
    this.binType = binType;
  }

  public Integer getOutput() {
    return output;
  }

  public void setOutput(Integer output) {
    this.output = output;
  }

  public Integer getOutput2() {
    return output2;
  }

  public void setOutput2(Integer output2) {
    this.output2 = output2;
  }

  public Double getUoc() {
    return uoc;
  }

  public void setUoc(Double uoc) {
    this.uoc = uoc;
  }

  public Double getIsc() {
    return isc;
  }

  public void setIsc(Double isc) {
    this.isc = isc;
  }

  public Double getFf() {
    return ff;
  }

  public void setFf(Double ff) {
    this.ff = ff;
  }

  public Double getEta() {
    return eta;
  }

  public void setEta(Double eta) {
    this.eta = eta;
  }

  public Double getM3Eta() {
    return m3Eta;
  }

  public void setM3Eta(Double m3Eta) {
    this.m3Eta = m3Eta;
  }

  public Double getIrev2() {
    return irev2;
  }

  public void setIrev2(Double irev2) {
    this.irev2 = irev2;
  }

  public Double getRser() {
    return rser;
  }

  public void setRser(Double rser) {
    this.rser = rser;
  }

  public Double getRshunt() {
    return rshunt;
  }

  public void setRshunt(Double rshunt) {
    this.rshunt = rshunt;
  }

  public Double getTcell() {
    return tcell;
  }

  public void setTcell(Double tcell) {
    this.tcell = tcell;
  }

  public Double getTmonicell() {
    return tmonicell;
  }

  public void setTmonicell(Double tmonicell) {
    this.tmonicell = tmonicell;
  }

  public Double getInsolM1() {
    return insolM1;
  }

  public void setInsolM1(Double insolM1) {
    this.insolM1 = insolM1;
  }

  public Double getM3Insol() {
    return m3Insol;
  }

  public void setM3Insol(Double m3Insol) {
    this.m3Insol = m3Insol;
  }

  public Integer getNumA() {
    return numA;
  }

  public void setNumA(Integer numA) {
    this.numA = numA;
  }

  public Integer getNumZhengmianyichang() {
    return numZhengmianyichang;
  }

  public void setNumZhengmianyichang(Integer numZhengmianyichang) {
    this.numZhengmianyichang = numZhengmianyichang;
  }

  public Integer getNumBeimianyichang() {
    return numBeimianyichang;
  }

  public void setNumBeimianyichang(Integer numBeimianyichang) {
    this.numBeimianyichang = numBeimianyichang;
  }

  public Integer getNumYanseyichang() {
    return numYanseyichang;
  }

  public void setNumYanseyichang(Integer numYanseyichang) {
    this.numYanseyichang = numYanseyichang;
  }

  public Integer getNumYanseyichang2() {
    return numYanseyichang2;
  }

  public void setNumYanseyichang2(Integer numYanseyichang2) {
    this.numYanseyichang2 = numYanseyichang2;
  }

  public Integer getNumMo5() {
    return numMo5;
  }

  public void setNumMo5(Integer numMo5) {
    this.numMo5 = numMo5;
  }

  public Integer getNumIrev2() {
    return numIrev2;
  }

  public void setNumIrev2(Integer numIrev2) {
    this.numIrev2 = numIrev2;
  }

  public Integer getNumRsh() {
    return numRsh;
  }

  public void setNumRsh(Integer numRsh) {
    this.numRsh = numRsh;
  }

  public Integer getNumDx() {
    return numDx;
  }

  public void setNumDx(Integer numDx) {
    this.numDx = numDx;
  }

  public Integer getNumDuanshan() {
    return numDuanshan;
  }

  public void setNumDuanshan(Integer numDuanshan) {
    this.numDuanshan = numDuanshan;
  }

  public Integer getNumHuashang() {
    return numHuashang;
  }

  public void setNumHuashang(Integer numHuashang) {
    this.numHuashang = numHuashang;
  }

  public Integer getNumHeidian() {
    return numHeidian;
  }

  public void setNumHeidian(Integer numHeidian) {
    this.numHeidian = numHeidian;
  }

  public Integer getNumKexindu() {
    return numKexindu;
  }

  public void setNumKexindu(Integer numKexindu) {
    this.numKexindu = numKexindu;
  }

  public Integer getNumYinlie() {
    return numYinlie;
  }

  public void setNumYinlie(Integer numYinlie) {
    this.numYinlie = numYinlie;
  }

  public Integer getNumColorAll() {
    return numColorAll;
  }

  public void setNumColorAll(Integer numColorAll) {
    this.numColorAll = numColorAll;
  }

  public Integer getNumColorA() {
    return numColorA;
  }

  public void setNumColorA(Integer numColorA) {
    this.numColorA = numColorA;
  }

  public Integer getNumColorB() {
    return numColorB;
  }

  public void setNumColorB(Integer numColorB) {
    this.numColorB = numColorB;
  }

  public Integer getNumColorC() {
    return numColorC;
  }

  public void setNumColorC(Integer numColorC) {
    this.numColorC = numColorC;
  }

  public Integer getNumDsAll() {
    return numDsAll;
  }

  public void setNumDsAll(Integer numDsAll) {
    this.numDsAll = numDsAll;
  }

  public Integer getNumDs0() {
    return numDs0;
  }

  public void setNumDs0(Integer numDs0) {
    this.numDs0 = numDs0;
  }

  public Integer getNumDs1() {
    return numDs1;
  }

  public void setNumDs1(Integer numDs1) {
    this.numDs1 = numDs1;
  }

  public Integer getNumDs2() {
    return numDs2;
  }

  public void setNumDs2(Integer numDs2) {
    this.numDs2 = numDs2;
  }

  public Integer getNumDs3() {
    return numDs3;
  }

  public void setNumDs3(Integer numDs3) {
    this.numDs3 = numDs3;
  }

  public Integer getNumDs4() {
    return numDs4;
  }

  public void setNumDs4(Integer numDs4) {
    this.numDs4 = numDs4;
  }

  public Integer getNumDs5() {
    return numDs5;
  }

  public void setNumDs5(Integer numDs5) {
    this.numDs5 = numDs5;
  }

  public Integer getNumDs5p() {
    return numDs5p;
  }

  public void setNumDs5p(Integer numDs5p) {
    this.numDs5p = numDs5p;
  }

  public Integer getNum213() {
    return num213;
  }

  public void setNum213(Integer num213) {
    this.num213 = num213;
  }

  public Integer getNum214() {
    return num214;
  }

  public void setNum214(Integer num214) {
    this.num214 = num214;
  }

  public Integer getNum215() {
    return num215;
  }

  public void setNum215(Integer num215) {
    this.num215 = num215;
  }

  public Integer getNum216() {
    return num216;
  }

  public void setNum216(Integer num216) {
    this.num216 = num216;
  }

  public Integer getNum217() {
    return num217;
  }

  public void setNum217(Integer num217) {
    this.num217 = num217;
  }

  public Integer getNum218() {
    return num218;
  }

  public void setNum218(Integer num218) {
    this.num218 = num218;
  }

  public Integer getNum219() {
    return num219;
  }

  public void setNum219(Integer num219) {
    this.num219 = num219;
  }

  public Integer getNum220() {
    return num220;
  }

  public void setNum220(Integer num220) {
    this.num220 = num220;
  }

  public Integer getNum221() {
    return num221;
  }

  public void setNum221(Integer num221) {
    this.num221 = num221;
  }

  public Integer getNum222() {
    return num222;
  }

  public void setNum222(Integer num222) {
    this.num222 = num222;
  }

  public Integer getNum223() {
    return num223;
  }

  public void setNum223(Integer num223) {
    this.num223 = num223;
  }

  public Integer getNum224() {
    return num224;
  }

  public void setNum224(Integer num224) {
    this.num224 = num224;
  }

  public Integer getNum225() {
    return num225;
  }

  public void setNum225(Integer num225) {
    this.num225 = num225;
  }

  public Integer getNum226() {
    return num226;
  }

  public void setNum226(Integer num226) {
    this.num226 = num226;
  }

  public Integer getNum227() {
    return num227;
  }

  public void setNum227(Integer num227) {
    this.num227 = num227;
  }

  public Integer getNum228() {
    return num228;
  }

  public void setNum228(Integer num228) {
    this.num228 = num228;
  }

  public Integer getNum229() {
    return num229;
  }

  public void setNum229(Integer num229) {
    this.num229 = num229;
  }

  public Integer getNum230() {
    return num230;
  }

  public void setNum230(Integer num230) {
    this.num230 = num230;
  }

  public Integer getNum231() {
    return num231;
  }

  public void setNum231(Integer num231) {
    this.num231 = num231;
  }

  public Integer getNum232() {
    return num232;
  }

  public void setNum232(Integer num232) {
    this.num232 = num232;
  }

  public Integer getNum233() {
    return num233;
  }

  public void setNum233(Integer num233) {
    this.num233 = num233;
  }

  public Integer getNum234() {
    return num234;
  }

  public void setNum234(Integer num234) {
    this.num234 = num234;
  }

  public Integer getNum235() {
    return num235;
  }

  public void setNum235(Integer num235) {
    this.num235 = num235;
  }

  public Integer getNum236() {
    return num236;
  }

  public void setNum236(Integer num236) {
    this.num236 = num236;
  }

  public Integer getNum237() {
    return num237;
  }

  public void setNum237(Integer num237) {
    this.num237 = num237;
  }

  public Integer getNum238() {
    return num238;
  }

  public void setNum238(Integer num238) {
    this.num238 = num238;
  }

  public Integer getNum239() {
    return num239;
  }

  public void setNum239(Integer num239) {
    this.num239 = num239;
  }

  public Integer getNum240() {
    return num240;
  }

  public void setNum240(Integer num240) {
    this.num240 = num240;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("EqpCTSource{");
    sb.append("rowKey='").append(rowKey).append('\'');
    sb.append("site='").append(site).append('\'');
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
    sb.append(", output=").append(output);
    sb.append(", output2=").append(output2);
    sb.append(", uoc=").append(uoc);
    sb.append(", isc=").append(isc);
    sb.append(", ff=").append(ff);
    sb.append(", eta=").append(eta);
    sb.append(", m3Eta=").append(m3Eta);
    sb.append(", irev2=").append(irev2);
    sb.append(", rser=").append(rser);
    sb.append(", rshunt=").append(rshunt);
    sb.append(", tcell=").append(tcell);
    sb.append(", tmonicell=").append(tmonicell);
    sb.append(", insolM1=").append(insolM1);
    sb.append(", m3Insol=").append(m3Insol);
    sb.append(", numA=").append(numA);
    sb.append(", numZhengmianyichang=").append(numZhengmianyichang);
    sb.append(", numBeimianyichang=").append(numBeimianyichang);
    sb.append(", numYanseyichang=").append(numYanseyichang);
    sb.append(", numYanseyichang2=").append(numYanseyichang2);
    sb.append(", numMo5=").append(numMo5);
    sb.append(", numIrev2=").append(numIrev2);
    sb.append(", numRsh=").append(numRsh);
    sb.append(", numDx=").append(numDx);
    sb.append(", numDuanshan=").append(numDuanshan);
    sb.append(", numHuashang=").append(numHuashang);
    sb.append(", numHeidian=").append(numHeidian);
    sb.append(", numKexindu=").append(numKexindu);
    sb.append(", numYinlie=").append(numYinlie);
    sb.append(", numColorAll=").append(numColorAll);
    sb.append(", numColorA=").append(numColorA);
    sb.append(", numColorB=").append(numColorB);
    sb.append(", numColorC=").append(numColorC);
    sb.append(", numDsAll=").append(numDsAll);
    sb.append(", numDs0=").append(numDs0);
    sb.append(", numDs1=").append(numDs1);
    sb.append(", numDs2=").append(numDs2);
    sb.append(", numDs3=").append(numDs3);
    sb.append(", numDs4=").append(numDs4);
    sb.append(", numDs5=").append(numDs5);
    sb.append(", numDs5p=").append(numDs5p);
    sb.append(", num213=").append(num213);
    sb.append(", num214=").append(num214);
    sb.append(", num215=").append(num215);
    sb.append(", num216=").append(num216);
    sb.append(", num217=").append(num217);
    sb.append(", num218=").append(num218);
    sb.append(", num219=").append(num219);
    sb.append(", num220=").append(num220);
    sb.append(", num221=").append(num221);
    sb.append(", num222=").append(num222);
    sb.append(", num223=").append(num223);
    sb.append(", num224=").append(num224);
    sb.append(", num225=").append(num225);
    sb.append(", num226=").append(num226);
    sb.append(", num227=").append(num227);
    sb.append(", num228=").append(num228);
    sb.append(", num229=").append(num229);
    sb.append(", num230=").append(num230);
    sb.append(", num231=").append(num231);
    sb.append(", num232=").append(num232);
    sb.append(", num233=").append(num233);
    sb.append(", num234=").append(num234);
    sb.append(", num235=").append(num235);
    sb.append(", num236=").append(num236);
    sb.append(", num237=").append(num237);
    sb.append(", num238=").append(num238);
    sb.append(", num239=").append(num239);
    sb.append(", num240=").append(num240);
    sb.append('}');
    return sb.toString();
  }

  public static void main(String[] args) throws NoSuchMethodException {
    Method m = EqpCTSource.class.getDeclaredMethod(String.format("set%s", StringUtils.capitalize("num240")), Integer.class);
  }
}
