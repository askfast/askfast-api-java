package com.askfast.askfastapi;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;

import com.askfast.model.DialogRequest;
import com.askfast.util.AskFastRestService;
import com.askfast.util.JacksonConverter;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class AskFastRestClient {

	private static final String	ASKFAST_REST_API	= "http://api.ask-fast.com";
	private static final String	ASKFAST_KEYSERVER	= "http://keyserver.ask-fast.com/keyserver/token";
//	private static final String	ASKFAST_REST_API	= "http://localhost:8084/rest";
//	private static final String	ASKFAST_KEYSERVER	= "http://localhost:8081/keyserver/token";
	
	private String accountId = null;
	private String refreshToken = null;
	private String accessToken = null;
	
	public AskFastRestClient(String accountId) {
		this(accountId, null);
	}
	
	public AskFastRestClient(String accountId, String resfreshToken) {
		this(accountId, resfreshToken, null);
	}
	
	public AskFastRestClient(String accountId, String resfreshToken, String accessToken) {
		this.accountId = accountId;
		this.refreshToken = resfreshToken;
		this.accessToken = accessToken;
	}	
	
	public String startPhoneDialog(String fromAddress, String toAddress, String url) throws Exception {
		
		RestAdapter adapter = getRestAdapter();
		AskFastRestService service = adapter.create(AskFastRestService.class);
				
		return service.startDialog(new DialogRequest(fromAddress, toAddress, url));
	}
	
	private RestAdapter getRestAdapter() throws Exception {
		return new RestAdapter.Builder()
		   .setRequestInterceptor(new RequestInterceptor() {
		        @Override
		        public void intercept(RequestFacade request) {
		        	try {
			        	String token = getAccessToken();
			            request.addHeader("Authorization", "Bearer "+token);
		        	} catch (Exception e) {
		        		e.printStackTrace();
		        	}
		        }
		    }).setEndpoint(ASKFAST_REST_API)
		    .setConverter(new JacksonConverter())
			.build();
	}
	
	public String getAccessToken() throws Exception {
		if(accessToken!=null) {
			return accessToken;
		} else {
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
		OAuthClientRequest request = OAuthClientRequest
				.tokenLocation(ASKFAST_KEYSERVER)
				.setGrantType(GrantType.REFRESH_TOKEN)
				.setClientId(accountId).setClientSecret("blabla")
				.setRefreshToken(refreshToken).buildQueryMessage();
		
		// create OAuth client that uses custom http client under the hood
		OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
		OAuthJSONAccessTokenResponse response = oAuthClient
				.accessToken(request);
		if (response.getAccessToken() != null) {
			accessToken = response.getAccessToken();
		}
		return accessToken;
	}
}
