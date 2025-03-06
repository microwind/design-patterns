package com.javaweborder.interfaces.response;

public class ResponseBody {
    private final int status;
    private final Object message;

    public ResponseBody(int status, Object message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public Object getMessage() {
        return message;
    }
}
