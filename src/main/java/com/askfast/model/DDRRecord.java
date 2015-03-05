package com.askfast.model;

import java.util.Collection;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DDRRecord
{   
    /**
     * status of the communication
     */
    public enum CommunicationStatus
    {
        DELIVERED, RECEIVED, SENT, FINISHED, MISSED, ERROR, UNKNOWN;
        @JsonCreator
        public static CommunicationStatus fromJson( String name )
        {
            return valueOf( name.toUpperCase() );
        }
    }
    
    private String id;
    private String adapterId;
    private String accountId;
    private String fromAddress;

    private String toAddressString;
    private String ddrTypeId;
    private Integer quantity;
    private Long start;
    private Long duration;
    Collection<String> sessionKeys;
    private CommunicationStatus status;
    private Map<String, CommunicationStatus> statusPerAddress;
    private Map<String, Object> additionalInfo;
    private AccountType accountType;
    private Double totalCost = 0.0;
    
    public DDRRecord(){}
       
    @JsonProperty("_id")
    public String getId() {
        return id;
    }
    @JsonProperty("_id")
    public void setId(String id) {
        this.id = id;
    }
    
    public String getAdapterId()
    {
        return adapterId;
    }
    
    public void setAdapterId( String adapterId )
    {
        this.adapterId = adapterId;
    }
    
    public String getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId( String accountId )
    {
        this.accountId = accountId;
    }
    
    public String getFromAddress()
    {
        return fromAddress;
    }
    
    public void setFromAddress( String fromAddress )
    {
        this.fromAddress = fromAddress;
    }
    
    public String getToAddressString() {
        return toAddressString;
    }
    
    public void setToAddressString(String toAddressString) {
        this.toAddressString = toAddressString;
    }
    
    public String getDdrTypeId()
    {
        return ddrTypeId;
    }
    
    public void setDdrTypeId( String ddrTypeId )
    {
        this.ddrTypeId = ddrTypeId;
    }
    
    public Integer getQuantity()
    {
        return quantity != null ? quantity : 0;
    }
    
    public void setQuantity( Integer quantity )
    {
        this.quantity = quantity;
    }
    
    public Long getStart()
    {
        return start;
    }

    public void setStart( Long start )
    {
        this.start = start;
    }
    
    public CommunicationStatus getStatus() {
        return status;
    }
    
    public void setStatus(CommunicationStatus status) {
        this.status = status;
    }
    
    public Map<String, CommunicationStatus> getStatusPerAddress() {
        return statusPerAddress;
    }
    
    public void setStatusPerAddress(Map<String, CommunicationStatus> statusPerAddress) {
        this.statusPerAddress = statusPerAddress;
    }
    
    public Long getDuration()
    {
        return duration;
    }

    public void setDuration( Long duration )
    {
        this.duration = duration;
    }
    
    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        
        this.totalCost = totalCost != null ? totalCost : 0.0;
    }

    public Map<String, Object> getAdditionalInfo() {
    
        return additionalInfo;
    }

    public void setAdditionalInfo(Map<String, Object> additionalInfo) {
    
        this.additionalInfo = additionalInfo;
    }
    
    public AccountType getAccountType() {
        
        return accountType;
    }
    public void setAccountType(AccountType accountType) {
    
        this.accountType = accountType;
    }
    public Collection<String> getSessionKeys() {
        
        return sessionKeys;
    }

    public void setSessionKeys(Collection<String> sessionKeys) {
    
        this.sessionKeys = sessionKeys;
    }
}

