package cn.com.shanghai.xinfusdk_w280p.useunxor.listener;

import cn.com.shanghai.xinfusdk_w280p.modle.DeviceInfo;

/**
 * Created by Administrator on 2016/5/19.
 */
public interface OnGetDeviceInfoListener {
    /**
     * 获取成功的回调
     */
    void onSucc(DeviceInfo deviceInfo);

    /**
     * 设备信息获取失败的回调
     *
     * @param errorMsg 失败信息
     */
    void onError(String errorMsg);
}
