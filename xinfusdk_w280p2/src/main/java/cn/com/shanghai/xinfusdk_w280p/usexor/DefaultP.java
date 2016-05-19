package cn.com.shanghai.xinfusdk_w280p.usexor;

/**
 * Created by Administrator on 2016/5/19.
 */
public class DefaultP {
    public static final String TAG = "library-XinfuSDK_W280p";
    public static boolean isDebug = false;//是否开启日志打印
    public static final String FILE_NAME_DEVICE_INFO_SHAREDPREFERENCE = "SavedDeviceInfo";
    //预设的设备信息
    public static final String termMn = "626A7A66742D6D706F73303335";
    public static final String termBn = "30303030303230313531323233303031";
    public static final String termSn = "7866303030333431";
    public static final String merId = "999310057320031";

    public static boolean isLoadMainKey = false;/*是否导入主密钥*/
    public static final String MainKey = "40224B56F9DF161A6360AAC71593B404";/*16进制主密钥明文 16bytes*/

    public static final String url = "http://psvc-test.ic-pay.com/gateway-mpos/api/mpos.do";/*URL*/
}
