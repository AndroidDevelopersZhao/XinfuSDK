package cn.com.shanghai.xinfusdk_w280p.modle;

import java.io.Serializable;

import cn.com.shanghai.xinfusdk_w280p.usexor.SDK;

/**
 * Created by Administrator on 2016/5/19.
 */
public class DeviceInfo implements Serializable {

    private String termSn;
    private String termBn;
    private String termMn;
    private String merId;


    public DeviceInfo(){

    }
    public DeviceInfo(String termSn, String termBn, String termMn, String merId) {
        this.termSn = termSn;
        this.termBn = termBn;
        this.termMn = termMn;
        this.merId = merId;
    }

    public void setTermSn(String termSn) {
        this.termSn = termSn;
    }

    public void setTermBn(String termBn) {
        this.termBn = termBn;
    }

    public void setTermMn(String termMn) {
        this.termMn = termMn;
    }

    public void setMerId(String merId) {
        this.merId = merId;
    }

    public String getTermSn() {
        return termSn;
    }

    public String getTermBn() {
        return termBn;
    }

    public String getTermMn() {
        return termMn;
    }

    public String getMerId() {
        return merId;
    }
}
