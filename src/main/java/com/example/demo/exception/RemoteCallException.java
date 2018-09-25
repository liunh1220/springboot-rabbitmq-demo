package com.example.demo.exception;

/**
 * Created by Administrator on 2018/7/21.
 */
public class RemoteCallException extends HystrixBadRequestException {
    private ErrorInfo originError;
    private int httpStatus;
    private CommonErrorCode commonErrorCode;
    private boolean systemException;

    public RemoteCallException(ErrorInfo error) {
        super(String.format("调用远程服务异常, errorInfo[%s]", new Object[]{error.toString()}));
        this.httpStatus = AppBusinessException.DEFAULT_CODE.getStatus();
        this.systemException = false;
        this.originError = error;
        this.httpStatus = error.getStatus();
        this.systemException = !(httpStatus >= 600 && httpStatus <= 999) ;
        if(this.systemException) {
            this.commonErrorCode = CommonErrorCode.fromHttpStatus(this.httpStatus);
        }

    }

    /** @deprecated */
    @Deprecated
    public RemoteCallException(ErrorInfo error, int httpStatus) {
        super(String.format("调用远程服务异常, errorInfo[%s], httpStatus[%d]", new Object[]{error.toString(), Integer.valueOf(httpStatus)}));
        this.httpStatus = AppBusinessException.DEFAULT_CODE.getStatus();
        this.systemException = false;
        this.originError = error;
        if(error.getStatus() == 0) {
            error.setStatus(httpStatus);
            this.httpStatus = httpStatus;
        } else {
            this.httpStatus = error.getStatus();
        }

        this.systemException = !(httpStatus >= 600 && httpStatus <= 999) ;
        if(this.systemException) {
            this.commonErrorCode = CommonErrorCode.fromHttpStatus(this.httpStatus);
        }

    }

    public ErrorInfo getOriginError() {
        return this.originError;
    }

    public int getHttpStatus() {
        return this.httpStatus;
    }

    public CommonErrorCode getCommonErrorCode() {
        return this.commonErrorCode;
    }

    public boolean isSystemException() {
        return this.systemException;
    }
}
