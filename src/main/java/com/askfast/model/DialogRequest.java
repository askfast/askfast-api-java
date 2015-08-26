package com.askfast.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import com.askfast.askfastapi.AskFastRestClient;
import com.askfast.askfastapi.model.Question;
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
    
    /**
     * A detailed consturctor which can be used to send a broadcast with a
     * specific senderName
     * 
     * @param addressMap
     *            The key value pairs of <toAddress, recipientName>
     * @param addressCcMap
     *            The key value pairs of <ccAddress, recipientName>. Used in
     *            case of an email adapter only
     * @param addressBccMap
     *            The key value pairs of <bccAddress, recipientName>. Used in
     *            case of an email adapter only
     * @param adapterType
     *            The type of communication opted for this outbound dialog. The
     *            first adapter of the given type is chosen if there are
     *            multiple ones exist. Usually either one of adapterType or
     *            adapterID is passed, although this constructor ignores the
     *            adapterType if the adapterID is present. An error is returned
     *            if its set outside of this constructor.
     * @param adapterID
     *            The id identifying a particular mode of communication. These
     *            values can be retried from {@linkplain https
     *            ://portal.ask-fast.com} in the adapters section.
     * @param senderName
     *            A senderName can be attached for medium types: SMS, EMAIL. For
     *            SMS, the length should not exceed 11 charecters.
     * @param subject
     *            Only valid for an email adapter. The subject of the message to
     *            be sent
     * @param url
     *            This can be one of: <br>
     *            1. http(s) endpoint fetching a {@link Question} json as
     *            response. <br>
     *            2. A standard text prefixed by keyword: text://<your message>.
     *            This will be transformed to a
     *            {@link Question#QUESTION_TYPE_COMMENT} <br>
     *            3. A unique dialogId as defined in the {@linkplain https
     *            ://portal.ask-fast.com} or by method
     *            {@link AskFastRestClient#createDialog(Dialog)} method
     */
    public DialogRequest(Map<String, String> addressMap, Map<String, String> addressCcMap,
        Map<String, String> addressBccMap, AdapterType adapterType, String adapterID, String senderName,
        String subject, String url) {

        this.addressMap = addressMap;
        this.addressCcMap = addressCcMap;
        this.addressBccMap = addressBccMap;
        this.adapterID = adapterID;
        //use the adapterType only if the adapterID is null
        if (adapterID == null) {
            this.adapterType = adapterType;
        }
        this.senderName = senderName;
        this.subject = subject;
        this.url = url;
    }

    /**
     * A consturctor which can be used to trigger an outbound request
     * 
     * @param address
     *            The address of the receipient. E.g. Phonenumber for Call, SMS
     *            and Email for Email adpater.
     * @param adapterType
     *            The type of communication opted for this outbound dialog. The
     *            first adapter of the given type is chosen if there are
     *            multiple ones exist. Usually either one of adapterType or
     *            adapterID is passed, although this constructor ignores the
     *            adapterType if the adapterID is present. An error is returned
     *            if its set outside of this constructor.
     * @param adapterID
     *            The id identifying a particular mode of communication. These
     *            values can be retried from {@linkplain https
     *            ://portal.ask-fast.com} in the adapters section.
     * @param senderName
     *            A senderName can be attached for medium types: SMS, EMAIL. For
     *            SMS, the length should not exceed 11 charecters.
     * @param subject
     *            Only valid for an email adapter. The subject of the message to
     *            be sent
     * @param url
     *            This can be one of: <br>
     *            1. http(s) endpoint fetching a {@link Question} json as
     *            response. <br>
     *            2. A standard text prefixed by keyword: text://<your message>.
     *            This will be transformed to a
     *            {@link Question#QUESTION_TYPE_COMMENT} <br>
     *            3. A unique dialogId as defined in the {@linkplain https
     *            ://portal.ask-fast.com} or by method
     *            {@link AskFastRestClient#createDialog(Dialog)} method
     */
    public DialogRequest(String address, AdapterType adapterType, String adapterId, String senderName, String subject,
        String url) {

        this.address = address;
        this.adapterID = adapterId;
        //use the adapterType only if the adapterID is null
        if (adapterID == null) {
            this.adapterType = adapterType;
        }
        this.senderName = senderName;
        this.subject = subject;
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
