package cn.com.shanghai.xinfusdk_w280p.businessprocessing;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
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
import cn.com.shanghai.xinfusdk_w280p.modle.ReqConsumeData;
import cn.com.shanghai.xinfusdk_w280p.modle.RespData_DoSign;
import cn.com.shanghai.xinfusdk_w280p.modle.TransMessage;
import cn.com.shanghai.xinfusdk_w280p.modle.UnConsumeData;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnConsumeListener;
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
    private ReqConsumeData mReqConsumeData;

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
        this.doConsme(consumeData, consumeListener);
    }

    @Override
    public void unConsume(UnConsumeData unConsumeData, OnUnConsumeListener unConsumeListener) {
        this.doUnConsume(unConsumeData, unConsumeListener);
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
     * 消费交易
     *
     * @param consumeData     消费所需参数，详见实体类内部描述
     * @param consumeListener 执行结果回调
     */
    private void doConsme(final ConsumeData consumeData, final OnConsumeListener consumeListener) {
        startTime = thisTime();
        Log.d("start consume...");
        loginDevice();
        mReqConsumeData = new ReqConsumeData();
        Log.d("will read card info,please inser or flush card.");
        CardReaderProviderImpl.readCard(consumeData.getTimeOut_waitCard(), (byte) 0x07, new CardReaderListener() {
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
                        consumeListener.onGetCard();
//                        XXSVProgressHUD.showWithStatus(MainActivity.this, "请在输入密码后点击确定键");
                        PinPadProviderImpl.loginDevice(context);
                        PinPadProviderImpl.initPinpad();
                        PinPadProviderImpl.inputOnlinePin(consumeData.getTimeOut_waitCard(), cardInfo.getCardNo(), Pinpad.KEYOFFSET_PINKEY, 1, new PinPadListener() {
                            @Override
                            public void onInputResult(int i, byte[] bytes) {
                                Log.d("输入完成，" + Utils.byteArratToHexString(bytes));
                                Log.d("start mack consume data and sorp...");
                                byte[] b = creatTransData_Consume_ShuaKa(consumeData, cardInfo, Utils.byteArratToHexString(bytes));
                                String TMK = preferences.get(context, Key.MainKey, "").toString().trim();
                                String macKey_e = preferences.get(context, Key.mackey, "").toString().trim();
                                String macKey_CV = preferences.get(context, Key.mackey_cv, "").toString().trim();
                                Log.d("传入mac的值:\nTMK:" + TMK + "\nmackey:" + macKey_e + "\nmackeyCv:" + macKey_CV);

                                Utils.getMac(TMK, macKey_e, macKey_CV, b, new onGetMac() {
                                    @Override
                                    public void onSucc(final String mac) {
                                        Utils.uploadTransData(context, map, mac.toLowerCase(), "Consume", consumeData.getTimeOut_Internet(), new OnUploadTransDataListener() {
                                            @Override
                                            public void onSucc(TransMessage transMessage) {
                                                if (transMessage.getRespCode().equals("00")) {
                                                    Log.d("consume successful in " + useTime() + " ms.");
                                                    Log.d("consume success,data was resped");
                                                    consumeListener.onSucc(transMessage);
                                                } else {
                                                    Log.e("consume was error," + transMessage.getRespMsg());
                                                    consumeListener.onError(transMessage.getRespMsg());
                                                }
                                            }

                                            @Override
                                            public void onError(String errorMsg) {
                                                Log.e("consume was error:" + errorMsg);
                                                consumeListener.onError(errorMsg);
                                            }
                                        });

                                    }

                                    @Override
                                    public void onError(String errorMsg) {
                                        Log.e("mac cle error:" + errorMsg);
                                        consumeListener.onError(errorMsg);
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
                        doConsume_IC(consumeData,consumeListener,cardInfo);
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
                consumeListener.onError("卡片读取失败");
                Log.e("read cardinfo error,errorcode:" + errorcode + ",cardType:" + cardType);

            }
        });
    }


    /**
     * 根据订单号撤销交易
     *
     * @param unConsumeData     撤销交易所需参数
     * @param unConsumeListener 执行结果监听器
     */
    private void doUnConsume(UnConsumeData unConsumeData, OnUnConsumeListener unConsumeListener) {
        //撤销交易
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
    private static byte[] creatTransData_Consume_ShuaKa(ConsumeData consumeData, CardInfo cardInfo, String pin) {
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
        Log.d("刷卡报文：\nMD5:" + MD5Util.getMD5String(Utils.sortMap(map)) + "\nmap:" + map);
        Log.d("刷卡报文组件成功，开始计算mac");
        return MD5Util.getMD5String(Utils.sortMap(map)).getBytes();
    }

    /**
     * 开始IC卡交易
     *
     * @param cardInfo
     */
    private void doConsume_IC(final ConsumeData consumeData, final OnConsumeListener consumeListener, CardInfo cardInfo) {
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
        transData.setAmount(Utils.getTransAmount(consumeData.getAmount()));
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
                        mReqConsumeData.setCardNo(cardno);
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
                        consumeListener.onGetCard();
                    }

                    @Override
                    public int onOnlineProc(String pinBlock) {

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
                        mReqConsumeData.setICCardData(field55);
                        mReqConsumeData.setICCardSeqNumber(card_seq);
                        mReqConsumeData.setCardExpireDate(cardExpireDate);
                        mReqConsumeData.setPin(pinBlock);
                        mReqConsumeData.setTrack2Data(track_2);
                                //卡片数据获取成功，开始组建报文
                                String data = creatTransData_Consume_ChaKa(consumeData,mReqConsumeData);
                                Log.d("开始计算mac");
                                String TMK = preferences.get(context, Key.MainKey, "").toString().trim();
                                String macKey_e = preferences.get(context, Key.mackey, "").toString().trim();
                                String macKey_CV = preferences.get(context, Key.mackey_cv, "").toString().trim();
                                Log.d("传入mac的值:\nTMK:" + TMK + "\nmackey:" + macKey_e + "\nmackeyCv:" + macKey_CV);
                                Utils.getMac(TMK, macKey_e, macKey_CV, data.getBytes(), new onGetMac() {
                                    @Override
                                    public void onSucc(String mac) {
                                        Log.d("Mac计算成功，" + mac);
                                        Utils.uploadTransData(context, map, mac.toLowerCase(), "Consume", consumeData.getTimeOut_Internet(), new OnUploadTransDataListener() {
                                            @Override
                                            public void onSucc(TransMessage transMessage) {
                                                if (transMessage.getRespCode().equals("00")) {
                                                    Log.d("付款成功");
                                                    consumeListener.onSucc(transMessage);
                                                }else {
                                                    Log.e(transMessage.getRespMsg());
                                                    consumeListener.onError(transMessage.getRespMsg());
                                                }
                                            }

                                            @Override
                                            public void onError(String errorMsg) {
                                                Log.e("上送交易数据失败，errorMsg：" + errorMsg);
                                                consumeListener.onError(errorMsg);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(String errorMsg) {
                                        Log.e("Mac计算失败，" + errorMsg);
                                        consumeListener.onError(errorMsg);
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
    private static String creatTransData_Consume_ChaKa(ConsumeData consumeData,ReqConsumeData chaKaData) {
        map = new TreeMap<>();
        map.put("version", "1.0");    //版本号	version	M	1.0
        map.put("termMn", consumeData.getDeviceInfo().getTermMn());    //终端型号	termMn	M
        map.put("termBn", consumeData.getDeviceInfo().getTermBn());    //终端批号	termBn	M
        map.put("termSn", consumeData.getDeviceInfo().getTermSn());    //终端序列号	termSn	M
        map.put("txnType", "01");    //交易类型	txnType	M	01
        map.put("txnSubType", "00");    //交易子类	txnSubType	M	00
        map.put("merId", consumeData.getDeviceInfo().getMerId());    //商户号	merId	M
        map.put("orderId", Utils.getTransTime(TransType.UnKnow));    //商户订单号	orderId	M	同一商户同一交易日内唯一
        map.put("txnTime",Utils.getSysTime_24());    //订单发送时间	txnTime	M	当前时间，同一终端不应出现相同发送时间的交易请求

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
}
