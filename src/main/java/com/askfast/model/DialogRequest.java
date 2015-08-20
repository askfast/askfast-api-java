package com.askfast.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DialogRequest {
    private String address;
    private Collection<String> addressList;
    private Map<String, String> addressMap;
    private Map<String, String> addressCcMap;
    private Map<String, String> addressBccMap;
    private String senderName;
    private String subject;
    private String url;
    private AdapterType adapterType;
    private String adapterID;
    
    public DialogRequest(String address, String adapterId, String url) {
        this.address = address;
        this.adapterID = adapterId;
        this.url = url;
    }
    
    public DialogRequest(String address, AdapterType adapterType, String url) {
        this.address = address;
        this.adapterType = adapterType;
        this.url = url;
    }

    public String getAddress() {

        return address;
    }

    public void setAddress(String address) {

        this.address = address;
    }

    public Collection<String> getAddressList() {

        return addressList;
    }

    public void setAddressList(Collection<String> addressList) {

        this.addressList = addressList;
    }

    public Map<String, String> getAddressMap() {

        return addressMap;
    }

    public void setAddressMap(Map<String, String> addressMap) {

        this.addressMap = addressMap;
    }

    public Map<String, String> getAddressCcMap() {

        return addressCcMap;
    }

    public void setAddressCcMap(Map<String, String> addressCcMap) {

        this.addressCcMap = addressCcMap;
    }

    public Map<String, String> getAddressBccMap() {

        return addressBccMap;
    }

    public void setAddressBccMap(Map<String, String> addressBccMap) {

        this.addressBccMap = addressBccMap;
    }

    public String getSenderName() {

        return senderName;
    }

    public void setSenderName(String senderName) {

        this.senderName = senderName;
    }

    public String getSubject() {

        return subject;
    }

    public void setSubject(String subject) {

        this.subject = subject;
    }

    public String getUrl() {

        return url;
    }

    public void setUrl(String url) {

        this.url = url;
    }

    public AdapterType getAdapterType() {

        return adapterType;
    }

    public void setAdapterType(AdapterType adapterType) {

        this.adapterType = adapterType;
    }

    public String getAdapterID() {

        return adapterID;
    }

    public void setAdapterID(String adapterID) {

        this.adapterID = adapterID;
    }

    /**
     * Collates all the addresses in the address, addressList, addressMaps
     * @return
     */
    @JsonIgnore
    public HashMap<String, String> getAllAddresses() {

        HashMap<String, String> result = new HashMap<String, String>();
        if (address != null) {
            result.put(address, "");
        }
        if (addressList != null && !addressList.isEmpty()) {
            for (String address : addressList) {
                result.put(address, "");
            }
        }
        if (addressMap != null && !addressMap.isEmpty()) {
            result.putAll(addressMap);
        }
        if (getAddressCcMap() != null && !addressCcMap.isEmpty()) {
            result.putAll(addressCcMap);
        }
        if (getAddressBccMap() != null && !addressBccMap.isEmpty()) {
            result.putAll(addressBccMap);
        }
        return result;
    }

    /**
     * Just validates the request payload
     * 
     * @return
     */
    @JsonIgnore
    public boolean isValidRequest() {

        if (getAllAddresses().isEmpty() || url == null || (adapterID == null && adapterType == null)) {
            return false;
        }
        return true;
    }
}
