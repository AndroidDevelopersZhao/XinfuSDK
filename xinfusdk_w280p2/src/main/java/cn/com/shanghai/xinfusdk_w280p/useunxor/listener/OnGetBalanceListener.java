package cn.com.shanghai.xinfusdk_w280p.useunxor.listener;

import cn.com.shanghai.xinfusdk_w280p.modle.TransMessage;

/**
 * Created by Administrator on 2016/5/20.
 */
public interface OnGetBalanceListener {
    void onSucc(TransMessage transMessage);

    void onGetCard();

    void onPswInPutDown();

    void onError(String errorMsg);
}
