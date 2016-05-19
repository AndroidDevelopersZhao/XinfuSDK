package cn.com.shanghai.xinfu;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import cn.com.shanghai.xinfusdk_w280p.XinfuSDK;
import cn.com.shanghai.xinfusdk_w280p.modle.ConsumeData;
import cn.com.shanghai.xinfusdk_w280p.modle.DeviceInfo;
import cn.com.shanghai.xinfusdk_w280p.modle.TransMessage;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnConsumeListener;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnGetDeviceInfoListener;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnSign;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final XinfuSDK xinfuSDK = XinfuSDK.getInstance(this, true, true);
        xinfuSDK.getDeviceInfo(new OnGetDeviceInfoListener() {
            @Override
            public void onSucc(final DeviceInfo deviceInfo) {
                app("sn:" + deviceInfo.getTermSn()
                        + "\nbn:" + deviceInfo.getTermBn()
                        + "\nmn:" + deviceInfo.getTermMn()
                        + "\nmr:" + deviceInfo.getMerId());
                xinfuSDK.doSign(deviceInfo, new OnSign() {
                    @Override
                    public void onSucc() {
                        app("签到成功");
                        app("请刷卡");
                        ConsumeData consumeData = new ConsumeData();
                        consumeData.setAmount(10.5);
                        consumeData.setDeviceInfo(deviceInfo);
                        consumeData.setTimeOut_Internet(20);
                        consumeData.setTimeOut_waitCard(20);
                        xinfuSDK.consume(consumeData, new OnConsumeListener() {
                            @Override
                            public void onSucc(TransMessage transMessage) {
                                app("消费成功,quid:" + transMessage.getQueryId());
                                //060173160519175344192056044201 fl
                                //060173160519182653833081114101

                                //060173160519181136138061769601 in
                                //060173160519182746974096123301 in
                            }

                            @Override
                            public void onGetCard() {
                                app("提示用户输入密码");
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
