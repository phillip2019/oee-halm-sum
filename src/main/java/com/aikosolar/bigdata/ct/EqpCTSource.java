package com.aikosolar.bigdata.ct;

/**
  * @author xiaowei.song
  * @version v1.0.0
  */

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
  * ct设备原始数据
  */
public class EqpCTSource implements Serializable {

  private static final long serialVersionUID = 1L;
  public String rowkey = "";
  public String site = "Z2";
  public String factory = "4";
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
  public String outputQty = "1";
  public String outputQty2 = "0";
  public String uoc = "0";
  public String isc = "0";
  public String ff = "0";
  public String eta = "0";
  public String m3Eta = "0";
  public String irev2 = "0";
  public String rser = "0";
  public String rshunt = "0";
  public String tcell = "0";
  public String tmonicell = "0";
  public String insolM1 = "0";
  public String m3Insol = "0";
  public String numA = "0";
  public String numZhengmianyichang = "0";
  public String numBeimianyichang = "0";
  public String numYanseyichang = "0";
  public String numYanseyichang2 = "0";
  public String numMo5 = "0";
  public String numIrev2 = "0";
  public String numRsh = "0";
  public String numDx = "0";
  public String numDuanshan = "0";
  public String numHuashang = "0";
  public String numHeidian = "0";
  public String numKexindu = "0";
  public String numYinlie = "0";
  public String numColorAll = "0";
  public String numColorA = "0";
  public String numColorB = "0";
  public String numColorC = "0";
  public String numDsAll = "0";
  public String numDs_0 = "0";
  public String numDs_1 = "0";
  public String numDs_2 = "0";
  public String numDs_3 = "0";
  public String numDs_4 = "0";
  public String numDs_5 = "0";
  public String numDs_5p = "0";
  public String num213 = "0";
  public String num214 = "0";
  public String num215 = "0";
  public String num216 = "0";
  public String num217 = "0";
  public String num218 = "0";
  public String num219 = "0";
  public String num220 = "0";
  public String num221 = "0";
  public String num222 = "0";
  public String num223 = "0";
  public String num224 = "0";
  public String num225 = "0";
  public String num226 = "0";
  public String num227 = "0";
  public String num228 = "0";
  public String num229 = "0";
  public String num230 = "0";
  public String num231 = "0";
  public String num232 = "0";
  public String num233 = "0";
  public String num234 = "0";
  public String num235 = "0";
  public String num236 = "0";
  public String num237 = "0";
  public String num238 = "0";
  public String num239 = "0";
  public String num240 = "0";

  public String getRowkey() {
    return rowkey;
  }

  public void setRowkey(String rowkey) {
    this.rowkey = rowkey;
  }

  public String getFactory() {
    return factory;
  }

  public void setFactory(String factory) {
    this.factory = factory;
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

  public String getOutputQty() {
    return outputQty;
  }

  public void setOutputQty(String outputQty) {
    this.outputQty = outputQty;
  }

  public String getOutputQty2() {
    return outputQty2;
  }

  public void setOutputQty2(String outputQty2) {
    this.outputQty2 = outputQty2;
  }


  public String getUoc() {
    return uoc;
  }

  public void setUoc(String uoc) {
    this.uoc = uoc;
  }

  public String getIsc() {
    return isc;
  }

  public void setIsc(String isc) {
    this.isc = isc;
  }

  public String getFf() {
    return ff;
  }

  public void setFf(String ff) {
    this.ff = ff;
  }

  public String getEta() {
    return eta;
  }

  public void setEta(String eta) {
    this.eta = eta;
  }

  public String getM3Eta() {
    return m3Eta;
  }

  public void setM3Eta(String m3Eta) {
    this.m3Eta = m3Eta;
  }

  public String getIrev2() {
    return irev2;
  }

  public void setIrev2(String irev2) {
    this.irev2 = irev2;
  }

  public String getRser() {
    return rser;
  }

  public void setRser(String rser) {
    this.rser = rser;
  }

  public String getRshunt() {
    return rshunt;
  }

  public void setRshunt(String rshunt) {
    this.rshunt = rshunt;
  }

  public String getTcell() {
    return tcell;
  }

  public void setTcell(String tcell) {
    this.tcell = tcell;
  }

  public String getTmonicell() {
    return tmonicell;
  }

  public void setTmonicell(String tmonicell) {
    this.tmonicell = tmonicell;
  }

  public String getInsolM1() {
    return insolM1;
  }

  public void setInsolM1(String insolM1) {
    this.insolM1 = insolM1;
  }

  public String getM3Insol() {
    return m3Insol;
  }

  public void setM3Insol(String m3Insol) {
    this.m3Insol = m3Insol;
  }

  public String getNumA() {
    return numA;
  }

  public void setNumA(String numA) {
    this.numA = numA;
  }

  public String getNumZhengmianyichang() {
    return numZhengmianyichang;
  }

  public void setNumZhengmianyichang(String numZhengmianyichang) {
    this.numZhengmianyichang = numZhengmianyichang;
  }

  public String getNumBeimianyichang() {
    return numBeimianyichang;
  }

  public void setNumBeimianyichang(String numBeimianyichang) {
    this.numBeimianyichang = numBeimianyichang;
  }

  public String getNumYanseyichang() {
    return numYanseyichang;
  }

  public void setNumYanseyichang(String numYanseyichang) {
    this.numYanseyichang = numYanseyichang;
  }

  public String getNumYanseyichang2() {
    return numYanseyichang2;
  }

  public void setNumYanseyichang2(String numYanseyichang2) {
    this.numYanseyichang2 = numYanseyichang2;
  }

  public String getNumMo5() {
    return numMo5;
  }

  public void setNumMo5(String numMo5) {
    this.numMo5 = numMo5;
  }

  public String getNumIrev2() {
    return numIrev2;
  }

  public void setNumIrev2(String numIrev2) {
    this.numIrev2 = numIrev2;
  }

  public String getNumRsh() {
    return numRsh;
  }

  public void setNumRsh(String numRsh) {
    this.numRsh = numRsh;
  }

  public String getNumDx() {
    return numDx;
  }

  public void setNumDx(String numDx) {
    this.numDx = numDx;
  }

  public String getNumDuanshan() {
    return numDuanshan;
  }

  public void setNumDuanshan(String numDuanshan) {
    this.numDuanshan = numDuanshan;
  }

  public String getNumHuashang() {
    return numHuashang;
  }

  public void setNumHuashang(String numHuashang) {
    this.numHuashang = numHuashang;
  }

  public String getNumHeidian() {
    return numHeidian;
  }

  public void setNumHeidian(String numHeidian) {
    this.numHeidian = numHeidian;
  }

  public String getNumKexindu() {
    return numKexindu;
  }

  public void setNumKexindu(String numKexindu) {
    this.numKexindu = numKexindu;
  }

  public String getNumYinlie() {
    return numYinlie;
  }

  public void setNumYinlie(String numYinlie) {
    this.numYinlie = numYinlie;
  }

  public String getNumColorAll() {
    return numColorAll;
  }

  public void setNumColorAll(String numColorAll) {
    this.numColorAll = numColorAll;
  }

  public String getNumColorA() {
    return numColorA;
  }

  public void setNumColorA(String numColorA) {
    this.numColorA = numColorA;
  }

  public String getNumColorB() {
    return numColorB;
  }

  public void setNumColorB(String numColorB) {
    this.numColorB = numColorB;
  }

  public String getNumColorC() {
    return numColorC;
  }

  public void setNumColorC(String numColorC) {
    this.numColorC = numColorC;
  }

  public String getNumDsAll() {
    return numDsAll;
  }

  public void setNumDsAll(String numDsAll) {
    this.numDsAll = numDsAll;
  }

  public String getNumDs_0() {
    return numDs_0;
  }

  public void setNumDs_0(String numDs0) {
    this.numDs_0 = numDs0;
  }

  public String getNumDs_1() {
    return numDs_1;
  }

  public void setNumDs_1(String numDs_1) {
    this.numDs_1 = numDs_1;
  }

  public String getNumDs_2() {
    return numDs_2;
  }

  public void setNumDs_2(String numDs2) {
    this.numDs_2 = numDs2;
  }

  public String getNumDs_3() {
    return numDs_3;
  }

  public void setNumDs_3(String numDs_3) {
    this.numDs_3 = numDs_3;
  }

  public String getNumDs_4() {
    return numDs_4;
  }

  public void setNumDs_4(String numDs_4) {
    this.numDs_4 = numDs_4;
  }

  public String getNumDs_5() {
    return numDs_5;
  }

  public void setNumDs_5(String numDs_5) {
    this.numDs_5 = numDs_5;
  }

  public String getNumDs_5p() {
    return numDs_5p;
  }

  public void setNumDs_5p(String numDs_5p) {
    this.numDs_5p = numDs_5p;
  }

  public String getNum213() {
    return num213;
  }

  public void setNum213(String num213) {
    this.num213 = num213;
  }

  public String getNum214() {
    return num214;
  }

  public void setNum214(String num214) {
    this.num214 = num214;
  }

  public String getNum215() {
    return num215;
  }

  public void setNum215(String num215) {
    this.num215 = num215;
  }

  public String getNum216() {
    return num216;
  }

  public void setNum216(String num216) {
    this.num216 = num216;
  }

  public String getNum217() {
    return num217;
  }

  public void setNum217(String num217) {
    this.num217 = num217;
  }

  public String getNum218() {
    return num218;
  }

  public void setNum218(String num218) {
    this.num218 = num218;
  }

  public String getNum219() {
    return num219;
  }

  public void setNum219(String num219) {
    this.num219 = num219;
  }

  public String getNum220() {
    return num220;
  }

  public void setNum220(String num220) {
    this.num220 = num220;
  }

  public String getNum221() {
    return num221;
  }

  public void setNum221(String num221) {
    this.num221 = num221;
  }

  public String getNum222() {
    return num222;
  }

  public void setNum222(String num222) {
    this.num222 = num222;
  }

  public String getNum223() {
    return num223;
  }

  public void setNum223(String num223) {
    this.num223 = num223;
  }

  public String getNum224() {
    return num224;
  }

  public void setNum224(String num224) {
    this.num224 = num224;
  }

  public String getNum225() {
    return num225;
  }

  public void setNum225(String num225) {
    this.num225 = num225;
  }

  public String getNum226() {
    return num226;
  }

  public void setNum226(String num226) {
    this.num226 = num226;
  }

  public String getNum227() {
    return num227;
  }

  public void setNum227(String num227) {
    this.num227 = num227;
  }

  public String getNum228() {
    return num228;
  }

  public void setNum228(String num228) {
    this.num228 = num228;
  }

  public String getNum229() {
    return num229;
  }

  public void setNum229(String num229) {
    this.num229 = num229;
  }

  public String getNum230() {
    return num230;
  }

  public void setNum230(String num230) {
    this.num230 = num230;
  }

  public String getNum231() {
    return num231;
  }

  public void setNum231(String num231) {
    this.num231 = num231;
  }

  public String getNum232() {
    return num232;
  }

  public void setNum232(String num232) {
    this.num232 = num232;
  }

  public String getNum233() {
    return num233;
  }

  public void setNum233(String num233) {
    this.num233 = num233;
  }

  public String getNum234() {
    return num234;
  }

  public void setNum234(String num234) {
    this.num234 = num234;
  }

  public String getNum235() {
    return num235;
  }

  public void setNum235(String num235) {
    this.num235 = num235;
  }

  public String getNum236() {
    return num236;
  }

  public void setNum236(String num236) {
    this.num236 = num236;
  }

  public String getNum237() {
    return num237;
  }

  public void setNum237(String num237) {
    this.num237 = num237;
  }

  public String getNum238() {
    return num238;
  }

  public void setNum238(String num238) {
    this.num238 = num238;
  }

  public String getNum239() {
    return num239;
  }

  public void setNum239(String num239) {
    this.num239 = num239;
  }

  public String getNum240() {
    return num240;
  }

  public void setNum240(String num240) {
    this.num240 = num240;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("EqpCTSource{");
    sb.append("rowkey='").append(rowkey).append('\'');
    sb.append("site='").append(site).append('\'');
    sb.append("factory='").append(factory).append('\'');
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
    sb.append(", output=").append(outputQty);
    sb.append(", output2=").append(outputQty2);
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
    sb.append(", numDs0=").append(numDs_0);
    sb.append(", numDs1=").append(numDs_1);
    sb.append(", numDs2=").append(numDs_2);
    sb.append(", numDs3=").append(numDs_3);
    sb.append(", numDs4=").append(numDs_4);
    sb.append(", numDs5=").append(numDs_5);
    sb.append(", numDs5p=").append(numDs_5p);
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
    Method m = EqpCTSource.class.getDeclaredMethod(String.format("set%s", StringUtils.capitalize("num240")), String.class);
  }
}
