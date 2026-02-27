package me.ccute.kvstore.sdk.exceptions;

public class KvConnectionException extends KvException {
    public KvConnectionException(String message) {
        super(message);
    }

    public KvConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
