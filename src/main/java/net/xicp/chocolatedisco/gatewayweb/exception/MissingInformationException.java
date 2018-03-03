package net.xicp.chocolatedisco.gatewayweb.exception;


public class MissingInformationException extends RuntimeException {
    public MissingInformationException(String message) {
        super(message);
    }

    public MissingInformationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingInformationException(Throwable cause) {
        super(cause);
    }

    public MissingInformationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public MissingInformationException() {

    }
}
