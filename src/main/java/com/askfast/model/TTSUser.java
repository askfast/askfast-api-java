package com.askfast.model;

import java.util.Map;


/**
 * Bean for the TTS user that is saved in the TTSGateway
 * @author shravan
 *
 */
public class TTSUser {

    String accountId;
    String username;
    String password;
    TTSProvider service = null;
    Map<String, Object> properties;
    String info;
    
    public String getAccountId() {
    
        return accountId;
    }
    
    public void setAccountId(String accountId) {
    
        this.accountId = accountId;
    }
    
    public String getUsername() {
    
        return username;
    }
    
    public void setUsername(String username) {
    
        this.username = username;
    }
    
    public String getPassword() {
    
        return password;
    }
    
    public void setPassword(String password) {
    
        this.password = password;
    }
    
    public TTSProvider getService() {
    
        return service;
    }
    
    public void setService(TTSProvider service) {
    
        this.service = service;
    }
    public String getInfo() {
        
        return info;
    }
    
    public void setInfo(String info) {
    
        this.info = info;
    }
    public Map<String, Object> getProperties() {
        
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
    
        this.properties = properties;
    }
}
