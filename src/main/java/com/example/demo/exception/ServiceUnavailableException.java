package com.example.demo.exception;

/**
 * Created by Administrator on 2018/7/21.
 */
public class ServiceUnavailableException extends AppBusinessException {
    private static final ErrorCode ERROR_CODE;

    public ServiceUnavailableException(String message) {
        super(ERROR_CODE, " 远程服务不可用: " + message);
    }

    static {
        ERROR_CODE = CommonErrorCode.SERVICE_UNAVAILABLE;
    }
}
