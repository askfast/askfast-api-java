package com.askfast.askfastapi;

import java.util.List;
import java.util.Set;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import com.askfast.model.Adapter;
import com.askfast.model.DDRRecord;
import com.askfast.model.Dialog;
import com.askfast.model.DialogRequest;
import com.askfast.util.AskFastRestService;
import com.askfast.util.JacksonConverter;
import com.squareup.okhttp.OkHttpClient;

/**
 * A client that gives access to the Ask Fast REST API. An accountId and accessToken are required to access the REST
 * API. If you do not pass an accessToken, make sure to pass a valid refreshToken, as the class tries to retrieve a
 * fresh accessToken at the key server.
 * <br />
 * This REST client uses <a href="https://github.com/square/retrofit">Retrofit</a>. Make sure to wrap every call with a
 * try/catch, as an exception could be thrown when the request fails.
 */
public class AskFastRestClient {

    public static final String ASKFAST_REST_API = "http://api.ask-fast.com";
    public static final String ASKFAST_KEYSERVER = "http://live.ask-fast.com/keyserver/token";

    private String accountId = null;
    private String refreshToken = null;
    private String accessToken = null;

    /**
     * Creates an AskFastRestClient instance. The accessToken will be retrieved from the key server using your accountId
     * and refreshToken.
     *
     * @param accountId
     *         Your accountId
     * @param refreshToken
     *         Your refreshToken
     */
    public AskFastRestClient(String accountId, String refreshToken) {
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
    public AskFastRestClient(String accountId, String refreshToken, String accessToken) {
        this.accountId = accountId;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }

    public String startPhoneDialog(String fromAddress, String toAddress, String url) {

        AskFastRestService service = getRestService();
        return service.startDialog(new DialogRequest(fromAddress, toAddress, url));
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
     * Returns a list of {@link DDRRecord DDRRecords}.
     *
     * TODO: update this section
     *
     * @param adapterId
     *         Required.
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
     *
     * @return
     */
    public List<DDRRecord> getDDRRecords(String adapterId, String fromAddress, String typeId, String status,
        Long startTime, Long endTime, String delimitedSessionKeys, Integer offset, Integer limit,
        Boolean shouldGenerateCosts, Boolean shouldIncludeServiceCosts) {

        AskFastRestService service = getRestService();
        return service.getDDRRecords(adapterId, fromAddress, typeId, status, startTime, endTime, delimitedSessionKeys,
                                     offset, limit, shouldGenerateCosts, shouldIncludeServiceCosts);
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
        }).setEndpoint(ASKFAST_REST_API)
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
        OAuthClientRequest request = OAuthClientRequest.tokenLocation(ASKFAST_KEYSERVER)
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
