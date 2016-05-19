package cn.com.shanghai.xinfusdk_w280p.utils;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;

import cn.com.shanghai.xinfusdk_w280p.useunxor.listener.OnStartRequestListener;

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
        getHttpQueue(context).add(request);
    }

    public static RequestQueue getHttpQueue(Context context) {

        if (queue == null) {
            queue = Volley.newRequestQueue(context);
        }
        return queue;
    }
}
