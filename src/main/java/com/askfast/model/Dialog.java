package com.askfast.model;

public class Dialog {
    protected String id = null;
    protected String name = null;
    protected String url = null;
    protected String owner = null;
    
    protected boolean useBasicAuth = false;
    protected String userName = null;
    protected String password = null;
    
    protected TTSInfo ttsInfo = null;

    public Dialog() {
        this(null, null);
    }

    public Dialog(String name, String url) {
        this.name = name;
        this.url = url;
        
        this.useBasicAuth = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    public boolean isUseBasicAuth() {
        return useBasicAuth;
    }
    
    public void setUseBasicAuth( boolean useBasicAuth ) {
        this.useBasicAuth = useBasicAuth;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName( String userName ) {
        this.userName = userName;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword( String password ) {
        this.password = password;
    }
    
    public TTSInfo getTtsInfo() {
        return ttsInfo;
    }
    
    public void setTtsInfo( TTSInfo ttsInfo ) {
        this.ttsInfo = ttsInfo;
    }
}
