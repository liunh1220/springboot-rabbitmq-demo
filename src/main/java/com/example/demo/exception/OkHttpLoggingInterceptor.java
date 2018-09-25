package com.example.demo.exception;

import com.example.demo.config.ApplicationConstant;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by Administrator on 2018/7/21.
 */
public class OkHttpLoggingInterceptor implements Interceptor {

    static final Logger logger = LoggerFactory.getLogger(OkHttpLoggingInterceptor.class);
    private ApplicationConstant applicationConstant;

    public OkHttpLoggingInterceptor(ApplicationConstant applicationConstant) {
        this.applicationConstant = applicationConstant;
    }

    public Response intercept(Chain chain) throws IOException {
        PerformanceLogLevel performanceLogLevel = this.applicationConstant.determinePerformanceLogType();
        Request request = chain.request();
        /*if(!PerformanceLogUtil.canLog(request.url().toString(), performanceLogLevel, logger)) {
            return chain.proceed(request);
        } else {
            long startTime = System.currentTimeMillis();
            Request4Log request4Log = Request4Log.create4OkHttp(request, performanceLogLevel);
            logger.info(request4Log.toString());
            Exception error = null;
            Response response = null;
            boolean var19 = false;

            Response var9;
            try {
                var19 = true;
                response = chain.proceed(request);
                var9 = response;
                var19 = false;
            } catch (Exception var20) {
                error = var20;
                throw var20;
            } finally {
                if(var19) {
                    long costTime = System.currentTimeMillis() - startTime;
                    Response4Log logResp = Response4Log.create4OkHttp(performanceLogLevel, request.url().toString(), costTime, PerformanceLogUtil.logError(error), response);
                    logger.info(logResp.toString());
                }
            }

            long costTime = System.currentTimeMillis() - startTime;
            Response4Log logResp = Response4Log.create4OkHttp(performanceLogLevel, request.url().toString(), costTime, PerformanceLogUtil.logError(error), response);
            logger.info(logResp.toString());
            return var9;*/
        return  null;
    }
}