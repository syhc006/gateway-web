package net.xicp.chocolatedisco.gatewayweb.exception;


public class DuplicateInformationException extends RuntimeException {
    public DuplicateInformationException() {
    }

    public DuplicateInformationException(String message) {
        super(message);
    }

    public DuplicateInformationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateInformationException(Throwable cause) {
        super(cause);
    }

    public DuplicateInformationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
