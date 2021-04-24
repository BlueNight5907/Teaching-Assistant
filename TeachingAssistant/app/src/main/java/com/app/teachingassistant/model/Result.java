package com.app.teachingassistant.model;

public class Result {
    private boolean isError;
    private String message;
    public Result(boolean isError, String mess){
        this.isError = isError;
        this.message = mess;
    }

    public boolean isError() {
        return isError;
    }

    public void setIsError(boolean error) {
        isError = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
