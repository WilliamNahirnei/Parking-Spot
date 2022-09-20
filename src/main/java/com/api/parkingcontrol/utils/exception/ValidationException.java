package com.api.parkingcontrol.utils.exception;

public class ValidationException extends Exception {
    private int httpCode;
    private String validationMessage;

    public ValidationException(String message, int httpCode, String validationMessage) {
        super(message);
        this.httpCode = httpCode;
        this.validationMessage = validationMessage;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public String getValidationMessage() {
        return validationMessage;
    }

    public void setValidationMessage(String validationMessage) {
        this.validationMessage = validationMessage;
    }
}
