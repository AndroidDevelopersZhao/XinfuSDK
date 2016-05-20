package cn.com.shanghai.xinfusdk_w280p.modle;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/5/19.
 */
public class ReqICCardData implements Serializable {
    private String cardNo = null;
    private String pin = null;
    private String ICCardData = null;//55
    private String ICCardSeqNumber = null;//序列号
    private String track2Data = null;
    private String track3Data = null;
    private String cardExpireDate = null;//有效期

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getICCardData() {
        return ICCardData;
    }

    public void setICCardData(String ICCardData) {
        this.ICCardData = ICCardData;
    }

    public String getICCardSeqNumber() {
        return ICCardSeqNumber;
    }

    public void setICCardSeqNumber(String ICCardSeqNumber) {
        this.ICCardSeqNumber = ICCardSeqNumber;
    }

    public String getTrack2Data() {
        return track2Data;
    }

    public void setTrack2Data(String track2Data) {
        this.track2Data = track2Data;
    }

    public String getTrack3Data() {
        return track3Data;
    }

    public void setTrack3Data(String track3Data) {
        this.track3Data = track3Data;
    }

    public String getCardExpireDate() {
        return cardExpireDate;
    }

    public void setCardExpireDate(String cardExpireDate) {
        this.cardExpireDate = cardExpireDate;
    }
}
