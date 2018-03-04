package net.xicp.chocolatedisco.gatewayweb.exception;

public class ErrorAuthorityException extends RuntimeException {
    public ErrorAuthorityException() {
        super();
    }

    public ErrorAuthorityException(String message) {
        super(message);
    }

    public ErrorAuthorityException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorAuthorityException(Throwable cause) {
        super(cause);
    }

    protected ErrorAuthorityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
