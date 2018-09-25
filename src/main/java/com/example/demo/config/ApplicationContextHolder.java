package com.example.demo.config;

import org.springframework.context.ApplicationContext;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2018/7/21.
 */
public class ApplicationContextHolder {

    public static volatile ApplicationContext context;
    /** @deprecated */
    @Deprecated
    public static volatile ApplicationConstant constant;
    private static AtomicBoolean startup = new AtomicBoolean(false);
    public static final ApplicationContextHolder INSTANCE = new ApplicationContextHolder();

    private ApplicationContextHolder() {
    }

    public static ApplicationContextHolder getInstance() {
        return INSTANCE;
    }

    public synchronized void init(ApplicationConstant applicationConstant) {
        constant = applicationConstant;
        EnhancedRestTemplate.initBuilderInfo(context);
    }

    public static boolean tryStartup() {
        return startup.compareAndSet(false, true);
    }

    public static boolean isStartup() {
        return startup.get();
    }

}
