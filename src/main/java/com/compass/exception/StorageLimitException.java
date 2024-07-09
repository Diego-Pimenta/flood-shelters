package com.compass.exception;

public class StorageLimitException extends RuntimeException {

    public StorageLimitException(String msg) {
        super(msg);
    }
}
