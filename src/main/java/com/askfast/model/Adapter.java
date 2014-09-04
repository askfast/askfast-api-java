package com.askfast.model;

public class Adapter {
    
    private String configId = null;
    private String adapterType = null;
    private String address = null;
    private String myAddress = null;
    private String keyword = null;
    private String initialAgentURL = null;
    private Boolean anonymous = false;
    private String preferred_language = "nl";
    private AccountType accountType = null;
    private String dialogId = null;

    public Adapter() {}

    public Adapter(String id) {
        configId = id;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getAdapterType() {
        return adapterType;
    }

    public void setAdapterType(String adapterType) {
        this.adapterType = adapterType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getMyAddress() {
        return myAddress;
    }
    
    public void setMyAddress(String myAddress) {
        this.myAddress = myAddress;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getInitialAgentURL() {
        return initialAgentURL;
    }

    public void setInitialAgentURL(String initialAgentURL) {
        this.initialAgentURL = initialAgentURL;
    }

    public Boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
    }
    
    public String getPreferred_language() {
        return preferred_language;
    }
    
    public void setPreferred_language(String preferred_language) {
        this.preferred_language = preferred_language;
    }
    
    public AccountType getAccountType() {
        return accountType;
    }
    
    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
    
    public String getDialogId() {
        return dialogId;
    }
    
    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Adapter) {
            Adapter adapter = (Adapter) obj;
            return adapter.getConfigId().equals(configId);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return configId != null ? configId.hashCode() : 0;
    }
}
