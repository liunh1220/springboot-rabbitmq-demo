package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2018/7/21.
 */
public abstract class TdInitializer {

    private static final Logger logger = LoggerFactory.getLogger(TdInitializer.class);
    private AtomicBoolean initialized = new AtomicBoolean(false);

    public TdInitializer() {
    }

    public void init() {
        if(this.initialized.compareAndSet(false, true)) {
            logger.info("{} 类开始执行初始化方法", this.getClass().getSimpleName());

            try {
                this.doInit();
            } catch (Exception var5) {
                if(this.isFatal()) {
                    throw var5;
                }

                logger.error("", var5);
            } finally {
                logger.info("{} 类初始化方法执行完成", this.getClass().getSimpleName());
            }
        }

    }

    protected boolean isFatal() {
        return false;
    }

    protected abstract void doInit();
}