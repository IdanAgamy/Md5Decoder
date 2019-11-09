package com.idan.md5Decoder.exceptions;

import com.idan.md5Decoder.enums.ErrorType;

public class ApplicationException extends Exception {
    private ErrorType errorType;

    public ApplicationException(String msg, ErrorType errorType) {
        super(msg);
        this.errorType = errorType;
    }

    public ApplicationException(String msg, Exception e, ErrorType errorType) {
        super(msg, e);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}
