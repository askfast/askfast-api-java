package com.askfast.model;

public class Recording{

    private String id = null;
    private String filename = null;
    private String contentType = null;
    private String accountId = null;
    private String ddrId = null;
    private String adapterId = null;
    
    private Long creationTime = null;
    
    public Recording() {}
    
    public String getId() {
        return id;
    }
    
    public void setId( String id ) {
        this.id = id;
    }
    
    public String getAccountId() {
        return accountId;
    }
    
    public void setAccountId( String accountId ) {
        this.accountId = accountId;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public void setFilename( String filename ) {
        this.filename = filename;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType( String contentType ) {
        this.contentType = contentType;
    }
    
    public String getDdrId() {
        return ddrId;
    }
    
    public void setDdrId( String ddrId ) {
        this.ddrId = ddrId;
    }
    
    public String getAdapterId() {
        return adapterId;
    }
    
    public void setAdapterId( String adapterId ) {
        this.adapterId = adapterId;
    }
    
    public Long getCreationTime() {
        return creationTime;
    }
    
    public void setCreationTime( Long creationTime ) {
        this.creationTime = creationTime;
    }
}
