package com.askfast.askfastapi;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import com.askfast.askfastapi.model.Question;
import com.askfast.model.Adapter;
import com.askfast.model.AdapterType;
import com.askfast.model.DDRRecord;
import com.askfast.model.Dialog;
import com.askfast.model.DialogRequest;
import com.askfast.model.Recording;
import com.askfast.model.Result;
import com.askfast.util.AskFastRestService;
import com.askfast.util.JSONUtil;
import com.askfast.util.JacksonConverter;
import com.squareup.okhttp.OkHttpClient;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;

/**
 * A client that gives access to the Ask Fast REST API. An accountId and accessToken are required to access the REST
 * API. If you do not pass an accessToken, make sure to pass a valid refreshToken, as the class tries to retrieve a
 * fresh accessToken at the key server.
 * <br />
 * This REST client uses <a href="https://github.com/square/retrofit">Retrofit</a>. Make sure to wrap every call with a
 * try/catch, as an exception could be thrown when the request fails.
 */
public class AskFastRestClient {

    public static final String DEFAULT_ENDPOINT = "https://api.ask-fast.com";
    public static final String KEYSERVER_PATH = "/keyserver/token";

    private String accountId = null;
    private String refreshToken = null;
    private String accessToken = null;
    private String endpoint = null;

    /**
     * Creates an AskFastRestClient instance. The accessToken will be retrieved from the key server using your accountId
     * and refreshToken.
     *
     * @param accountId
     *         Your accountId
     * @param refreshToken
     *         Your refreshToken
     */
    public AskFastRestClient(final String accountId, final String refreshToken) {
        this(accountId, refreshToken, null);
    }

    /**
     * Creates an AskFastRestClient instance. Be sure the accessToken is valid, otherwise any request will fail and most
     * likely throw an exception. The accessToken is not automatically refreshed.
     *
     * @param accountId
     *         Your accountId
     * @param refreshToken
     *         Your refreshToken
     * @param accessToken
     *         The access token
     */
    public AskFastRestClient(final String accountId, final String refreshToken, final String accessToken) {
        this(accountId, refreshToken, accessToken, null);
    }
    
    /**
     * Creates an AskFastRestClient instance. Be sure the accessToken is valid, otherwise any request will fail and most
     * likely throw an exception. The accessToken is not automatically refreshed.
     *
     * @param accountId
     *         Your accountId
     * @param refreshToken
     *         Your refreshToken
     * @param accessToken
     *         The access token
     * @param endpoint
     *         The endpoint, the url of API endpoint you wish to use. (The default is set to: https://api.ask-fast.com)
     */
    public AskFastRestClient(final String accountId, final String refreshToken, final String accessToken, final String endpoint) {
        this.accountId = accountId;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.endpoint = endpoint;
        
        if(endpoint==null) {
            this.endpoint = DEFAULT_ENDPOINT;
        }
    }

    /**
     * Initiate a phone call from a random call adapter.
     * 
     * @param toAddress
     *            The address which will be called
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
     * @return Result based on a the request. If its a error, this might throw a
     *         RetrofitError, if it has a {@link RetrofitError#getBody()} try to
     *         deserialize it to {@link Result} and parse the reason for the
     *         failure. A login error would however not be caught by this
     *         Exception type.
     */
    public Result startPhoneDialog(String toAddress, String url) {

        return this.startDialog(toAddress, AdapterType.CALL, null, null, url);
    }
    
    /**
     * Initiate an SMS request from a random SMS adapter.
     * 
     * @param toAddress
     *            The mobile number to which an SMS is to be sent
     * @param senderName
     *            A senderName can be attached for medium types: SMS, EMAIL. For
     *            SMS, the length should not exceed 11 charecters.
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
     * @return Result based on a the request. If its a error, this might throw a
     *         RetrofitError, if it has a {@link RetrofitError#getBody()} try to
     *         deserialize it to {@link Result} and parse the reason for the
     *         failure. A login error would however not be caught by this
     *         Exception type.
     */
    public Result startSMSDialog(String toAddress, String senderName, String url) {

        return this.startDialog(toAddress, AdapterType.SMS, senderName, null, url);
    }

    /**
     * Initiate an Email request from a random Email adapter.
     * 
     * @param toAddress
     *            An email address to which an email is to be sent
     * @param senderName
     *            A senderName can be attached for medium types: SMS, EMAIL. For
     *            SMS, the length should not exceed 11 charecters. * @param
     *            subject Only valid for an email adapter. The subject of the
     *            message to be sent
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
     * @return Result based on a the request. If its a error, this might throw a
     *         RetrofitError, if it has a {@link RetrofitError#getBody()} try to
     *         deserialize it to {@link Result} and parse the reason for the
     *         failure. A login error would however not be caught by this
     *         Exception type.
     */
    public Result startEmailDialog(String toAddress, String senderName, String subject, String url) {

        return this.startDialog(toAddress, AdapterType.EMAIL, senderName, subject, url);
    }
    
    /**
     * Initiates a dialog request from a given adapterId.
     * 
     * @param toAddress
     *            The address for which an outbound dialog request is requested
     *            to be initiated.
     * @param adapterId
     *            Specific adapterId (channel or communication mode) that is
     *            either owned by you, or that you are a shared user of.
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
     * @return Result based on a the request. If its a error, this might throw a
     *         RetrofitError, if it has a {@link RetrofitError#getBody()} try to
     *         deserialize it to {@link Result} and parse the reason for the
     *         failure. A login error would however not be caught by this
     *         Exception type.
     */
    public Result startDialog(String toAddress, String adapterId, String senderName, String subject, String url) {

        AskFastRestService service = getRestService();
        return service.startDialog(new DialogRequest(toAddress, null, adapterId, senderName, subject, url));
    }
    
    /**
     * Initiates a dialog request from a given adapterId. This will pick up the
     * first random adapter of the given type, in case of multiple adapters of
     * the same type.
     * 
     * @param toAddress
     *            The address for which an outbound dialog request is requested
     *            to be initiated.
     * @param adapterType
     *            The type of the communication for which a dialog is initiated.
     *            E.g. Call, SMS, Email etc
     * @param senderName
     *            A senderName can be attached for medium types: SMS, EMAIL. For
     *            SMS, the length should not exceed 11 charecters.
     * @param subject
     *            Only valid for an email adapter. The subject of the message to
     *            be sent
     * @param url
     *            The url used to load the dialog. This can also be a dialogId
     * @return Result based on a the request. If its a error, this might throw a
     *         RetrofitError, if it has a {@link RetrofitError#getBody()} try to
     *         deserialize it to {@link Result} and parse the reason for the
     *         failure. A login error would however not be caught by this
     *         Exception type.
     */
    public Result startDialog(String toAddress, AdapterType adapterType, String senderName, String subject, String url) {

        AskFastRestService service = getRestService();
        return service.startDialog(new DialogRequest(toAddress, adapterType, null, senderName, subject, url));
    }
    
    /**
     * Initiates a broadcast dialog request from a given adapterId.
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
     *            adapterID.
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
    public Result startDialog(Map<String, String> addressMap, Map<String, String> addressCcMap,
        Map<String, String> addressBccMap, AdapterType adapterType, String adapterID, String senderName, String subject, String url) {

        AskFastRestService service = getRestService();
        return service.startDialog(new DialogRequest(addressMap, addressCcMap, addressBccMap, adapterType, adapterID, senderName,
            subject, url));
    }
    
    /**
     * Returns a set of {@link Adapter Adapters}, optionally narrowed down by a {@code type}.
     *
     * @param type
     *         Optional. Possible values: {@code sms}, {@code call}, {@code email} or {@code ussd}
     *
     * @return A set of Adapters, optionally narrowed down by a {@code type}.
     */
    public Set<Adapter> getAdapters(String type) {
        
        AskFastRestService service = getRestService();
        return service.getAdapters(type);
    }
    
    /**
     * Returns the corresponding adapter by id
     * @param type
     * @return
     */
    public Adapter getAdapter(String adapterId) {
        return getRestService().getAdapter(adapterId);
    }

    /**
     * Updates the adapter with the given {@code adapterId}.
     *
     * @param adapterId
     *         The id of the adapter
     * @param adapter
     *         The adapter
     *
     * @return The updated adapter
     */
    public Adapter updateAdapter(String adapterId, Adapter adapter) {

        AskFastRestService service = getRestService();
        return service.updateAdapter(adapterId, adapter);
    }

    /**
     * Buy an adapter.
     *
     * TODO: specify error behaviour: what happens if you can't buy the adapter? what if you're out of credits?
     *
     * @param adapterId
     *         The id of the adapter
     */
    public void buyAdapter(String adapterId) {
        
        AskFastRestService service = getRestService();
        service.buyAdapter(adapterId);
    }

    /**
     * Remove an adapter from the logged in account.
     *
     * // TODO: describe more specifically per adapter type
     *
     * @param adapterId
     *         The The id of the adapter
     */
    public void removeAdapter(String adapterId) {
        
        AskFastRestService service = getRestService();
        service.removeAdapter(adapterId);
    }
    
    public Set<Adapter> getFreeAdapters(String adapterType, String address) {
        
        AskFastRestService service = getRestService();
        return service.getFreeAdapters(adapterType, address);
    }

    /**
     * Create a dialog. The system assigns an id to the dialog.
     *
     * @param dialog
     *         The dialog
     *
     * @return The dialog
     */
    public Dialog createDialog(Dialog dialog) {
        AskFastRestService service = getRestService();
        return service.createDialog(dialog);
    }

    /**
     * Returns the list of dialogs.
     *
     * @return The list of dialogs
     */
    public Set<Dialog> getDialogs() {
        
        AskFastRestService service = getRestService();
        return service.getDialogs();
    }

    /**
     * Retrieve a dialog by its id.
     *
     * @param dialogId
     *         The id of the dialog
     *
     * @return The dialog
     */
    public Dialog getDialog(String dialogId) {
        AskFastRestService service = getRestService();
        return service.getDialog(dialogId);
    }

    /**
     * Update a dialog.
     *
     * @param dialogId
     *         The id of the dialog
     * @param dialog
     *         The dialog
     *
     * @return The updated dialog
     */
    public Dialog updateDialog(String dialogId, Dialog dialog) {
        AskFastRestService service = getRestService();
        return service.updateDialog(dialogId, dialog);
    }

    /**
     * Delete a dialog.
     *
     * @param dialogId
     *         The The id of the dialog
     */
    public void removeDialog(String dialogId) {
        AskFastRestService service = getRestService();
        service.removeDialog(dialogId);
    }
    
    /**
     * Get all the recordings of the current accountId
     * @return List of recordings
     */
    public List<Recording> getRecordings() {
        return getRecordings( accountId );
    }
    
    /**
     * Get all the recordings linked to the specified account.    
     * @param accountId
     *         The accountId to for which all the recordings are recorded.
     * @return List of recordings
     */
    public List<Recording> getRecordings(String accountId) {
        
        if(accountId == null) {
            throw new IllegalArgumentException( "No accountId given" );
        }
        
        AskFastRestService service = getRestService();
        return service.getRecordings( accountId );
    }
    
    
    /**
     * Returns a list of {@link DDRRecord DDRRecords} based on the give
     * parameters.
     * 
     * @param adapterIds
     * @param adapterTypes
     * @param fromAddress
     * @param typeId
     * @param status
     * @param startTime
     * @param endTime
     * @param delimitedSessionKeys
     * @param offset
     * @param limit
     * @param shouldGenerateCosts
     * @param shouldIncludeServiceCosts
     * @throws Exception
     * @return
     */
    public List<DDRRecord> getDDRRecords(Collection<String> adapterIds, Collection<String> adapterTypes,
        String fromAddress, String typeId, String status, Long startTime, Long endTime, String delimitedSessionKeys,
        Integer offset, Integer limit, Boolean shouldGenerateCosts, Boolean shouldIncludeServiceCosts) throws Exception {

        AskFastRestService service = getRestService();
        String delimitedAdapterIds = null;
        String delimitedAdapterTypes = null;
        if (adapterIds != null) {
            delimitedAdapterIds = JSONUtil.toCDLString(adapterIds);
        }
        if (adapterTypes != null) {
            delimitedAdapterTypes = JSONUtil.toCDLString(adapterTypes);
        }
        return service.getDDRRecords(delimitedAdapterIds, delimitedAdapterTypes, fromAddress, typeId, status,
            startTime, endTime, delimitedSessionKeys, offset, limit, shouldGenerateCosts, shouldIncludeServiceCosts);
    }
    
    /**
     * A faster fetch of the aggregation of all quantities in the {@link DDRRecord#getQuantity()}
     * based on the filtering criteria given
     * 
     * @param adapterIds
     * @param adapterTypes
     * @param fromAddress
     * @param typeId
     * @param status
     * @param startTime
     * @param endTime
     * @param delimitedSessionKeys
     * @param offset
     * @throws Exception
     * @return
     */
    public List<DDRRecord> getDDRRecordCount(Collection<String> adapterIds, Collection<String> adapterTypes,
        String fromAddress, String typeId, String status, Long startTime, Long endTime, String delimitedSessionKeys,
        Integer offset) throws Exception {

        AskFastRestService service = getRestService();
        String delimitedAdapterIds = null;
        String delimitedAdapterTypes = null;
        if (adapterIds != null) {
            delimitedAdapterIds = JSONUtil.toCDLString(adapterIds);
        }
        if (adapterTypes != null) {
            delimitedAdapterTypes = JSONUtil.toCDLString(adapterTypes);
        }
        return service.getDDRRecordsCount(delimitedAdapterIds, delimitedAdapterTypes, fromAddress, typeId, status,
            startTime, endTime, delimitedSessionKeys, offset);
    }

    /**
     * Builds the RestAdapter that is able instantiate a RestService instance.
     *
     * @return A RestAdapter instance
     */
    private RestAdapter getRestAdapter() {
        return new RestAdapter.Builder().setRequestInterceptor(new RequestInterceptor() {

            @Override
            public void intercept(RequestFacade request) {
                try {
                    String token = getAccessToken();
                    request.addHeader("Authorization", "Bearer " + token);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).setEndpoint(this.endpoint)
            .setConverter(new JacksonConverter())
            .setClient(new OkClient(new OkHttpClient())).build();
    }

    /**
     * Retrieves a RestAdapter and instantiates an AskFastRestService instance.
     *
     * @return An AskFastRestService instance
     */
    private AskFastRestService getRestService() {
        RestAdapter adapter = getRestAdapter();
        return adapter.create(AskFastRestService.class);
    }

    /**
     * Returns the access token. If the access token is {@code null}, it requests the key server to give a fresh token.
     *
     * @return The access token
     *
     * @throws Exception
     *         Thrown when refreshing the access token if the accountId or refreshToken is null
     */
    public String getAccessToken() throws Exception {
        if (accessToken != null) {
            return accessToken;
        }
        else {
            return refreshAccessToken();
        }
    }

    /**
     * Returns the refresh token
     *
     * @return The refresh token
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Requests the key server to give a fresh access token.
     *
     * @return A fresh access token
     *
     * @throws Exception
     *         If the accountId or refreshToken is null
     */
    private String refreshAccessToken() throws Exception {
        if (accountId == null) {
            throw new Exception("AccountID isn't set.");
        }

        if (refreshToken == null) {
            throw new Exception("Refresh Token isn't set.");
        }

        // First resfresh accessToken from Keyserver
        OAuthClientRequest request = OAuthClientRequest.tokenLocation(this.endpoint + KEYSERVER_PATH)
            .setGrantType(GrantType.REFRESH_TOKEN).setClientId(accountId).setClientSecret("blabla")
            .setRefreshToken(refreshToken).buildQueryMessage();

        // create OAuth client that uses custom http client under the hood
        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        OAuthJSONAccessTokenResponse response = oAuthClient.accessToken(request);
        if (response.getAccessToken() != null) {
            accessToken = response.getAccessToken();
        }
        return accessToken;
    }
}
