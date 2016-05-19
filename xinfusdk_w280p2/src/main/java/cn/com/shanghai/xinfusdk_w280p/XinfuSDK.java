package cn.com.shanghai.xinfusdk_w280p;

import android.content.Context;

import cn.com.shanghai.xinfusdk_w280p.businessprocessing.W280pDevice;
import cn.com.shanghai.xinfusdk_w280p.modle.ConsumeData;
import cn.com.shanghai.xinfusdk_w280p.modle.DeviceInfo;
import cn.com.shanghai.xinfusdk_w280p.modle.UnConsumeData;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnConsumeListener;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnGetDeviceInfoListener;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnSign;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnUnConsumeListener;
import cn.com.shanghai.xinfusdk_w280p.usexor.DefaultP;
import cn.com.shanghai.xinfusdk_w280p.usexor.SDK;
import cn.com.shanghai.xinfusdk_w280p.utils.Log;

/**
 * Created by Administrator on 2016/5/19.
 */
public class XinfuSDK {
    private Context context = null;
    private static XinfuSDK xinfuSDK = null;
    private static SDK sdk = null;

    private XinfuSDK(Context c) {
        this.context = c;
        Log.d("XinfuSDK init ok");
    }

    public static XinfuSDK getInstance(Context context, boolean isDebug, boolean isLoadMainKey) {
        DefaultP.isDebug = isDebug;
        DefaultP.isLoadMainKey = isLoadMainKey;
        if (xinfuSDK == null) {
            xinfuSDK = new XinfuSDK(context);
            sdk = new W280pDevice(context);
        }
        return xinfuSDK;
    }

    public static XinfuSDK getInstance(Context context, boolean isDebug) {
        DefaultP.isDebug = isDebug;
        if (xinfuSDK == null) {
            xinfuSDK = new XinfuSDK(context);
            sdk = new W280pDevice(context);
        }
        return xinfuSDK;
    }

    public static XinfuSDK getInstance(Context context) {
        if (xinfuSDK == null) {
            xinfuSDK = new XinfuSDK(context);
            sdk = new W280pDevice(context);
        }
        return xinfuSDK;
    }

    /**
     * 获取设备信息
     *
     * @param deviceInfoListener 执行结果的回调
     */
    public void getDeviceInfo(OnGetDeviceInfoListener deviceInfoListener) {
        sdk.getDeviceInfo(deviceInfoListener);
    }

    /**
     * 设备签到，每天至少签到一次
     *
     * @param deviceInfo 设备信息
     * @param sign       执行结果回调
     */
    public void doSign(DeviceInfo deviceInfo, OnSign sign) {
        if (deviceInfo == null) {
            sign.onError("传入设备信息为空");
            return;
        }
        sdk.doSign(deviceInfo, sign);
    }

    /**
     * 消费交易
     *
     * @param consumeData     消费所需参数，详见实体类内部描述
     * @param consumeListener 执行结果回调
     */
    public void consume(ConsumeData consumeData, OnConsumeListener consumeListener) {
        if (consumeData == null) {
            consumeListener.onError("所需交易参数不能位空");
            return;
        }
        sdk.consume(consumeData, consumeListener);
    }

    /**
     * 根据订单号撤销交易
     *
     * @param unConsumeData     撤销交易所需参数
     * @param unConsumeListener 执行结果监听器
     */
    public void unConsume(UnConsumeData unConsumeData, OnUnConsumeListener unConsumeListener) {
        if (unConsumeData == null) {
            unConsumeListener.onError("撤销交易所需参数不能为空");
            return;
        }
        sdk.unConsume(unConsumeData,unConsumeListener);
    }
}
