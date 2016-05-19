package cn.com.shanghai.xinfusdk_w280p.useunxor.listener;

import cn.com.shanghai.xinfusdk_w280p.modle.TransMessage;

/**
 * Created by Administrator on 2016/5/19.
 */
public interface OnConsumeListener {
    void onSucc(TransMessage transMessage);

    void onGetCard();
    void onError(String errorMsg);
}
