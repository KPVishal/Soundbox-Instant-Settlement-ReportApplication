package com.bijlipay.Utill;

import org.springframework.http.HttpStatus;

public class ApiResponseEncrypt {
    public HttpStatus status;
    public String message;
    public Object data;


    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
