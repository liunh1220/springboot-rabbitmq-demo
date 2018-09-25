package com.example.demo.config;

import com.example.demo.exception.AppBusinessException;
import com.example.demo.exception.ServiceUnavailableException;
import com.example.demo.exception.CommonErrorCode;
import com.example.demo.utils.OkHttpUtils;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.*;

import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2018/7/21.
 */
public class EnhancedRestTemplate extends RestTemplate {

    private static final Logger logger = LoggerFactory.getLogger(EnhancedRestTemplate.class);

    private static OkHttpClient rawOkHttpClient = null;
    private static MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = null;
    private static ApplicationConstant applicationConstant = null;
    private static AtomicBoolean init = new AtomicBoolean(false);

    public EnhancedRestTemplate() {
    }

    public EnhancedRestTemplate(ClientHttpRequestFactory requestFactory) {
        super(requestFactory);
    }

    public EnhancedRestTemplate(List<HttpMessageConverter<?>> messageConverters) {
        super(messageConverters);
    }

    protected <T> T doExecute(URI url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor) throws RestClientException {
        try {
            return super.doExecute(url, method, requestCallback, responseExtractor);
        } catch (ResourceAccessException var6) {
            throw new ServiceUnavailableException(var6.getMessage());
        } catch (IllegalStateException var7) {
            if(var7.getMessage().contains("No instances")) {
                throw new ServiceUnavailableException(var7.getMessage());
            } else {
                throw var7;
            }
        }
    }

    public static EnhancedRestTemplate assembleRestTemplate(MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter, OkHttpClient okHttpClient) {
        EnhancedRestTemplate restTemplate = createRestTemplate(okHttpClient);
        restTemplate.setErrorHandler(new RestTemplateErrorHandler());
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        Iterator iterator = messageConverters.iterator();

        while(iterator.hasNext()) {
            HttpMessageConverter<?> converter = (HttpMessageConverter)iterator.next();
            if(converter instanceof MappingJackson2HttpMessageConverter) {
                iterator.remove();
            }
        }

        messageConverters.add(mappingJackson2HttpMessageConverter);
        return restTemplate;
    }

    private static EnhancedRestTemplate createRestTemplate(OkHttpClient okHttpClient) {
        EnhancedRestTemplate restTemplate = new EnhancedRestTemplate(new OkHttp3ClientHttpRequestFactory(okHttpClient));
        return restTemplate;
    }

    public static EnhancedRestTemplate.Builder newBuilder() {
        if(!init.get()) {
            throw new AppBusinessException(CommonErrorCode.INTERNAL_ERROR, "EnhancedRestTemplate.newBuilder初始化失败, 需要等项目启动完成再调用该方法");
        } else {
            return new EnhancedRestTemplate.Builder();
        }
    }

    public static synchronized void initBuilderInfo(ApplicationContext context) {
        if(init.compareAndSet(false, true)) {
            applicationConstant = (ApplicationConstant)context.getBean(ApplicationConstant.class);
            mappingJackson2HttpMessageConverter = (MappingJackson2HttpMessageConverter)context.getBean(MappingJackson2HttpMessageConverter.class);
            rawOkHttpClient = OkHttpUtils.okHttpClientBuilder(applicationConstant).build();
        }

    }

    public static final class Builder {
        private okhttp3.OkHttpClient.Builder builder = null;
        private long okHttpReadTimeout;
        private long okHttpConnectTimeout;
        private long okHttpWriteTimeout;

        public Builder() {
            this.init();
        }

        private void init() {
            try {
                this.builder = EnhancedRestTemplate.rawOkHttpClient.newBuilder();
                this.okHttpReadTimeout = EnhancedRestTemplate.applicationConstant.okHttpReadTimeout;
                this.okHttpConnectTimeout = EnhancedRestTemplate.applicationConstant.okHttpConnectTimeout;
                this.okHttpWriteTimeout = EnhancedRestTemplate.applicationConstant.okHttpWriteTimeout;
            } catch (Exception var2) {
                EnhancedRestTemplate.logger.error("EnhancedRestTemplate.newBuilder初始化失败, 需要等项目启动完成再调用该方法");
                throw var2;
            }
        }

        public EnhancedRestTemplate.Builder readTimeout(long readTimeout) {
            this.okHttpReadTimeout = readTimeout;
            return this;
        }

        public EnhancedRestTemplate.Builder connectTimeout(long connectTimeout) {
            this.okHttpConnectTimeout = connectTimeout;
            return this;
        }

        public EnhancedRestTemplate.Builder writeTimeout(long writeTimeout) {
            this.okHttpWriteTimeout = writeTimeout;
            return this;
        }

        public EnhancedRestTemplate build() {
            return EnhancedRestTemplate.assembleRestTemplate(EnhancedRestTemplate.mappingJackson2HttpMessageConverter, this.builder.connectTimeout(this.okHttpConnectTimeout, TimeUnit.MILLISECONDS).readTimeout(this.okHttpReadTimeout, TimeUnit.MILLISECONDS).writeTimeout(this.okHttpWriteTimeout, TimeUnit.MILLISECONDS).build());
        }
    }
}
