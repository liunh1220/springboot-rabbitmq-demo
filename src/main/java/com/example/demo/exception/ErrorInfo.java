package com.example.demo.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Administrator on 2018/7/21.
 */
public class ErrorInfo {

    private String code;
    private String message;
    private String requestUri;
    private int status;

    /** @deprecated */
    @Deprecated
    @JsonCreator
    public ErrorInfo(@JsonProperty("code") String code, @JsonProperty("requestUri") String requestUri, @JsonProperty(value = "message",defaultValue = "") String message) {
        this(code, requestUri, message, 500);
    }

    public ErrorInfo(ErrorCode errorCode, String requestUri) {
        this((ErrorCode)errorCode, requestUri, (String)null);
    }

    public ErrorInfo(ErrorCode errorCode, String requestUri, String message) {
        this(errorCode.getCode(), requestUri, message == null?errorCode.getMessage():message, errorCode.getStatus());
    }

    public ErrorInfo(String code, String requestUri, String message, int status) {
        this.code = code;
        this.requestUri = requestUri;
        this.message = message;
        this.status = status;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public String getRequestUri() {
        return this.requestUri;
    }

    public int getStatus() {
        return this.status;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String toString() {
        return "ErrorInfo{code='" + this.code + '\'' + ", message='" + this.message + '\'' + ", requestUri='" + this.requestUri + '\'' + ", status=" + this.status + '}';
    }



}
