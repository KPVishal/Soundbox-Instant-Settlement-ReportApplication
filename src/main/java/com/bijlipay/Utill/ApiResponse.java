package com.bijlipay.Utill;

import org.springframework.http.HttpStatus;

public class ApiResponse {

    private HttpStatus status;
    private Object message;
    private Object data;




    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ApiResponse(HttpStatus status, Object message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(HttpStatus status, Object message) {
        this.status = status;
        this.message = message;
    }

    public ApiResponse() {
        //super();
        this.status = null;
        this.message = null;
        this.data = null;
    }




}
