package com.askfast.model;

import retrofit.RetrofitError;

/**
 * This is the bean sent as a response for a dialog request. If its a error,
 * this might throw a RetrofitError, if it has a {@link RetrofitError#getBody()}
 * try to deserialize it to {@link Result} and parse the reason for the failure.
 * A login error would however not be caught by this Exception type.
 * @author shravan
 */
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
