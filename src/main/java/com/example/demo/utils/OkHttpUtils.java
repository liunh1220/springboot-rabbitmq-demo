package com.example.demo.utils;

import com.example.demo.config.ApplicationConstant;
import com.example.demo.exception.OkHttpLoggingInterceptor;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018/7/21.
 */
public class OkHttpUtils {

    public OkHttpUtils() {
    }

    public static OkHttpClient.Builder okHttpClientBuilder(ApplicationConstant applicationConstant) {
        return (new OkHttpClient.Builder()).readTimeout(applicationConstant.okHttpReadTimeout, TimeUnit.MILLISECONDS).connectTimeout(applicationConstant.okHttpConnectTimeout, TimeUnit.MILLISECONDS).writeTimeout(applicationConstant.okHttpWriteTimeout, TimeUnit.MILLISECONDS).retryOnConnectionFailure(true).connectionPool(new ConnectionPool(applicationConstant.okHttpMaxIdle, (long)applicationConstant.okHttpAliveDuration, TimeUnit.SECONDS)).addInterceptor(new OkHttpLoggingInterceptor(applicationConstant));
    }

}
