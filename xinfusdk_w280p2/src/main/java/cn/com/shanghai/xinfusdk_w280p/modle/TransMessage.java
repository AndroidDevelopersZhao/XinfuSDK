package cn.com.shanghai.xinfusdk_w280p.modle;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;


/**
 * you can you do.no can no bb.
 * 类说明: 交易数据实体类
 * 包名：cn.com.xinfusdk.modle
 * 作者：赵文贇
 * 创建时间:2016/3/16 14:05
 */
public class TransMessage implements Parcelable {

    private String version = "";//版本号
    private String mac = "";//报文鉴别码
    private String termMn = "";//终端型号
    private String termBn = "";//终端批号
    private String termSn = "";//终端序列号
    private String txnType = "";//交易类型
    private String txnSubType = "";//交易子类
    private String merId = "";//商户代码
    private String orderId = "";//商户订单号
    private String txnTime = "";//订单发送时间YYYYMMDDhhmmss
    private String txnAmt = "";//交易金额,单位为分
    private String currencyCode = "";//交易币种
    private String orderDesc = "";//订单描述
    private String cardType = "";//卡类型
    private String cardNo = "";//卡号
    private String pin = "";//密码
    private String ICCardData = "";//IC卡数据,55域数据
    private String ICCardSeqNumber = "";//IC卡的序列号
    private String track2Data = "";//第二磁道数据
    private String track3Data = "";//第三磁道数据
    private String respCode = "";//响应码
    private String respMsg = "";//响应码描述
    private String queryId = "";//交易查询流水号
    private String settleDate = "";//清算日期
    private String traceNo = "";//系统跟踪号
    private String origQryId = "";//原始交易流水号
    private String balance = "";//余额 Json格式，需要再次解析
    private String extData = "";//个性化信息 Json格式，需要再次解析
    private String pik = "";//PIN工作密钥
    private String pikCv = "";//PIK校验码
    private String mak = "";//MAC工作密钥
    private String makCv = "";//MAK校验码
    private String origRespCode = "";//原交易应答码
    private String origRespMsg = "";//原交易应答信息
    private String cardExpireDate = "";//卡片效期

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getTermMn() {
        return termMn;
    }

    public void setTermMn(String termMn) {
        this.termMn = termMn;
    }

    public String getTermBn() {
        return termBn;
    }

    public void setTermBn(String termBn) {
        this.termBn = termBn;
    }

    public String getTermSn() {
        return termSn;
    }

    public void setTermSn(String termSn) {
        this.termSn = termSn;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getTxnSubType() {
        return txnSubType;
    }

    public void setTxnSubType(String txnSubType) {
        this.txnSubType = txnSubType;
    }

    public String getMerId() {
        return merId;
    }

    public void setMerId(String merId) {
        this.merId = merId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTxnTime() {
        return txnTime;
    }

    public void setTxnTime(String txnTime) {
        this.txnTime = txnTime;
    }

    public String getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(String txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getOrderDesc() {
        return orderDesc;
    }

    public void setOrderDesc(String orderDesc) {
        this.orderDesc = orderDesc;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

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

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespMsg() {
        return respMsg;
    }

    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public String getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    public String getTraceNo() {
        return traceNo;
    }

    public void setTraceNo(String traceNo) {
        this.traceNo = traceNo;
    }

    public String getOrigQryId() {
        return origQryId;
    }

    public void setOrigQryId(String origQryId) {
        this.origQryId = origQryId;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getExtData() {
        return extData;
    }

    public void setExtData(String extData) {
        this.extData = extData;
    }

    public String getPik() {
        return pik;
    }

    public void setPik(String pik) {
        this.pik = pik;
    }

    public String getPikCv() {
        return pikCv;
    }

    public void setPikCv(String pikCv) {
        this.pikCv = pikCv;
    }

    public String getMak() {
        return mak;
    }

    public void setMak(String mak) {
        this.mak = mak;
    }

    public String getMakCv() {
        return makCv;
    }

    public void setMakCv(String makCv) {
        this.makCv = makCv;
    }

    public String getOrigRespCode() {
        return origRespCode;
    }

    public void setOrigRespCode(String origRespCode) {
        this.origRespCode = origRespCode;
    }

    public String getOrigRespMsg() {
        return origRespMsg;
    }

    public void setOrigRespMsg(String origRespMsg) {
        this.origRespMsg = origRespMsg;
    }

    public String getCardExpireDate() {
        return cardExpireDate;
    }

    public void setCardExpireDate(String cardExpireDate) {
        this.cardExpireDate = cardExpireDate;
    }

    public TransMessage() {
    }

    public TransMessage(Parcel resource) {
        this.version = resource.readString();
        this.mac = resource.readString();
        this.termMn = resource.readString();
        this.termBn = resource.readString();
        this.termSn = resource.readString();
        this.txnType = resource.readString();
        this.txnSubType = resource.readString();
        this.merId = resource.readString();
        this.orderId = resource.readString();
        this.txnTime = resource.readString();
        this.txnAmt = resource.readString();
        this.currencyCode = resource.readString();
        this.orderDesc = resource.readString();
        this.cardType = resource.readString();
        this.cardNo = resource.readString();
        this.pin = resource.readString();
        this.ICCardData = resource.readString();
        this.ICCardSeqNumber = resource.readString();
        this.track2Data = resource.readString();
        this.track3Data = resource.readString();
        this.respCode = resource.readString();
        this.respMsg = resource.readString();
        this.queryId = resource.readString();
        this.settleDate = resource.readString();
        this.traceNo = resource.readString();
        this.origQryId = resource.readString();
        this.balance = resource.readString();
        this.extData = resource.readString();
        this.pik = resource.readString();
        this.pikCv = resource.readString();
        this.mak = resource.readString();
        this.makCv = resource.readString();
        this.origRespCode = resource.readString();
        this.origRespMsg = resource.readString();
        this.cardExpireDate = resource.readString();
    }

    public TransMessage(TransMessage resource) {
        this.version = resource.getVersion();
        this.mac = resource.getMac();
        this.termMn = resource.getTermMn();
        this.termBn = resource.getTermBn();
        this.termSn = resource.getTermSn();
        this.txnType = resource.getTxnType();
        this.txnSubType = resource.getTxnSubType();
        this.merId = resource.getMerId();
        this.orderId = resource.getOrderId();
        this.txnTime = resource.getTxnTime();
        this.txnAmt = resource.getTxnAmt();
        this.currencyCode = resource.getCurrencyCode();
        this.orderDesc = resource.getOrderDesc();
        this.cardType = resource.getCardType();
        this.cardNo = resource.getCardNo();
        this.pin = resource.getPin();
        this.ICCardData = resource.getICCardData();
        this.ICCardSeqNumber = resource.getICCardSeqNumber();
        this.track2Data = resource.getTrack2Data();
        this.track3Data = resource.getTrack3Data();
        this.respCode = resource.getRespCode();
        this.respMsg = resource.getRespMsg();
        this.queryId = resource.getQueryId();
        this.settleDate = resource.getSettleDate();
        this.traceNo = resource.getTraceNo();
        this.origQryId = resource.getOrigQryId();
        this.balance = resource.getBalance();
        this.extData = resource.getExtData();
        this.pik = resource.getPik();
        this.pikCv = resource.getPikCv();
        this.mak = resource.getMak();
        this.makCv = resource.getMakCv();
        this.origRespCode = resource.getOrigRespCode();
        this.origRespMsg = resource.getOrigRespMsg();
        this.cardExpireDate = resource.getCardExpireDate();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(version);
        dest.writeString(mac);
        dest.writeString(termMn);
        dest.writeString(termBn);
        dest.writeString(termSn);
        dest.writeString(txnType);
        dest.writeString(txnSubType);
        dest.writeString(merId);
        dest.writeString(orderId);
        dest.writeString(txnTime);
        dest.writeString(txnAmt);
        dest.writeString(currencyCode);
        dest.writeString(orderDesc);
        dest.writeString(cardType);
        dest.writeString(cardNo);
        dest.writeString(pin);
        dest.writeString(ICCardData);
        dest.writeString(ICCardSeqNumber);
        dest.writeString(track2Data);
        dest.writeString(track3Data);
        dest.writeString(respCode);
        dest.writeString(respMsg);
        dest.writeString(queryId);
        dest.writeString(settleDate);
        dest.writeString(traceNo);
        dest.writeString(origQryId);
        dest.writeString(balance);
        dest.writeString(extData);
        dest.writeString(pik);
        dest.writeString(pikCv);
        dest.writeString(mak);
        dest.writeString(makCv);
        dest.writeString(origRespCode);
        dest.writeString(origRespMsg);
        dest.writeString(cardExpireDate);
    }

    public static final Creator<TransMessage> CREATOR = new Creator<TransMessage>() {
        @Override
        public TransMessage createFromParcel(Parcel source) {
            return new TransMessage(source);
        }

        @Override
        public TransMessage[] newArray(int size) {
            return new TransMessage[size];
        }
    };

    /**
     * 将非空的数据填入请求参数对象中
     *
     * @return 填充结果
     */
    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        if (!version.equals("")) {
            params.put("version", version);
        }
        if (!mac.equals("")) {
            params.put("mac", mac);
        }
        if (!termMn.equals("")) {
            params.put("termMn", termMn);
        }
        if (!termBn.equals("")) {
            params.put("termBn", termBn);
        }
        if (!termSn.equals("")) {
            params.put("termSn", termSn);
        }
        if (!txnType.equals("")) {
            params.put("txnType", txnType);
        }
        if (!txnSubType.equals("")) {
            params.put("txnSubType", txnSubType);
        }
        if (!merId.equals("")) {
            params.put("merId", merId);
        }
        if (!orderId.equals("")) {
            params.put("orderId", orderId);
        }
        if (!txnTime.equals("")) {
            params.put("txnTime", txnTime);
        }
        if (!txnAmt.equals("")) {
            params.put("txnAmt", txnAmt);
        }
        if (!currencyCode.equals("")) {
            params.put("currencyCode", currencyCode);
        }
        if (!orderDesc.equals("")) {
            params.put("orderDesc", orderDesc);
        }
        if (!cardType.equals("")) {
            params.put("cardType", cardType);
        }
        if (!cardNo.equals("")) {
            params.put("cardNo", cardNo);
        }
        if (!pin.equals("")) {
            params.put("pin", pin);
        }
        if (!ICCardData.equals("")) {
            params.put("ICCardData", ICCardData);
        }
        if (!ICCardSeqNumber.equals("")) {
            params.put("ICCardSeqNumber", ICCardSeqNumber);
        }
        if (!track2Data.equals("")) {
            params.put("track2Data", track2Data);
        }
        if (!track3Data.equals("")) {
            params.put("track3Data", track3Data);
        }
        if (!respCode.equals("")) {
            params.put("respCode", respCode);
        }
        if (!respMsg.equals("")) {
            params.put("respMsg", respMsg);
        }
        if (!queryId.equals("")) {
            params.put("queryId", queryId);
        }
        if (!settleDate.equals("")) {
            params.put("settleDate", settleDate);
        }
        if (!traceNo.equals("")) {
            params.put("traceNo", traceNo);
        }
        if (!origQryId.equals("")) {
            params.put("origQryId", origQryId);
        }
        if (!balance.equals("")) {
            params.put("balance", balance);
        }
        if (!extData.equals("")) {
            params.put("extData", extData);
        }
        if (!pik.equals("")) {
            params.put("pik", pik);
        }
        if (!pikCv.equals("")) {
            params.put("pikCv", pikCv);
        }
        if (!mak.equals("")) {
            params.put("mak", mak);
        }
        if (!makCv.equals("")) {
            params.put("makCv", makCv);
        }
        if (!origRespCode.equals("")) {
            params.put("origRespCode", origRespCode);
        }
        if (!origRespMsg.equals("")) {
            params.put("origRespMsg", origRespMsg);
        }
        if (!cardExpireDate.equals("")) {
            params.put("cardExpireDate", cardExpireDate);
        }
        return params;
    }

    @Override
    public String toString() {
        return "TransMessage{" +
                "version='" + version + '\'' +
                ", mac='" + mac + '\'' +
                ", termMn='" + termMn + '\'' +
                ", termBn='" + termBn + '\'' +
                ", termSn='" + termSn + '\'' +
                ", txnType='" + txnType + '\'' +
                ", txnSubType='" + txnSubType + '\'' +
                ", merId='" + merId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", txnTime='" + txnTime + '\'' +
                ", txnAmt='" + txnAmt + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", orderDesc='" + orderDesc + '\'' +
                ", cardType='" + cardType + '\'' +
                ", cardNo='" + cardNo + '\'' +
                ", pin='" + pin + '\'' +
                ", ICCardData='" + ICCardData + '\'' +
                ", ICCardSeqNumber='" + ICCardSeqNumber + '\'' +
                ", track2Data='" + track2Data + '\'' +
                ", track3Data='" + track3Data + '\'' +
                ", respCode='" + respCode + '\'' +
                ", respMsg='" + respMsg + '\'' +
                ", queryId='" + queryId + '\'' +
                ", settleDate='" + settleDate + '\'' +
                ", traceNo='" + traceNo + '\'' +
                ", origQryId='" + origQryId + '\'' +
                ", balance='" + balance + '\'' +
                ", extData='" + extData + '\'' +
                ", pik='" + pik + '\'' +
                ", pikCv='" + pikCv + '\'' +
                ", mak='" + mak + '\'' +
                ", makCv='" + makCv + '\'' +
                ", origRespCode='" + origRespCode + '\'' +
                ", origRespMsg='" + origRespMsg + '\'' +
                ", cardExpireDate='" + cardExpireDate + '\'' +
                '}';
    }
}
