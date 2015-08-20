package com.askfast.model;

public class Result{

    private String version;
    private Object result;
    private int code;
    private String message;
    
    public Result() {}
    
    public String getVersion() {
    
        return version;
    }
    
    public void setVersion(String version) {
    
        this.version = version;
    }
    
    public Object getResult() {
    
        return result;
    }
    
    public void setResult(Object result) {
    
        this.result = result;
    }
    
    public int getCode() {
    
        return code;
    }
    
    public void setCode(int code) {
    
        this.code = code;
    }
    
    public String getMessage() {
    
        return message;
    }
    
    public void setMessage(String message) {
    
        this.message = message;
    }
}
