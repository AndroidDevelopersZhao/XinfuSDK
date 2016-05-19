package cn.com.shanghai.xinfu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import cn.com.shanghai.xinfusdk_w280p.XinfuSDK;
import cn.com.shanghai.xinfusdk_w280p.modle.DeviceInfo;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnGetDeviceInfoListener;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnSign;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final XinfuSDK xinfuSDK = XinfuSDK.getInstance(this,true,true);
        xinfuSDK.getDeviceInfo(new OnGetDeviceInfoListener() {
            @Override
            public void onSucc(DeviceInfo deviceInfo) {
                app("sn:" + deviceInfo.getTermSn()
                        + "\nbn:" + deviceInfo.getTermBn()
                        + "\nmn:" + deviceInfo.getTermMn()
                        + "\nmr:" + deviceInfo.getMerId());

                xinfuSDK.doSign(deviceInfo, new OnSign() {
                    @Override
                    public void onSucc() {
                        app("签到成功*****");
                    }

                    @Override
                    public void onError(String errorMsg) {
                        app(errorMsg);

                    }
                });
            }

            @Override
            public void onError(String errorMsg) {
                app(errorMsg);
            }
        });
    }


    private void app(String msg) {
        Log.w("App", msg);
    }
}
