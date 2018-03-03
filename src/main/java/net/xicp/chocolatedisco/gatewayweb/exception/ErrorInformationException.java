package net.xicp.chocolatedisco.gatewayweb.exception;


public class ErrorInformationException extends RuntimeException {
    public ErrorInformationException() {
    }

    public ErrorInformationException(String message) {
        super(message);
    }

    public ErrorInformationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorInformationException(Throwable cause) {
        super(cause);
    }

    public ErrorInformationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
