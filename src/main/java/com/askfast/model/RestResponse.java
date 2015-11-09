package com.askfast.model;

/**
 * A generic Response schema for all REST-level responses. 
 * @author shravan
 *
 */
public class RestResponse {

    String version;
    Object result;
    int code;
    String message;
    
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
    
    public RestResponse() {

    }
}
