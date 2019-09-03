
package com.example.classchat.Util;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/*
网络工具类，发送Http请求和处理JSON文本
 */
public class Util_NetUtil {
    // 带有Requestbody的post请求
    public static void sendOKHTTPRequest(String address, okhttp3.RequestBody requestBody, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    // 带有Requestbody的get请求
    public static void sendOKHTTPRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }
}