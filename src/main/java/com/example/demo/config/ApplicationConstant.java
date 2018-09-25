package com.example.demo.config;

import com.example.demo.exception.PerformanceLogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

/**
 * Created by Administrator on 2018/7/21.
 */
public class ApplicationConstant {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationConstant.class);

    @Value("${app.zookeeper.address:test}")
    public String zookeeperAddress;
    @Value("${config.zk.address:test}")
    private String newZookeeperAddress;

    @Value("${app.scheduler.thread.count:10}")
    public int schedulerThreadCount;
    @Value("${spring.application.name:test}")
    public String applicationName;
    @Value("${spring.application.index:0}")
    public int applicationIndex;
    @Value("${app.web.project:false}")
    public boolean webProject;
    @Value("${app.performance.log:NOTSET}")
    public String performanceLogType;
    @Value("${app.performance.log.ignore.urls:test}")
    public String[] performanceLogIgnoreUrls;
    @Value("${app.performance.log.ignore.urlParams:test}")
    public String[] performanceLogIgnoreUrlParams;
    @Value("${app.performance.log.ignore.headers:test}")
    public String[] performanceLogIgnoreHeaders;
    @Value("${app.performance.log.ignore.sql:false}")
    public boolean performanceLogIgnoreSql;
    @Value("${app.performance.log.ignore.mq:false}")
    public boolean performanceLogIgnoreMq;
    @Value("${spring.cloud.config.profile:dev}")
    public String profile;
    @Value("${app.okhttp.read.timeout:10000}")
    public long okHttpReadTimeout;
    @Value("${app.okhttp.connect.timeout:10000}")
    public long okHttpConnectTimeout;
    @Value("${app.okhttp.write.timeout:10000}")
    public long okHttpWriteTimeout;
    @Value("${app.okhttp.max.idle:5}")
    public int okHttpMaxIdle;
    @Value("${app.okhttp.alive.duration:300}")
    public int okHttpAliveDuration;
    @Value("${app.sjdbc.show.log:true}")
    public boolean sjdbcShowLog;
    @Value("${app.sjdbc.executor.size:0}")
    public int sjdbcExecutorSize;
    @Value("${app.ds.show.log:true}")
    public boolean dsShowLog;


    public ApplicationConstant() {
    }

    public PerformanceLogLevel determinePerformanceLogType() {
        PerformanceLogLevel type = PerformanceLogLevel.NOTSET;

        try {
            type = PerformanceLogLevel.valueOf(this.performanceLogType.toUpperCase());
        } catch (Exception var3) {
            logger.error("", var3);
        }

        return type.equals(PerformanceLogLevel.NOTSET)?(this.isProdProfile()?PerformanceLogLevel.SIMPLE:PerformanceLogLevel.ALL):type;
    }

    public boolean isDevProfile() {
        return StringUtils.isEmpty(this.profile) || "DEV".equalsIgnoreCase(this.profile);
    }

    public boolean isTestProfile() {
        return "TEST".equalsIgnoreCase(this.profile);
    }

    public boolean isPrevProfile() {
        return "PREV".equalsIgnoreCase(this.profile);
    }

    public boolean isProdProfile() {
        return "PROD".equalsIgnoreCase(this.profile);
    }

    public String getZkAddress() {
        return StringUtils.isEmpty(this.zookeeperAddress)?this.newZookeeperAddress: this.zookeeperAddress;
    }


}
