package cn.com.shanghai.xinfu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import cn.com.shanghai.xinfusdk_w280p.XinfuSDK;
import cn.com.shanghai.xinfusdk_w280p.modle.ConsumeData;
import cn.com.shanghai.xinfusdk_w280p.modle.DeviceInfo;
import cn.com.shanghai.xinfusdk_w280p.modle.GetBalanceData;
import cn.com.shanghai.xinfusdk_w280p.modle.TransMessage;
import cn.com.shanghai.xinfusdk_w280p.modle.UnConsumeData;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnConsumeListener;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnGetBalanceListener;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnGetDeviceInfoListener;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnSign;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnUnConsumeListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button getDeviceInfo;
    private Button dosign;
    private Button consume;
    private Button unconsume;
    private Button getbalance;
    private XinfuSDK mXinfuSDK = null;
    private DeviceInfo mDeviceInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mXinfuSDK = XinfuSDK.getInstance(this, true, true);
        initView();
        //git test
    }


    private void app(String msg) {
        Log.w("App", msg);
    }

    private void T(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initView() {
        getDeviceInfo = (Button) findViewById(R.id.getDeviceInfo);
        dosign = (Button) findViewById(R.id.dosign);
        consume = (Button) findViewById(R.id.consume);
        unconsume = (Button) findViewById(R.id.unconsume);
        getbalance = (Button) findViewById(R.id.getbalance);

        getDeviceInfo.setOnClickListener(this);
        dosign.setOnClickListener(this);
        consume.setOnClickListener(this);
        unconsume.setOnClickListener(this);
        getbalance.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getDeviceInfo:
                mXinfuSDK.getDeviceInfo(new OnGetDeviceInfoListener() {
                    @Override
                    public void onSucc(DeviceInfo deviceInfo) {
                        app("设备信息获取成功");
                        T("Success");
                        mDeviceInfo = deviceInfo;
                    }

                    @Override
                    public void onError(String errorMsg) {
                        app("设备信息获取失败");
                        T("Error");
                    }
                });
                break;
            case R.id.dosign:
                mXinfuSDK.doSign(mDeviceInfo, new OnSign() {
                    @Override
                    public void onSucc() {
                        app("签到成功");
                        T("签到成功");
                    }

                    @Override
                    public void onError(String errorMsg) {
                        app("签到失败，" + errorMsg);
                        T("签到失败，" + errorMsg);
                    }
                });
                break;
            case R.id.consume:
                ConsumeData consumeData = new ConsumeData();
                consumeData.setDeviceInfo(mDeviceInfo);
                consumeData.setTimeOut_Internet(20);
                consumeData.setTimeOut_waitCard(20);
                consumeData.setAmount(15.5);
                T("请 刷|插 卡");
                mXinfuSDK.consume(consumeData, new OnConsumeListener() {
                    @Override
                    public void onSucc(TransMessage transMessage) {
                        T("付款成功，QueryId:" + transMessage.getQueryId());
                        app("付款成功，QueryId:" + transMessage.getQueryId());
                    }

                    @Override
                    public void onGetCard() {
                        app("请在终端输入密码后点击确定键");
                        T("请在终端输入密码后点击确定键");
                    }

                    @Override
                    public void onPswInPutDown() {
                        app("正在上送数据，请稍后");
                        T("正在上送数据，请稍后");
                    }

                    @Override
                    public void onError(String errorMsg) {
                        app("付款失败，" + errorMsg);
                        T("付款失败，" + errorMsg);
                    }
                });
                break;
            case R.id.unconsume:
                UnConsumeData data = new UnConsumeData();

                data.setDeviceInfo(mDeviceInfo);
                data.setQueadId("060173160520124447986219420201");
                data.setTimeOut_waitCard(20);
                data.setTimeOut_Internet(20);
                T("请 刷|插 卡");
                mXinfuSDK.unConsume(data, new OnUnConsumeListener() {
                    @Override
                    public void onSucc() {
                        T("撤销成功");
                        app("撤销成功");
                    }

                    @Override
                    public void onGetCard() {
                        app("请在终端输入密码后点击确定键");
                        T("请在终端输入密码后点击确定键");
                    }

                    @Override
                    public void onPswInPutDown() {
                        app("正在上送数据，请稍后");
                        T("正在上送数据，请稍后");
                    }

                    @Override
                    public void onError(String errorMsg) {
                        app("撤销失败，" + errorMsg);
                        T("撤销失败，" + errorMsg);
                    }
                });
                break;
            case R.id.getbalance:
                GetBalanceData getBalanceData = new GetBalanceData();
                getBalanceData.setDeviceInfo(mDeviceInfo);
                getBalanceData.setTimeOut_Internet(20);
                getBalanceData.setTimeOut_waitCard(20);
                T("请 刷|插 卡");
                mXinfuSDK.getBalance(getBalanceData, new OnGetBalanceListener() {
                    @Override
                    public void onSucc(TransMessage transMessage) {
                        T("查余成功，" + transMessage.getBalance());
                        app("查余成功，" + transMessage.getBalance());
                    }

                    @Override
                    public void onGetCard() {
                        app("请在终端输入密码后点击确定键");
                        T("请在终端输入密码后点击确定键");
                    }

                    @Override
                    public void onPswInPutDown() {
                        app("正在上送数据，请稍后");
                        T("正在上送数据，请稍后");
                    }

                    @Override
                    public void onError(String errorMsg) {
                        T("查余失败，" + errorMsg);
                        app("查余失败，" + errorMsg);
                    }
                });
                break;
        }
    }
}
