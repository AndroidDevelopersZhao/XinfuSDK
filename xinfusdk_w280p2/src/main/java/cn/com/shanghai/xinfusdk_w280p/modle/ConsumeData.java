package cn.com.shanghai.xinfusdk_w280p.modle;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/5/19.
 */
public class ConsumeData implements Serializable {
    private String amount = null;/*消费金额，单位：元*/
    private DeviceInfo mDeviceInfo = null;/*设备信息*/
    private int timeOut_Internet = 20;/*网络超时时间*/
    private int timeOut_waitCard = 20;/*等待客户刷卡的超时时间*/


    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public DeviceInfo getDeviceInfo() {
        return mDeviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        mDeviceInfo = deviceInfo;
    }

    public int getTimeOut_Internet() {
        return timeOut_Internet;
    }

    public void setTimeOut_Internet(int timeOut_Internet) {
        this.timeOut_Internet = timeOut_Internet;
    }

    public int getTimeOut_waitCard() {
        return timeOut_waitCard;
    }

    public void setTimeOut_waitCard(int timeOut_waitCard) {
        this.timeOut_waitCard = timeOut_waitCard;
    }
}
