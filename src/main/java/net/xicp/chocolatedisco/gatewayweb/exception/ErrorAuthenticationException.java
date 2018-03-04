package net.xicp.chocolatedisco.gatewayweb.exception;

public class ErrorAuthenticationException extends RuntimeException {
    public ErrorAuthenticationException() {
        super();
    }

    public ErrorAuthenticationException(String message) {
        super(message);
    }

    public ErrorAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorAuthenticationException(Throwable cause) {
        super(cause);
    }

    protected ErrorAuthenticationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
