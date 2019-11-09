package com.idan.md5Decoder.enums;

public enum ErrorType {
    INCORRECT_VALIDATION(601),
    NO_MINIONS(602),
    MINION_ALREADY_REGISTERED(603),
    HTTP_REQUEST_ERROR(604);

    private int number;

    private ErrorType(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
