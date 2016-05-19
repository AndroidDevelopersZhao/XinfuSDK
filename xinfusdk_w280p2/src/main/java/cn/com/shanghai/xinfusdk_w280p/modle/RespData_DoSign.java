package cn.com.shanghai.xinfusdk_w280p.modle;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/5/16.
 */
public class RespData_DoSign implements Serializable{

    /**
     * txnSubType : 00
     * makCv : f52a9f5f4464e52e
     * txnType : 99
     * version : 1.0
     * termBn : 30303030303230313531323233303031
     * termSn : 7866303030333431
     * pik : 08a3ea5bea3597df07dddea786d2717f
     * mak : e14cf2cff4d1facf5bb7b4509c2e47d3
     * respMsg : æå
     * termMn : 626A7A66742D6D706F73303335
     * pikCv : 3b955a653c753ca4
     * respCode : 00
     */

    private String txnSubType;
    private String makCv;
    private String txnType;
    private String version;
    private String termBn;
    private String termSn;
    private String pik;
    private String mak;
    private String respMsg;
    private String termMn;
    private String pikCv;
    private String respCode;

    public void setTxnSubType(String txnSubType) {
        this.txnSubType = txnSubType;
    }

    public void setMakCv(String makCv) {
        this.makCv = makCv;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setTermBn(String termBn) {
        this.termBn = termBn;
    }

    public void setTermSn(String termSn) {
        this.termSn = termSn;
    }

    public void setPik(String pik) {
        this.pik = pik;
    }

    public void setMak(String mak) {
        this.mak = mak;
    }

    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg;
    }

    public void setTermMn(String termMn) {
        this.termMn = termMn;
    }

    public void setPikCv(String pikCv) {
        this.pikCv = pikCv;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getTxnSubType() {
        return txnSubType;
    }

    public String getMakCv() {
        return makCv;
    }

    public String getTxnType() {
        return txnType;
    }

    public String getVersion() {
        return version;
    }

    public String getTermBn() {
        return termBn;
    }

    public String getTermSn() {
        return termSn;
    }

    public String getPik() {
        return pik;
    }

    public String getMak() {
        return mak;
    }

    public String getRespMsg() {
        return respMsg;
    }

    public String getTermMn() {
        return termMn;
    }

    public String getPikCv() {
        return pikCv;
    }

    public String getRespCode() {
        return respCode;
    }
}
