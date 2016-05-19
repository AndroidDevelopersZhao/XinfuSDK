package cn.com.shanghai.xinfusdk_w280p.useunxor.listener;

/**
 * you can you do.no can no bb.
 * 类说明：    获取M1卡块4秘钥的监听
 * 包名：cn.com.xinfusdk.cardinfo
 * 作者：赵文贇
 * 创建时间:2016/3/18 16:01
 */
public interface OnStartRequestListener {
    /**
     * 成功回调
     *
     * @param data 返回的所有数据
     */
    void Succ(String data);

    /**
     * 错误回调
     *
     * @param errorMsg 错误信息
     */
    void Error(String errorMsg);
}
