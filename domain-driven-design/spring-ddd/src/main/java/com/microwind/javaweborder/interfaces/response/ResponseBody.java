package com.microwind.javaweborder.interfaces.response;

public class ResponseBody {
    private final int status;
    private final String message;
    private Object data;

    public ResponseBody(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public Object getMessage() {
        return message;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
