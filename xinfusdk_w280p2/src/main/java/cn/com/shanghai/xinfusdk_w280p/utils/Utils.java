package cn.com.shanghai.xinfusdk_w280p.utils;

import android.content.Context;
import android.text.format.Time;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import cn.com.shanghai.xinfusdk_w280p.modle.TransMessage;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnStartRequestListener;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnUploadTransDataListener;
import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.onGetMac;
import cn.com.shanghai.xinfusdk_w280p.usexor.DefaultP;
import cn.com.shanghai.xinfusdk_w280p.usexor.TransType;

/**
 * Created by Administrator on 2016/5/19.
 */
public class Utils {

    private static RequestQueue queue;

    /**
     * 网络请求工具(默认超时时间20秒)
     *
     * @param context  上下文
     * @param map      上传参数
     * @param url      请求的URL
     * @param TAG      请求标签
     * @param listener 请求结果监听器
     */
    public static void StartRequest(Context context, final Map<String, String> map,
                                    String url, final String TAG,
                                    final OnStartRequestListener listener) {
        final StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //请求成功
                Log.d("网络请求成功返回" + TAG + "的数据:\n" + s);
                listener.Succ(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //请求失败
                Log.e("请求" + TAG + "网络异常");
                listener.Error("网络异常");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //上送的参数
                Log.d("上送的" + TAG + "参数：\n" + map);
                return map;
            }
        };
        request.setTag(TAG);
        //当前签到超时时间为20秒，默认超时后重试一次
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        RequestQueue queue = getHttpQueue(context);
        queue.add(request);
        queue.start();

    }

    public static RequestQueue getHttpQueue(Context context) {

        if (queue == null) {
            queue = Volley.newRequestQueue(context);
        }
        return queue;
    }

    /**
     * 将字节数组转换成十六进制字符串
     */
    public static String byteArratToHexString(byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }
        return toHexString(data, 0, data.length);
    }

    /**
     * 将字节数组中的指定字节转换成十六进制字符串
     */
    public static String toHexString(byte[] data, int offset, int length) {
        if (data == null || data.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(toHexString(data[offset + i]));
        }
        return sb.toString();
    }

    /**
     * 十六进制输出byte，每个byte两个字符，不足时前补0
     */
    public static final String toHexString(byte b) {
        String s = Integer.toHexString(b & 0xFF).toUpperCase();
        if (s.length() == 1) {
            s = "0" + s; // 补齐成两个字符
        }
        return s;
    }


    /**
     * 将16进制字符串转为byte【】
     *
     * @param hexStr
     * @return
     */
    public static byte[] hexStringToByteArray(String hexStr) {
        if (hexStr == null) {
            return null;
        } else if (hexStr.length() % 2 != 0) {
            return null;
        } else {
            byte[] data = new byte[hexStr.length() / 2];

            for (int i = 0; i < hexStr.length() / 2; ++i) {
                char hc = hexStr.charAt(2 * i);
                char lc = hexStr.charAt(2 * i + 1);
                byte hb = hexChar2Byte(hc);
                byte lb = hexChar2Byte(lc);
                if (hb < 0 || lb < 0) {
                    return null;
                }

                int n = hb << 4;
                data[i] = (byte) (n + lb);
            }

            return data;
        }
    }

    public static byte hexChar2Byte(char c) {
        return c >= 48 && c <= 57 ? (byte) (c - 48) : (c >= 97 && c <= 102 ? (byte) (c - 97 + 10) : (c >= 65 && c <= 70 ? (byte) (c - 65 + 10) : -1));
    }


    /**
     * 获取交易所需时间
     *
     * @param transType 交易类型枚举类
     * @return 时间字符串
     */
    public static String getTransTime(TransType transType) {
        if (transType == null) throw new NullPointerException("获取交易时间时传入的交易类型不可为空");
        String time = null;
        switch (transType) {
            case Consume:
                time = new SimpleDateFormat("yyMMddhhmmss").format(new Date());
                break;
            case UnConsume:
                time = new SimpleDateFormat("yyMMddhhmmss").format(new Date());
                break;
            case GetBalanc:
                time = new SimpleDateFormat("yyMMddhhmmss").format(new Date());
                break;
            case UnKnow:
                Log.d("当前毫秒数");
                time = String.valueOf(new Date().getTime());
                break;
            default:
                Log.d("默认返回当前毫秒数");
                time = String.valueOf(new Date().getTime());
                break;
        }
        return time;
    }


    /**
     * 获取24小时制当前时间
     *
     * @return 字符串
     */
    public static String getSysTime_24() {
        Time t = new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。

        t.setToNow(); // 取得系统时间。

        int year = t.year;
        int month = (t.month) + 1;
        String Month = null;
        if (month < 10) {
            Month = new StringBuffer().append(0).append(month).toString();
        } else {
            Month = new StringBuffer().append(month).toString();
        }

        int date = t.monthDay;
        String Date = null;
        if (date < 10) {
            Date = new StringBuffer().append(0).append(date).toString();
        } else {
            Date = new StringBuffer().append(date).toString();
        }
        int hour = t.hour; // 0-23

        String Hour = null;
        if (hour < 10) {
            Hour = new StringBuffer().append(0).append(hour).toString();
        } else {
            Hour = new StringBuffer().append(hour).toString();
        }


        int minute = t.minute;
        String Minute = null;
        if (minute < 10) {
            Minute = new StringBuffer().append(0).append(minute).toString();
        } else {
            Minute = new StringBuffer().append(minute).toString();
        }
        int second = t.second;
        String Second = null;
        if (second < 10) {
            Second = new StringBuffer().append(0).append(second).toString();
        } else {
            Second = new StringBuffer().append(second).toString();
        }
        //2016 03 16 15 08 059
        StringBuffer sb = new StringBuffer();
        sb.append(year).append(Month).append(Date).append(Hour).append(Minute).append(Second);


        return sb.toString();
    }

    /**
     * 集合排序，拼接Mac时用到
     *
     * @param map 传入Map
     * @return 排序后的Map字符串
     */
    public static String sortMap(Map<String, String> map) {
        String[] keys = (String[]) map.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        StringBuilder raw = new StringBuilder();
        String[] var7 = keys;
        int var6 = keys.length;

        String con;
        for (int var5 = 0; var5 < var6; ++var5) {
            con = var7[var5];
            raw.append(con).append("=").append((String) map.get(con)).append("&");
        }

        con = raw.deleteCharAt(raw.length() - 1).toString();
        return con;
    }


    /**
     * 上送交易数据报文
     *
     * @param context                   上下文
     * @param map                       map集合
     * @param mac                       计算后的mac
     * @param tag                       请求标签
     * @param timeOutM                  超时时间-秒
     * @param onUploadTransDataListener 状态监听
     */
    public static synchronized void uploadTransData(Context context, final TreeMap<String, String> map,
                                                    final String mac, final String tag, int timeOutM,
                                                    final OnUploadTransDataListener onUploadTransDataListener) {
        StringRequest request = new StringRequest(Request.Method.POST,
                DefaultP.url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //成功返回
                Log.d("上送的" + tag + "数据返回信息：" + s);
                TransMessage transMessage = new Gson().fromJson(s, TransMessage.class);
                Log.d("RespCode:" + transMessage.getRespCode() + "------respMsg:" + transMessage.getRespMsg());
                if (transMessage.getRespCode().equals("00")) {
                    onUploadTransDataListener.onSucc(transMessage);
                    //mac校验
                } else {
                    onUploadTransDataListener.onError(transMessage.getRespMsg());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //失败回调
                Log.d(volleyError + "");
                Log.e("上送" + tag + "数据失败，" + volleyError.getMessage());
                onUploadTransDataListener.onError("网络异常");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                map.put("mac", mac);
                Log.d("上送的" + tag + "参数:" + map);
                return map;
            }
        };
        request.setTag(tag);
        //当前签到超时时间为20秒，默认超时后重试一次
        request.setRetryPolicy(new DefaultRetryPolicy(timeOutM * 1000, 1, 1.0f));
        RequestQueue queue = Utils.getHttpQueue(context);
        queue.add(request);
        queue.start();
    }

    /**
     * @param TMK      终端主密钥明文 hexString 16bytes
     * @param MacKey_e MacKey密文 hexString 16bytes
     * @param macCv    MacKey校验值  hexString  8bytes
     * @param data     参与计算的数据  byte[]
     * @param onGetMac 计算结果回调
     */
    public static final void getMac(String TMK, String MacKey_e, String macCv, byte[] data, onGetMac onGetMac) {
        if (XX3DESUtils.setKeyInfo("DESede", 168, "DESede/ECB/NoPadding")) {
            try {
//                    byte[] key = XX3DESUtils.initKey();
                byte[] mackey_d = getMacKey(TMK, MacKey_e, macCv);
                if (mackey_d == null) {
                    onGetMac.onError("还原Mackey失败");
                    return;
                }

//                byte[] key = XXUtils.hexStringToByteArray("21831D0E98FC56F6838F7569B8B7587E");
                Log.d("当前使用的秘钥为（mackey）：" + Utils.byteArratToHexString(mackey_d));
//                String da = "991a9a2edd89e241614a3e155f6eaef7";
                Log.d("参与加密的数据：" + data);
                byte[] encryptdata = XX3DESUtils.encrypt(mackey_d, data);
                onGetMac.onSucc(Utils.byteArratToHexString(encryptdata));
//                Log.d("加密后：" + XXUtils.byteArratToHexString(encryptdata));
//                byte[] decryptdata = XX3DESUtils.decrypt(key, encryptdata);
//                Log.d("解密后：" + new String(decryptdata));

            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
                Log.e("加解密异常，" + e.getMessage());
                onGetMac.onError("Mac计算失败，" + e.getMessage());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                onGetMac.onError("Mac计算失败，" + e.getMessage());
                Log.e("加解密异常，" + e.getMessage());
            } catch (InvalidKeyException e) {
                Log.e("加解密异常，" + e.getMessage());
                onGetMac.onError("Mac计算失败，" + e.getMessage());
                e.printStackTrace();
            } catch (BadPaddingException e) {
                Log.e("加解密异常，" + e.getMessage());
                onGetMac.onError("Mac计算失败，" + e.getMessage());
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
                onGetMac.onError("Mac计算失败，" + e.getMessage());
                Log.e("加解密异常，" + e.getMessage());
            }
        } else {
            Log.d("参数设置失败");
            onGetMac.onError("计算模式等数据设置失败");
        }
    }

    private static byte[] getMacKey(String tmk, String MacKey_e, String macKeyCV) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        boolean isCheckMackeyOk = false;
        XX3DESUtils.setKeyInfo("DESede", 168, "DESede/ECB/NoPadding");
        byte[] tmk_byte = Utils.hexStringToByteArray(tmk);
        byte[] mackey = XX3DESUtils.decrypt(tmk_byte, Utils.hexStringToByteArray(MacKey_e));
        Log.d("MacKey解密成功，Mackey：" + Utils.byteArratToHexString(mackey));
//        byte[] i = new byte[]{0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30};
        byte[] i = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

        byte[] macKeyCv = XX3DESUtils.encrypt(mackey, i);
        Log.d("计算得mackey校验值：" + byteArratToHexString(macKeyCv).substring(0, 16).toLowerCase());
        Log.d("传入mackey校验值：" + macKeyCV);
        if (macKeyCV.equals(byteArratToHexString(macKeyCv).substring(0, 8).toLowerCase())) {
            isCheckMackeyOk = true;
        }
        return isCheckMackeyOk ? mackey : null;
    }
}
