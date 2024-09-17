package com.vw.tide_eye.exception;

public class TideDataFetchException extends Exception {

    public TideDataFetchException(String message) {
        super(message);
    }

    public TideDataFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
