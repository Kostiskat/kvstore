package me.ccute.kvstore.sdk.exceptions;

public class KvTimeoutException extends KvException {
    public KvTimeoutException(String message) {
        super(message);
    }

    public KvTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
