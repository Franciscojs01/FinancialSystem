package com.example.financialSystem.infra.config.https;

public class RestErrorMessage {
    private int statusCode;
    private String errorName;
    private String errorMessage;

    public RestErrorMessage(int statusCode, String errorName, String erorMessage) {
        this.statusCode = statusCode;
        this.errorName = errorName;
        this.errorMessage = erorMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorName() {
        return errorName;
    }

    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
