package cn.com.shanghai.xinfusdk_w280p.useunxor.listener;


import cn.com.shanghai.xinfusdk_w280p.modle.TransMessage;

/**
 * you can you do.no can no bb.
 * 类说明: 返回Utils类请求的消费结果
 * 包名：cn.com.xinfusdk.sdk
 * 作者：赵文贇
 * 创建时间:2016/3/16 17:13
 */
public interface OnUploadTransDataListener {
    /**
     * 后台有数据成功返回时调用
     *
     * @param transMessage 交易实体类
     */
    void onSucc(TransMessage transMessage);

    /**
     * 错误回调
     *
     * @param errorMsg 错误信息
     */
    void onError(String errorMsg);
}
