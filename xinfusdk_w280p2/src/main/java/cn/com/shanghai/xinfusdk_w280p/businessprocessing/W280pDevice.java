package cn.com.shanghai.xinfusdk_w280p.businessprocessing;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;

import com.google.gson.Gson;
import com.landicorp.android.eptandapi.pinpad.Pinpad;
import com.landicorp.entity.KeyInfo;
import com.landicorp.entity.PinPadConstant;
import com.landicorp.impl.PinPadProviderImpl;
import com.landicorp.util.ByteUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.com.shanghai.xinfusdk_w280p.modle.ConsumeData;
import cn.com.shanghai.xinfusdk_w280p.modle.DeviceInfo;
import cn.com.shanghai.xinfusdk_w280p.modle.RespData_DoSign;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnConsumeListener;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnGetDeviceInfoListener;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnSign;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnStartRequestListener;
import cn.com.shanghai.xinfusdk_w280p.usexor.DefaultP;
import cn.com.shanghai.xinfusdk_w280p.usexor.SDK;
import cn.com.shanghai.xinfusdk_w280p.utils.Key;
import cn.com.shanghai.xinfusdk_w280p.utils.Log;
import cn.com.shanghai.xinfusdk_w280p.utils.Utils;
import cn.com.shanghai.xinfusdk_w280p.utils.XXSharedPreferences;

/**
 * Created by Administrator on 2016/5/19.
 */
public class W280pDevice implements SDK {
    private Context context = null;
    private long startTime = 0L;
    private XXSharedPreferences preferences = null;

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
        this.doConsme(consumeData,consumeListener);
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

    private void doConsme(ConsumeData consumeData, OnConsumeListener consumeListener) {

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
        } else {
            Log.e("load mainkey was error");
        }
    }
}
