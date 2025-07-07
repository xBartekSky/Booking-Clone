package com.webhotel.webhotel.dto;

public class ErrorDto {
    private String message;

    public ErrorDto(String string) {
        message = string;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
