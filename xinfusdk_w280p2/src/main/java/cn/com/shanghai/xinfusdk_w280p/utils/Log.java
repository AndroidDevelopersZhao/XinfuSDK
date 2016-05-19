package cn.com.shanghai.xinfusdk_w280p.utils;

import cn.com.shanghai.xinfusdk_w280p.usexor.DefaultP;

/**
 * Created by Administrator on 2016/5/19.
 */
public class Log {
    public static final void d(String msg) {
        if (DefaultP.isDebug)
            android.util.Log.d(DefaultP.TAG, msg);
    }

    public static final void e(String msg) {
        if (DefaultP.isDebug)
            android.util.Log.e(DefaultP.TAG, msg);
    }
}
