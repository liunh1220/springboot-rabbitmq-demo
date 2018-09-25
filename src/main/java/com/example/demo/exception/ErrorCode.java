package com.example.demo.exception;

/**
 * Created by Administrator on 2018/7/21.
 */
public interface ErrorCode {
    int MIN_BUSINESS_ERROR_STATUS = 600;
    int MAX_BUSINESS_ERROR_STATUS = 999;

    boolean isBusinessStatus(int httpStatus) ;//{        return httpStatus >= 600 && httpStatus <= 999;    }

    /** @deprecated */
    @Deprecated
    String getCode();

    int getStatus();

    String getMessage();
}
