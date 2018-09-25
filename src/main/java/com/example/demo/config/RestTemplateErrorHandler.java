package com.example.demo.config;

import com.example.demo.exception.CommonErrorCode;
import com.example.demo.exception.ErrorCode;
import com.example.demo.exception.ErrorInfo;
import com.example.demo.exception.RemoteCallException;
import com.example.demo.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by Administrator on 2018/7/21.
 */
public class RestTemplateErrorHandler extends DefaultResponseErrorHandler {
    protected Logger logger = LoggerFactory.getLogger(RestTemplateErrorHandler.class);

    public RestTemplateErrorHandler() {
    }

    public boolean hasError(ClientHttpResponse response) throws IOException {
        int statusCode = response.getRawStatusCode();
        return (statusCode >= 600 && statusCode <= 999) || statusCode >= 400 && statusCode < 600;
    }

    public void handleError(ClientHttpResponse response) throws IOException {
        Charset charset = this.getCharset(response);
        byte[] responseBody = this.getResponseBody(response);
        String responseText = null;
        if(responseBody != null && responseBody.length > 0) {
            responseText = (new String(responseBody, charset)).trim();
        }

        ErrorCode errorCode = CommonErrorCode.REQUEST_SERVICE_ERROR;
        ErrorInfo error = new ErrorInfo(errorCode, "", errorCode.getMessage());
        if(!StringUtils.isEmpty(responseText)) {
            try {
                error = (ErrorInfo) JsonUtils.json2Object(responseText, ErrorInfo.class);
            } catch (Exception var8) {
                ;
            }
        }

        throw new RemoteCallException(error, response.getRawStatusCode());
    }

    public byte[] getResponseBody(ClientHttpResponse response) {
        try {
            InputStream responseBody = response.getBody();
            if(responseBody != null) {
                return FileCopyUtils.copyToByteArray(responseBody);
            }
        } catch (IOException var3) {
            ;
        }

        return new byte[0];
    }

    public Charset getCharset(ClientHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        MediaType contentType = headers.getContentType();
        return contentType != null && contentType.getCharset() != null?contentType.getCharset():Charset.forName("UTF-8");
    }
}