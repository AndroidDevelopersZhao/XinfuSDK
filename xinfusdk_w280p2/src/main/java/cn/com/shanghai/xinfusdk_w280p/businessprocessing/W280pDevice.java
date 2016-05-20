package cn.com.shanghai.xinfusdk_w280p.businessprocessing;

import android.content.Context;
import android.text.format.Time;

import com.google.gson.Gson;
import com.landicorp.android.eptandapi.pinpad.Pinpad;
import com.landicorp.android.eptapi.DeviceService;
import com.landicorp.android.eptapi.emv.process.data.TransactionData;
import com.landicorp.android.eptapi.exception.ReloginException;
import com.landicorp.android.eptapi.exception.RequestException;
import com.landicorp.android.eptapi.exception.ServiceOccupiedException;
import com.landicorp.android.eptapi.exception.UnsupportMultiProcess;
import com.landicorp.android.eptapi.utils.BytesUtil;
import com.landicorp.emv.EmvChannel;
import com.landicorp.emv.EmvConstant;
import com.landicorp.emv.EmvTransData;
import com.landicorp.entity.CardInfo;
import com.landicorp.entity.KeyInfo;
import com.landicorp.entity.PinPadConstant;
import com.landicorp.impl.CardReaderProviderImpl;
import com.landicorp.impl.EmvProviderImpl;
import com.landicorp.impl.PinPadProviderImpl;
import com.landicorp.listener.CardReaderListener;
import com.landicorp.listener.EmvTransListener;
import com.landicorp.listener.PinPadListener;
import com.landicorp.util.ByteUtils;
import com.landicorp.util.MakeField55;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.com.shanghai.xinfusdk_w280p.modle.ConsumeData;
import cn.com.shanghai.xinfusdk_w280p.modle.DeviceInfo;
import cn.com.shanghai.xinfusdk_w280p.modle.GetBalanceData;
import cn.com.shanghai.xinfusdk_w280p.modle.ReqICCardData;
import cn.com.shanghai.xinfusdk_w280p.modle.RespData_DoSign;
import cn.com.shanghai.xinfusdk_w280p.modle.TransMessage;
import cn.com.shanghai.xinfusdk_w280p.modle.UnConsumeData;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnConsumeListener;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnGetBalanceListener;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnGetDeviceInfoListener;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnSign;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnStartRequestListener;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnUnConsumeListener;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnUploadTransDataListener;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.onGetMac;
import cn.com.shanghai.xinfusdk_w280p.usexor.DefaultP;
import cn.com.shanghai.xinfusdk_w280p.usexor.SDK;
import cn.com.shanghai.xinfusdk_w280p.usexor.TransType;
import cn.com.shanghai.xinfusdk_w280p.utils.EmvParameterUtil;
import cn.com.shanghai.xinfusdk_w280p.utils.KernelConstant;
import cn.com.shanghai.xinfusdk_w280p.utils.Key;
import cn.com.shanghai.xinfusdk_w280p.utils.Log;
import cn.com.shanghai.xinfusdk_w280p.utils.MD5Util;
import cn.com.shanghai.xinfusdk_w280p.utils.Utils;
import cn.com.shanghai.xinfusdk_w280p.utils.XXSharedPreferences;

/**
 * Created by Administrator on 2016/5/19.
 */
public class W280pDevice implements SDK {
    private static TreeMap<String, String> map;
    private Context context = null;
    private long startTime = 0L;
    private XXSharedPreferences preferences = null;
    private ReqICCardData mReqIcCardData;
    private TransType transType = TransType.UnKnow;//交易类型

    public W280pDevice(Context context) {
        this.context = context;
        PinPadProviderImpl.loginDevice(context);
        PinPadProviderImpl.initPinpad();
        preferences = new XXSharedPreferences(DefaultP.FILE_NAME_DEVICE_INFO_SHAREDPREFERENCE);
        preferences.put(context, Key.termSn, DefaultP.termSn);
        preferences.put(context, Key.termBn, DefaultP.termBn);
        preferences.put(context, Key.termMn, DefaultP.termMn);
        preferences.put(context, Key.merId, DefaultP.merId);
        if (DefaultP.isLoadMainKey) {
            loadMainKey();
        }
        Log.d("device info was load success.W280pDevice init ok");
    }

    /**
     * 获取设备信息
     *
     * @param deviceInfoListener 执行结果回调
     */
    @Override
    public void getDeviceInfo(OnGetDeviceInfoListener deviceInfoListener) {
        this.GetDeviceInfo(deviceInfoListener);
    }

    /**
     * 签到
     *
     * @param deviceInfo 设备信息
     * @param sign       执行结果回调
     */
    @Override
    public void doSign(DeviceInfo deviceInfo, OnSign sign) {
        this.Dosign(deviceInfo, sign);
    }

    @Override
    public void consume(ConsumeData consumeData, OnConsumeListener consumeListener) {
        this.transType = TransType.Consume;
        Log.d("transType updata to " + transType);
        this.readCard(consumeData, consumeListener);
    }

    @Override
    public void unConsume(UnConsumeData unConsumeData, OnUnConsumeListener unConsumeListener) {
        this.transType = TransType.UnConsume;
        Log.d("transType updata to " + transType);
        this.readCard(unConsumeData, unConsumeListener);
    }

    @Override
    public void getBalance(GetBalanceData getBalanceData, OnGetBalanceListener getBalanceListener) {
        this.transType = TransType.GetBalanc;
        Log.d("transType updata to " + transType);
        this.readCard(getBalanceData, getBalanceListener);
    }


    /**********************************************************************************************/
    /*****************************************以下为业务处理部分***************************************/
    /**********************************************************************************************/


    private void GetDeviceInfo(OnGetDeviceInfoListener deviceInfoListener) {
        startTime = thisTime();
        Log.d("start get device info...");
        if (preferences == null) {
            this.preferences = new XXSharedPreferences(DefaultP.FILE_NAME_DEVICE_INFO_SHAREDPREFERENCE);
        }
        try {
            String sn = preferences.get(context, Key.termSn, "").toString().trim();
            String bn = preferences.get(context, Key.termBn, "").toString().trim();
            String mn = preferences.get(context, Key.termMn, "").toString().trim();
            String mr = preferences.get(context, Key.merId, "").toString().trim();
            Log.d("get device info success in " + useTime() + " ms.");
            deviceInfoListener.onSucc(new DeviceInfo(sn, bn, mn, mr));
        } catch (NullPointerException ex) {
            deviceInfoListener.onError("设备信息获取失败，请确认该终端是否已成功灌装");
            Log.e("从共享参数读取的设备信息数据为空（至少有一项为空）");
        }
    }

    private void Dosign(DeviceInfo deviceInfo, final OnSign sign) {
        startTime = thisTime();
        Log.d("start do sign...");
        Map<String, String> map = new HashMap<>();
        map.put("version", "1.0");
        map.put("termMn", deviceInfo.getTermMn());
        map.put("termBn", deviceInfo.getTermBn());
        map.put("termSn", deviceInfo.getTermSn());
        map.put("txnType", "99");
        map.put("txnSubType", "00");
        map.put("signMode", "UMS8");

        Utils.StartRequest(context, map, DefaultP.url, "doSign", new OnStartRequestListener() {
            @Override
            public void Succ(String data) {
                Log.d("doSign to service back data:" + data);
                if (data.equals("")) {
                    Log.e("签到失败，后台返回空字符串");
                    sign.onError("签到失败，后台返回空字符串");
                    return;
                }
                RespData_DoSign data_doSign = new Gson().fromJson(data, RespData_DoSign.class);
                if (data_doSign.getRespCode().equals("00")) {
                    //签到成功
                    Log.d("dosign to service success in " + (new Date().getTime() - startTime) + " ms");
                    long startLoadPinKeyTime = new Date().getTime();

                    String pinKey = data_doSign.getPik();
                    String pinCheckValue = data_doSign.getPikCv().substring(0, 8);
                    String macKey = data_doSign.getMak();
                    String macCheckValue = data_doSign.getMakCv().substring(0, 8);
                    Log.d("start loading pinkey\n pik:" + pinKey + "\npikCv:" + pinCheckValue);

                    KeyInfo info = new KeyInfo();
                    info.setEncrypted(true);
                    info.setKeyData(ByteUtils.hexString2ByteArray(pinKey));
                    info.setKcvValue(ByteUtils.hexString2ByteArray(pinCheckValue));
                    info.setKcvValueLen(ByteUtils.hexString2ByteArray(pinCheckValue).length);
                    info.setmKeyIndex(Pinpad.KEYOFFSET_MAINKEY);
                    info.setKeyType(PinPadConstant.KEYTYPE_PINKEY);
                    info.setwKeyIndex(Pinpad.KEYOFFSET_PINKEY);
                    info.setKeyDataLen(16);
                    int pinRet = PinPadProviderImpl.loadKey(info);
                    if (pinRet == 0) {
                        Log.d("load pinkey to device success in " + (new Date().getTime() - startLoadPinKeyTime) + " ms");
                        Log.d("start loading mackey\n mak:" + macKey + "\nmacCv:" + macCheckValue);
                        long startLoadMacKeyTime = new Date().getTime();
                        info.setKeyData(ByteUtils.hexString2ByteArray(macKey));
                        info.setKeyType(PinPadConstant.KEYTYPE_MACKEY);
                        info.setKcvValue(ByteUtils.hexString2ByteArray(macCheckValue));
                        info.setKcvValueLen(ByteUtils.hexString2ByteArray(macCheckValue).length);
                        info.setKeyDataLen(8);
                        info.setwKeyIndex(Pinpad.KEYOFFSET_MACKEY);
                        int macRet = PinPadProviderImpl.loadKey(info);
                        if (macRet == 0) {
                            Log.d("load mackey to device success in " + (new Date().getTime() - startLoadMacKeyTime) + " ms");
                            if (preferences == null) {
                                preferences = new XXSharedPreferences(DefaultP.FILE_NAME_DEVICE_INFO_SHAREDPREFERENCE);
                            }
                            preferences.put(context, Key.mackey, macKey);
                            preferences.put(context, Key.mackey_cv, macCheckValue);
                            Log.d("doSign success in " + useTime() + " ms");
                            sign.onSucc();
                        } else {
                            Log.e("load mackey to device error");
                            sign.onError("load mackey to device error");
                        }
                    } else {
                        sign.onError("load pinkey to device error");
                        Log.e("load pinkey to device error");
                    }


                } else {
                    //签到失败
                    sign.onError(data_doSign.getRespMsg());
                    Log.e("doSign to service error," + data_doSign.getRespMsg());
                }
            }

            @Override
            public void Error(String errorMsg) {
                Log.e("doSign to service error," + errorMsg);
                sign.onError(errorMsg);
                //签到失败
            }
        });
    }


    /**
     * 读取卡片（每种交易前必须做的事）
     *
     * @param transdata
     * @param tranceListener
     */
    private void readCard(final Object transdata, final Object tranceListener) {
        startTime = thisTime();
        switch (this.transType) {
            case Consume:
                Log.d("start consume...");
                break;
            case UnConsume:
                Log.d("start unConsume...");
                break;

            case GetBalanc:
                Log.d("start getBalance...");
                break;
        }
        loginDevice();
        mReqIcCardData = new ReqICCardData();
        Log.d("will read card info,please inser or flush card.");
        CardReaderProviderImpl.readCard(20, (byte) 0x07, new CardReaderListener() {
            @Override
            public void onReadCardSucc(final CardInfo cardInfo) {
                Log.d("read cardinfo success,cardInfo:" + cardInfo);
                switch (cardInfo.getCardType()) {

                    case CardInfo.CARD_MAG:
                        //磁条卡
                        Log.d("检测到磁条卡");
                        Log.d("cardInfo-CardType:" + cardInfo.getCardType());
                        Log.d("cardInfo-CardNo:" + cardInfo.getCardNo());
                        Log.d("cardInfo-ExpDate:" + cardInfo.getExpDate());
                        Log.d("cardInfo-ServiceCode:" + cardInfo.getServiceCode());
                        Log.d("cardInfo-Track:" + cardInfo.getTrack());
                        Log.d("cardInfo-Track1:" + cardInfo.getTrack1());
                        Log.d("cardInfo-Track2:" + cardInfo.getTrack2());
                        Log.d("cardInfo-Track3:" + cardInfo.getTrack3());
                        int timeOut = -1;
                        switch (transType) {
                            case Consume:
                                ((OnConsumeListener) tranceListener).onGetCard();
                                timeOut = ((ConsumeData) transdata).getTimeOut_waitCard();
                                break;
                            case UnConsume:
                                ((OnUnConsumeListener) tranceListener).onGetCard();
                                timeOut = ((UnConsumeData) transdata).getTimeOut_waitCard();
                                break;

                            case GetBalanc:
                                ((OnGetBalanceListener) tranceListener).onGetCard();
                                timeOut = ((GetBalanceData) transdata).getTimeOut_waitCard();
                                break;
                        }

                        PinPadProviderImpl.loginDevice(context);
                        PinPadProviderImpl.initPinpad();
                        final int finalTimeOut = timeOut;
                        PinPadProviderImpl.inputOnlinePin(timeOut > 0 ? timeOut : 20, cardInfo.getCardNo(), Pinpad.KEYOFFSET_PINKEY, 1, new PinPadListener() {
                            @Override
                            public void onInputResult(int i, byte[] bytes) {
                                switch (transType) {
                                    case Consume:
                                        ((OnConsumeListener) tranceListener).onPswInPutDown();
                                        break;

                                    case UnConsume:
                                        ((OnUnConsumeListener) tranceListener).onPswInPutDown();
                                        break;
                                    case GetBalanc:
                                        ((OnGetBalanceListener) tranceListener).onPswInPutDown();
                                        break;
                                }
                                Log.d("输入完成，" + Utils.byteArratToHexString(bytes));
                                Log.d("start make data and sorp...");
                                String data_afterMD5 = null;
                                switch (transType) {
                                    case Consume:
                                        data_afterMD5 = creatTransData_Consume_ShuaKa((ConsumeData) transdata, cardInfo, Utils.byteArratToHexString(bytes));
                                        Log.d("md5-consume data :" + data_afterMD5);
                                        break;
                                    case UnConsume:
                                        data_afterMD5 = creatTransData_unConsume_ShuaKa((UnConsumeData) transdata, cardInfo, Utils.byteArratToHexString(bytes));
                                        Log.d("md5-unConsume data :" + data_afterMD5);
                                        break;

                                    case GetBalanc:
                                        data_afterMD5 = creatTransData_getBalance_ShuaKa((GetBalanceData) transdata, cardInfo, Utils.byteArratToHexString(bytes));
                                        Log.d("md5-get balance data :" + data_afterMD5);
                                        break;
                                }

                                String TMK = preferences.get(context, Key.MainKey, "").toString().trim();
                                String macKey_e = preferences.get(context, Key.mackey, "").toString().trim();
                                String macKey_CV = preferences.get(context, Key.mackey_cv, "").toString().trim();
                                Log.d("传入mac的值:\nTMK:" + TMK + "\nmackey:" + macKey_e + "\nmackeyCv:" + macKey_CV);
                                Log.d("start get mac");
                                Utils.getMac(TMK, macKey_e, macKey_CV, data_afterMD5.getBytes(), new onGetMac() {
                                    @Override
                                    public void onSucc(final String mac) {
                                        Log.d("get mac success,mac:" + mac);
                                        String Tag = null;
                                        if (transType == TransType.Consume) {
                                            Tag = "Consume";
                                        }
                                        if (transType == TransType.UnConsume) {
                                            Tag = "unConsume";
                                        }
                                        if (transType == TransType.GetBalanc) {
                                            Tag = "GetBalance";
                                        }

                                        Utils.uploadTransData(context, map, mac.toLowerCase(), Tag, finalTimeOut, new OnUploadTransDataListener() {
                                            @Override
                                            public void onSucc(TransMessage transMessage) {

                                                if (transMessage.getRespCode().equals("00")) {
                                                    Log.d("trance successful in " + useTime() + " ms.");
                                                    switch (transType) {
                                                        case Consume:
                                                            ((OnConsumeListener) tranceListener).onSucc(transMessage);
                                                            break;

                                                        case UnConsume:
                                                            ((OnUnConsumeListener) tranceListener).onSucc();
                                                            break;

                                                        case GetBalanc:
                                                            ((OnGetBalanceListener) tranceListener).onSucc(transMessage);
                                                            break;
                                                    }


                                                } else {
                                                    Log.e("trance was error," + transMessage.getRespMsg());
                                                    switch (transType) {
                                                        case Consume:
                                                            ((OnConsumeListener) tranceListener).onError(transMessage.getRespMsg());
                                                            break;

                                                        case UnConsume:
                                                            ((OnUnConsumeListener) tranceListener).onError(transMessage.getRespMsg());

                                                            break;

                                                        case GetBalanc:
                                                            ((OnGetBalanceListener) tranceListener).onError(transMessage.getRespMsg());
                                                            break;
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onError(String errorMsg) {
                                                switch (transType) {
                                                    case Consume:
                                                        Log.e("consume was error:" + errorMsg);
                                                        ((OnConsumeListener) tranceListener).onError(errorMsg);
                                                        break;

                                                    case UnConsume:
                                                        Log.e("unconsume was error:" + errorMsg);
                                                        ((OnUnConsumeListener) tranceListener).onError(errorMsg);
                                                        break;

                                                    case GetBalanc:
                                                        Log.e("get balance was error:" + errorMsg);
                                                        ((OnGetBalanceListener) tranceListener).onError(errorMsg);
                                                        break;
                                                }

                                            }
                                        });

                                    }

                                    @Override
                                    public void onError(String errorMsg) {
                                        Log.e("mac cle error:" + errorMsg);
                                        switch (transType) {
                                            case Consume:
                                                ((OnConsumeListener) tranceListener).onError(errorMsg);
                                                break;

                                            case UnConsume:
                                                ((OnUnConsumeListener) tranceListener).onError(errorMsg);
                                                break;

                                            case GetBalanc:
                                                ((OnGetBalanceListener) tranceListener).onError(errorMsg);
                                                break;
                                        }

                                    }
                                });


                            }

                            @Override
                            public void onSendKey(int i, int i1) {
                                Log.d("正在输入..." + i + "," + i1);
                            }
                        });
//
                        break;
                    case CardInfo.CARD_INSERT:
                        //IC卡
                        Log.d("cardInfo-CardType:" + cardInfo.getCardType());
                        Log.d("cardInfo-CardNo:" + cardInfo.getCardNo());
                        Log.d("cardInfo-ExpDate:" + cardInfo.getExpDate());
                        Log.d("cardInfo-ServiceCode:" + cardInfo.getServiceCode());
                        Log.d("cardInfo-Track:" + cardInfo.getTrack());
                        Log.d("cardInfo-Track1:" + cardInfo.getTrack1());
                        Log.d("cardInfo-Track2:" + cardInfo.getTrack2());
                        Log.d("cardInfo-Track3:" + cardInfo.getTrack3());
                        doIC_EMV(transdata, tranceListener, cardInfo);
                        break;

                    case CardInfo.CARD_CLSS:
                        //非接卡
                        Log.d("检测到非接卡");
                        break;
                    case CardInfo.CARD_UNKNOW:
                        // 未知
                        Log.d("unknow card type");
                        break;
                }

            }

            @Override
            public void onReadCardErr(int errorcode, int cardType) {
                CardReaderProviderImpl.stopReadCard();

                switch (transType) {
                    case Consume:
                        ((OnConsumeListener) tranceListener).onError("卡片读取失败");
                        break;

                    case UnConsume:
                        ((OnUnConsumeListener) tranceListener).onError("卡片读取失败");
                        break;

                    case GetBalanc:

                        break;
                }

                Log.e("read cardinfo error,errorcode:" + errorcode + ",cardType:" + cardType);

            }
        });
    }
    /************************************************************************************************/
    /********************************************工具部分**********************************************/
    /************************************************************************************************/

    private long thisTime() {
        return new Date().getTime();
    }

    private long useTime() {
        return (new Date().getTime() - startTime);
    }


    private void loadMainKey() {
        byte[] plainKey = ByteUtils.hexString2ByteArray(DefaultP.MainKey);
        KeyInfo keyInfo = new KeyInfo();
        keyInfo.setEncrypted(false);
        keyInfo.setKcvValue(null);
        keyInfo.setKcvValueLen(0);
        keyInfo.setKeyType(PinPadConstant.KEYTYPE_MKEY);
        keyInfo.setKeyData(plainKey);
        keyInfo.setKeyDataLen(16);
        keyInfo.setmKeyIndex(Pinpad.KEYOFFSET_MAINKEY);
        int pinpadret = PinPadProviderImpl.loadKey(keyInfo);
        if (pinpadret == 0) {
            Log.d("mainkey load success");
            if (preferences == null) {
                preferences = new XXSharedPreferences(DefaultP.FILE_NAME_DEVICE_INFO_SHAREDPREFERENCE);
            }
            preferences.put(context, Key.MainKey, DefaultP.MainKey);
        } else {
            Log.e("load mainkey was error");
        }
    }

    private void loginDevice() {
        try {
            DeviceService.login(context);
        } catch (RequestException e) {
            e.printStackTrace();
        } catch (ServiceOccupiedException e) {
            e.printStackTrace();
        } catch (ReloginException e) {
            e.printStackTrace();
        } catch (UnsupportMultiProcess e) {
            e.printStackTrace();
        }
    }


    /**
     * 组件刷卡消费报文
     *
     * @param cardInfo 刷卡后终端回调数据的实体对象
     */
    private static String creatTransData_Consume_ShuaKa(ConsumeData consumeData, CardInfo cardInfo, String pin) {
        map = new TreeMap<>();

        map.put("version", "1.0");    //版本号	version	M	1.0
        map.put("termMn", consumeData.getDeviceInfo().getTermMn());    //终端型号	termMn	M
        map.put("termBn", consumeData.getDeviceInfo().getTermBn());    //终端批号	termBn	M
        map.put("termSn", consumeData.getDeviceInfo().getTermSn());    //终端序列号	termSn	M
        map.put("txnType", "01");    //交易类型	txnType	M	01
        map.put("txnSubType", "00");    //交易子类	txnSubType	M	00
        map.put("merId", consumeData.getDeviceInfo().getMerId());    //商户号	merId	M
        map.put("orderId", Utils.getTransTime(TransType.UnKnow));    //商户订单号	orderId	M	同一商户同一交易日内唯一
        map.put("txnTime", Utils.getSysTime_24());    //订单发送时间	txnTime	M	当前时间，同一终端不应出现相同发送时间的交易请求
        map.put("txnAmt", Utils.getTransAmount(consumeData.getAmount()));    //交易金额	txnAmt	M
        map.put("currencyCode", "156");    //交易币种	currencyCode	M
//        if (consumeModle.getOrderMsg() != null && !consumeModle.getOrderMsg().equals("")) {
//            map.put("orderDesc", consumeModle.getOrderMsg());    //订单描述	orderDesc	O
//        }

        map.put("cardType", "001");    //卡类型	cardType	M
        map.put("cardNo", cardInfo.getCardNo());    //卡号	cardNo	M
        map.put("pin", pin);    //密码	pin	C	有输入密码时必传
        String t2 = cardInfo.getTrack2().replace("=", "d");
        if (t2.length() == 38) {
            //第二磁道数据	track2Data	C
            map.put("track2Data", t2.substring(0, t2.length() - 1));//2磁
        } else {
            map.put("track2Data", t2);//2磁
        }
        if (cardInfo.getTrack3() != null && !cardInfo.getTrack3().equals("")) {
            map.put("track3Data", cardInfo.getTrack3());    //第三磁道数据	track3Data	C	磁条卡交易时必填，可获取时必填
        }
        return MD5Util.getMD5String(Utils.sortMap(map));
    }

    /**
     * 开始IC卡交易
     */
    private void doIC_EMV(final Object trancedata, final Object tranceListener, CardInfo cardinfo) {
        //初始化交易流程
        String sn = android.os.Build.SERIAL;// 终端序列号
        EmvParameterUtil.getInstance().initEmvParameter(context, sn,
                "000000000000000", "00000000");// 终端序列号、商户名、商户号(商户名、商户号需要根据实际参数设置)
        EmvParameterUtil.getInstance().initICParam(KernelConstant.aid);
        EmvParameterUtil.getInstance().initICPublicKey(KernelConstant.pk);

        // 初始化IC卡交易终端参数
        EmvProviderImpl.initEmvtermConfig();
        EmvProviderImpl.setmContext(context);
        final EmvTransData transData = new EmvTransData();
        transData.setAmount("10000");
        Time tm = new Time();
        tm.setToNow();
        String date = String.format("%04d", tm.year)
                + String.format("%02d", tm.month + 1)
                + String.format("%02d", tm.monthDay);
        String time = String.format("%02d", tm.hour)
                + String.format("%02d", tm.minute)
                + String.format("%02d", tm.second);
        transData.setTransDate(date);
        transData.setTransTime(time);
        transData.setTransNo("000123"); // 交易流水号（注：根据交易具体参数设置）
        transData.setTag9Value((byte) 0x00);
        transData.setCardAuth(true);
        transData.setForceOnline(true);
        transData.setSupportSM(false);
        transData.setSupportEC(false);
        transData.setSupportCvm(true);
        // channel type 需要统一定义
        transData.setChannel(EmvChannel.ICC);
        // emvFlow 需要统一定义
        transData.setFlow(EmvConstant.FLOW.COMPLETE);

        PinPadProviderImpl.initPinpad();
        EmvProviderImpl.process(transData,
                new EmvTransListener() {
                    @Override
                    public int onWaitAppSelect(List<String> list, boolean b) {
                        Log.d("onWaitAppSelect");
                        return 0;
                    }

                    @Override
                    public int onConfirmCardNo(String cardno) {
                        Log.d("onConfirmCardNo");
                        mReqIcCardData.setCardNo(cardno);
                        // 获取2磁道信息
                        byte[] track2 = EmvProviderImpl.getTlv(0x57);
                        String track_2 = BytesUtil.bytes2HexString(track2);
                        // 获取卡片序列号
                        byte[] cardSeq = EmvProviderImpl.getTlv(0x5f34);
                        String card_seq = BytesUtil.bytes2HexString(cardSeq);
                        // IC卡序列号长度为3
                        int length = card_seq.length();
                        if (length < 3) {
                            for (int i = 0; i < 3 - length; i++) {
                                card_seq = "0" + card_seq;
                            }
                        }
                        Log.d("cardno:" + cardno);
                        Log.d("track_2:" + track_2);
                        Log.d("card_seq:" + card_seq);
                        return 0;
                    }

                    @Override
                    public int onCertVerfiy(String s, String s1) {
                        Log.d("onCertVerfiy");
                        return 0;
                    }

                    @Override
                    public void onCardHolderPwd(boolean b, int i) {
                        Log.d("onCardHolderPwd");
                        switch (transType) {
                            case Consume:
                                ((OnConsumeListener) tranceListener).onGetCard();
                                break;

                            case UnConsume:
                                ((OnUnConsumeListener) tranceListener).onGetCard();
                                break;
                            case GetBalanc:
                                ((OnGetBalanceListener) tranceListener).onGetCard();
                                break;
                        }
                    }

                    @Override
                    public int onOnlineProc(String pinBlock) {
                        switch (transType) {
                            case Consume:
                                ((OnConsumeListener) tranceListener).onPswInPutDown();
                                break;

                            case UnConsume:
                                ((OnUnConsumeListener) tranceListener).onPswInPutDown();
                                break;

                            case GetBalanc:
                                ((OnGetBalanceListener) tranceListener).onPswInPutDown();
                                break;
                        }
                        Log.d("onOnlineProc");
                        // 此处可获取IC卡数据做联机、联机成功则返0 emv流程继续 失败则返回非0 内核自动结束流程

                        // 获取2磁道信息
                        byte[] track2 = EmvProviderImpl.getTlv(0x57);
                        String track_2 = BytesUtil.bytes2HexString(track2);
                        // 获取卡片序列号
                        byte[] cardSeq = EmvProviderImpl.getTlv(0x5f34);
                        String card_seq = BytesUtil.bytes2HexString(cardSeq);
                        // IC卡序列号长度为3
                        int length = card_seq.length();
                        if (length < 3) {
                            for (int i = 0; i < 3 - length; i++) {
                                card_seq = "0" + card_seq;
                            }
                        }

                        String field55 = MakeField55.getField55();
//                            append_d();
                        Log.d("-----onOnlineProc-----\n" + "等效二磁道：" + track_2
                                + "\n卡片序列号:" + card_seq + "\n" + "55域数据:" + field55 + "\n");
                        // IC插卡有效期直接通过tag取值
                        byte[] cardValidTime = EmvProviderImpl.getTlv(0x5F24);
                        String cardExpireDate = ByteUtils.toBCDString(cardValidTime)
                                .substring(0, 4);
                        Log.d("cardExpireDate:" + cardExpireDate);
                        Log.d("pinBlock：" + pinBlock);
                        mReqIcCardData.setICCardData(field55);
                        mReqIcCardData.setICCardSeqNumber(card_seq);
                        mReqIcCardData.setCardExpireDate(cardExpireDate);
                        mReqIcCardData.setPin(pinBlock);
                        mReqIcCardData.setTrack2Data(track_2);
                        //卡片数据获取成功，开始组建报文
                        String data_md5 = null;
                        switch (transType) {

                            case Consume:
                                data_md5 = creatTransData_Consume_ChaKa(((ConsumeData) trancedata), mReqIcCardData);
                                Log.d("consume data-md5:" + data_md5);
                                break;

                            case UnConsume:
                                data_md5 = creatTransData_unConsume_ChaKa(((UnConsumeData) trancedata), mReqIcCardData);
                                Log.d("uncosume data-md5:" + data_md5);
                                break;

                            case GetBalanc:
                                data_md5 = creatTransData_getBalance_ChaKa(((GetBalanceData) trancedata), mReqIcCardData);
                                Log.d("get balance data-md5:" + data_md5);
                                break;
                        }
                        String TMK = preferences.get(context, Key.MainKey, "").toString().trim();
                        String macKey_e = preferences.get(context, Key.mackey, "").toString().trim();
                        String macKey_CV = preferences.get(context, Key.mackey_cv, "").toString().trim();
                        Log.d("传入mac的值:\nTMK:" + TMK + "\nmackey:" + macKey_e + "\nmackeyCv:" + macKey_CV);
                        Log.d("start get mac");
                        Utils.getMac(TMK, macKey_e, macKey_CV, data_md5.getBytes(), new onGetMac() {
                            @Override
                            public void onSucc(String mac) {
                                Log.d("get mac success，mac:" + mac);
                                int timeOut = -1;
                                String Tag = null;
                                if (transType == TransType.Consume) {
                                    Tag = "Consume";
                                }
                                if (transType == TransType.UnConsume) {
                                    Tag = "unConsume";
                                }
                                if (transType == TransType.GetBalanc) {
                                    Tag = "GetBalance";
                                }
                                Utils.uploadTransData(context, map, mac.toLowerCase(), Tag, timeOut > 0 ? timeOut : 20, new OnUploadTransDataListener() {
                                    @Override
                                    public void onSucc(TransMessage transMessage) {
                                        if (transMessage.getRespCode().equals("00")) {
                                            Log.d("trance success in " + useTime() + " ms");
                                            switch (transType) {
                                                case Consume:
                                                    ((OnConsumeListener) tranceListener).onSucc(transMessage);
                                                    break;

                                                case UnConsume:
                                                    ((OnUnConsumeListener) tranceListener).onSucc();
                                                    break;

                                                case GetBalanc:
                                                    ((OnGetBalanceListener) tranceListener).onSucc(transMessage);
                                                    break;
                                            }
                                        } else {
                                            Log.e(transMessage.getRespMsg());
                                            switch (transType) {
                                                case Consume:
                                                    ((OnConsumeListener) tranceListener).onError(transMessage.getRespMsg());
                                                    break;

                                                case UnConsume:
                                                    ((OnUnConsumeListener) tranceListener).onError(transMessage.getRespMsg());
                                                    break;

                                                case GetBalanc:
                                                    ((OnGetBalanceListener) tranceListener).onError(transMessage.getRespMsg());
                                                    break;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(String errorMsg) {
                                        Log.e("上送交易数据失败，errorMsg：" + errorMsg);
                                        switch (transType) {
                                            case Consume:
                                                ((OnConsumeListener) tranceListener).onError(errorMsg);
                                                break;

                                            case UnConsume:
                                                ((OnUnConsumeListener) tranceListener).onError(errorMsg);
                                                break;

                                            case GetBalanc:
                                                ((OnGetBalanceListener) tranceListener).onError(errorMsg);
                                                break;
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onError(String errorMsg) {
                                Log.e("Mac计算失败，" + errorMsg);
                                switch (transType) {
                                    case Consume:
                                        ((OnConsumeListener) tranceListener).onError(errorMsg);
                                        break;

                                    case UnConsume:
                                        ((OnUnConsumeListener) tranceListener).onError(errorMsg);
                                        break;

                                    case GetBalanc:
                                        ((OnGetBalanceListener) tranceListener).onError(errorMsg);
                                        break;
                                }
                            }
                        });
                        return 0;
                    }

                    @Override
                    public void onShowMessage(String s) {
                        Log.d("onShowMessage");

                    }

                    @Override
                    public void onSendKey(int i) {
                        Log.d("onSendKey");

                    }

                    @Override
                    public void onEndProcess(int i, TransactionData transactionData) {
                        Log.d("onEndProcess");

                    }

                    @Override
                    public void onGetEcBance(String s) {
                        Log.d("onGetEcBance");

                    }
                });
    }


    /**
     * 组建插卡消费报文
     *
     * @param chaKaData 插卡后终端回调数据的实体对象
     * @return md5摘要后的byte
     */
    private static String creatTransData_Consume_ChaKa(ConsumeData consumeData, ReqICCardData chaKaData) {
        map = new TreeMap<>();
        map.put("version", "1.0");    //版本号	version	M	1.0
        map.put("termMn", consumeData.getDeviceInfo().getTermMn());    //终端型号	termMn	M
        map.put("termBn", consumeData.getDeviceInfo().getTermBn());    //终端批号	termBn	M
        map.put("termSn", consumeData.getDeviceInfo().getTermSn());    //终端序列号	termSn	M
        map.put("txnType", "01");    //交易类型	txnType	M	01
        map.put("txnSubType", "00");    //交易子类	txnSubType	M	00
        map.put("merId", consumeData.getDeviceInfo().getMerId());    //商户号	merId	M
        map.put("orderId", Utils.getTransTime(TransType.UnKnow));    //商户订单号	orderId	M	同一商户同一交易日内唯一
        map.put("txnTime", Utils.getSysTime_24());    //订单发送时间	txnTime	M	当前时间，同一终端不应出现相同发送时间的交易请求

        map.put("txnAmt", Utils.getTransAmount(consumeData.getAmount()));    //交易金额	txnAmt	M
        map.put("currencyCode", "156");    //交易币种	currencyCode	M
//        if (consumeModle.getOrderMsg() != null && !consumeModle.getOrderMsg().equals("")) {
//            map.put("orderDesc", consumeModle.getOrderMsg());    //订单描述	orderDesc	O
//        }
        map.put("cardType", "002");    //卡类型	cardType	M
        map.put("cardNo", chaKaData.getCardNo());    //卡号	cardNo	M
        map.put("pin", chaKaData.getPin());    //密码	pin	C	有输入密码时必传
        map.put("ICCardData", chaKaData.getICCardData());    //IC卡数据	ICCardData	C	CUPS 55域信息，IC卡交易时必传
        map.put("ICCardSeqNumber", chaKaData.getICCardSeqNumber());    //IC卡的序列号	ICCardSeqNumber	C
        if (chaKaData.getTrack2Data().length() == 38) {
            //第二磁道数据	track2Data	C
            map.put("track2Data", chaKaData.getTrack2Data().substring(0, chaKaData.getTrack2Data().length() - 1));//2磁
        } else {
            map.put("track2Data", chaKaData.getTrack2Data());//2磁
        }
//        map.put("", "");    //第三磁道数据	track3Data	C	磁条卡交易时必填，可获取时必填
        map.put("cardExpireDate", chaKaData.getCardExpireDate());    //卡片效期	cardExpireDate	C	若可获取时必传
        //排序
        return MD5Util.getMD5String(Utils.sortMap(map));
    }

    /**
     * 组建刷卡撤销交易报文
     *
     * @return md5摘要后的byte
     */
    private static String creatTransData_unConsume_ShuaKa(UnConsumeData unConsumeData, CardInfo cardInfo, String password) {
        map = new TreeMap<>();
        map.put("version", "1.0");    //版本号	version	M	1.0
        //  报文鉴别码	mac	M
        map.put("termMn", unConsumeData.getDeviceInfo().getTermMn());    //终端型号	termMn	M
        map.put("termBn", unConsumeData.getDeviceInfo().getTermBn());    //终端批号	termBn	M
        map.put("termSn", unConsumeData.getDeviceInfo().getTermSn());    //终端序列号	termSn	M
        map.put("txnType", "31");    //交易类型	txnType	M	31
        map.put("txnSubType", "00");    //交易子类	txnSubType	M	00
        map.put("merId", unConsumeData.getDeviceInfo().getMerId());    //商户号	merId	M
        map.put("orderId", Utils.getTransTime(TransType.UnKnow));    //商户订单号	orderId	M	同一商户同一交易日内唯一
        map.put("txnTime", Utils.getSysTime_24());    //订单发送时间	txnTime	M	当前时间，同一终端不应出现相同发送时间的交易请求
        map.put("origQryId", unConsumeData.getQueadId());
        map.put("pin", password);    //密码	pin	C	有输入密码时必传
        String t2 = cardInfo.getTrack2().replace("=", "d");
        if (t2.length() == 38) {
            //第二磁道数据	track2Data	C
            map.put("track2Data", t2.substring(0, t2.length() - 1));//2磁
        } else {
            map.put("track2Data", t2);//2磁
        }
        if (cardInfo.getTrack3() != null && !cardInfo.getTrack3().equals("")) {
            map.put("track3Data", cardInfo.getTrack3());//3磁
        }
        return MD5Util.getMD5String(Utils.sortMap(map));
    }


    /**
     * 组建刷卡查余报文
     *
     * @return
     */
    private static String creatTransData_getBalance_ShuaKa(GetBalanceData getBalanceData
            , CardInfo cardInfo, String password) {
        map = new TreeMap<String, String>();
        map.put("version", "1.0");
        map.put("termMn", getBalanceData.getDeviceInfo().getTermMn());
        map.put("termBn", getBalanceData.getDeviceInfo().getTermBn());
        map.put("termSn", getBalanceData.getDeviceInfo().getTermSn());
        map.put("txnType", "71");
        map.put("txnSubType", "00");
        map.put("merId", getBalanceData.getDeviceInfo().getMerId());
        map.put("orderId", Utils.getTransTime(TransType.UnKnow));
        map.put("txnTime", Utils.getSysTime_24());
        map.put("cardType", "002");
        map.put("cardNo", cardInfo.getCardNo());
        map.put("pin", password);
//                map.put("pin", s);
//        map.put("ICCardData", s1);
//        map.put("ICCardSeqNumber", s4);
//        如果2磁是38位时减去最后一位
        String t2 = cardInfo.getTrack2().replace("=", "d");
        if (t2.length() == 38) {
            //第二磁道数据	track2Data	C
            map.put("track2Data", t2.substring(0, t2.length() - 1));//2磁
        } else {
            map.put("track2Data", t2);//2磁
        }
        if (cardInfo.getTrack3() != null && !cardInfo.getTrack3().equals("")) {
            map.put("track3Data", cardInfo.getTrack3());
        }

        //排序
        String conn1 = Utils.sortMap(map);
        return MD5Util.getMD5String(conn1);
    }

    /**
     * 组建插卡撤销交易报文
     *
     * @param chaKaData 插卡接口回调的数据
     * @return md5摘要后的byte
     */
    private static String creatTransData_unConsume_ChaKa(UnConsumeData unConsumeData, ReqICCardData chaKaData) {

        map = new TreeMap<String, String>();
        //上送报文
        map.put("version", "1.0");    //版本号	version	M	1.0
        //  报文鉴别码	mac	M
        map.put("termMn", unConsumeData.getDeviceInfo().getTermMn());    //终端型号	termMn	M
        map.put("termBn", unConsumeData.getDeviceInfo().getTermBn());    //终端批号	termBn	M
        map.put("termSn", unConsumeData.getDeviceInfo().getTermSn());    //终端序列号	termSn	M
        map.put("txnType", "31");    //交易类型	txnType	M	31
        map.put("txnSubType", "00");    //交易子类	txnSubType	M	00
        map.put("merId", unConsumeData.getDeviceInfo().getMerId());    //商户号	merId	M
        map.put("orderId", Utils.getTransTime(TransType.UnKnow));    //商户订单号	orderId	M	同一商户同一交易日内唯一
        map.put("txnTime", Utils.getSysTime_24());    //订单发送时间	txnTime	M	当前时间，同一终端不应出现相同发送时间的交易请求
        map.put("origQryId", unConsumeData.getQueadId());
        map.put("pin", chaKaData.getPin());    //密码	pin	C	有输入密码时必传
        map.put("ICCardData", chaKaData.getICCardData());    //IC卡数据	ICCardData	C	CUPS 55域信息，IC卡交易时必传
        map.put("ICCardSeqNumber", chaKaData.getICCardSeqNumber());    //IC卡的序列号	ICCardSeqNumber	C

        if (chaKaData.getTrack2Data().length() == 38) {
            //第二磁道数据	track2Data	C
            map.put("track2Data", chaKaData.getTrack2Data().substring(0, chaKaData.getTrack2Data().length() - 1));//2磁
        } else {
            map.put("track2Data", chaKaData.getTrack2Data());//2磁
        }
        map.put("cardExpireDate", chaKaData.getCardExpireDate());    //卡片效期	cardExpireDate	C	若可获取时必传
        //排序
        String conn1 = Utils.sortMap(map);
        return MD5Util.getMD5String(conn1);
    }

    /**
     * 插卡查余报文组建
     *
     * @param chaKaData
     * @return
     */
    private static String creatTransData_getBalance_ChaKa(GetBalanceData unConsumeData, ReqICCardData chaKaData) {
        map = new TreeMap<>();
        map.put("version", "1.0");
        map.put("termMn", unConsumeData.getDeviceInfo().getTermMn());
        map.put("termBn", unConsumeData.getDeviceInfo().getTermBn());
        map.put("termSn", unConsumeData.getDeviceInfo().getTermSn());
        map.put("txnType", "71");
        map.put("txnSubType", "00");
        map.put("merId", unConsumeData.getDeviceInfo().getMerId());
        map.put("orderId", Utils.getTransTime(TransType.UnKnow));
        map.put("txnTime", Utils.getSysTime_24());
        map.put("cardType", "002");
        map.put("cardNo", chaKaData.getCardNo());
        map.put("pin", chaKaData.getPin());
        map.put("ICCardData", chaKaData.getICCardData());
        map.put("ICCardSeqNumber", chaKaData.getICCardSeqNumber());
        //如果2磁是38位时减去最后一位
        if (chaKaData.getTrack2Data().length() == 38) {
            map.put("track2Data", chaKaData.getTrack2Data().substring(0, chaKaData.getTrack2Data().length() - 1));
        } else {
            map.put("track2Data", chaKaData.getTrack2Data());
        }
//            map.put("track3Data","");
        map.put("cardExpireDate", chaKaData.getCardExpireDate());

        //排序
        String conn1 = Utils.sortMap(map);
        Log.d("插卡查余传入getMac的参数（Md5摘要后）：" + MD5Util.getMD5String(conn1));
        return MD5Util.getMD5String(conn1);
    }
}
