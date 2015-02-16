package com.askfast.askfastapi;

import java.util.List;
import java.util.Set;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import com.askfast.model.Adapter;
import com.askfast.model.DDRRecord;
import com.askfast.model.Dialog;
import com.askfast.model.DialogRequest;
import com.askfast.util.AskFastRestService;
import com.askfast.util.JacksonConverter;
import com.squareup.okhttp.OkHttpClient;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class AskFastRestClient {

    public static final String ASKFAST_REST_API = "http://api.ask-fast.com";
    public static final String ASKFAST_KEYSERVER = "http://live.ask-fast.com/keyserver/token";

    private String accountId = null;
    private String refreshToken = null;
    private String accessToken = null;

    public AskFastRestClient(String accountId) {
        this(accountId, null);
    }

    public AskFastRestClient(String accountId, String refreshToken) {
        this(accountId, refreshToken, null);
    }

    public AskFastRestClient(String accountId, String refreshToken, String accessToken) {
        this.accountId = accountId;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }

    public String startPhoneDialog(String fromAddress, String toAddress, String url) {

        AskFastRestService service = getRestService();
        return service.startDialog(new DialogRequest(fromAddress, toAddress, url));
    }

    public Set<Adapter> getAdapters(String type) {
        
        AskFastRestService service = getRestService();
        return service.getAdapters(type);
    }
    
    public void buyAdapter(String adapterId) {
        
        AskFastRestService service = getRestService();
        service.buyAdapter(adapterId);
    }
    
    public void removeAdapter(String adapterId) {
        
        AskFastRestService service = getRestService();
        service.removeAdapter(adapterId);
    }
    
    public Set<Adapter> getFreeAdapters(String adapterType, String address) {
        
        AskFastRestService service = getRestService();
        return service.getFreeAdapters(adapterType, address);
    }
    
    public Dialog createDialog(Dialog dialog) {
        AskFastRestService service = getRestService();
        return service.createDialog(dialog);
    }
    
    public Set<Dialog> getDialogs() {
        
        AskFastRestService service = getRestService();
        return service.getDialogs();
    }
    
    public Dialog getDialog(String dialogId) {
        AskFastRestService service = getRestService();
        return service.getDialog(dialogId);
    }
    
    public Dialog updateDialog(String dialogId, Dialog dialog) {
        AskFastRestService service = getRestService();
        return service.updateDialog(dialogId, dialog);
    }
    
    public void removeDialog(String dialogId) {
        AskFastRestService service = getRestService();
        service.removeDialog(dialogId);
    }
    
    public List<DDRRecord> getDDRRecords(String adapterId, String fromAddress, String typeId,
                                             String status, Long startTime, Long endTime, 
                                             Integer offset, Integer limit, Boolean shouldGenerateCosts,
                                             Boolean shouldIncludeServiceCosts) {
        
        AskFastRestService service = getRestService();
        return service.getDDRRecords(adapterId, fromAddress, typeId, status, startTime, endTime, offset, limit, shouldGenerateCosts, shouldIncludeServiceCosts);
    }

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
    
    private AskFastRestService getRestService() {
        RestAdapter adapter = getRestAdapter();
        return adapter.create(AskFastRestService.class);
    }

    public String getAccessToken() throws Exception {
        if (accessToken != null) {
            return accessToken;
        }
        else {
            return refreshAccessToken();
        }
    }

    public String getRefreshToken() {
        return refreshToken;
    }

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
