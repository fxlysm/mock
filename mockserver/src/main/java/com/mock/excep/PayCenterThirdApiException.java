package com.mock.excep;

public class PayCenterThirdApiException extends RuntimeException {
    private static final long serialVersionUID = -4413966272339906357L;
    private String code;

    public String getCode() {
        return code;
    }

    public PayCenterThirdApiException(String code) {
        this.code = code;
    }

    public PayCenterThirdApiException(String code, String message) {
        super(message);
        this.code = code;
    }

    public PayCenterThirdApiException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public PayCenterThirdApiException(String code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public PayCenterThirdApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String code) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }
}
