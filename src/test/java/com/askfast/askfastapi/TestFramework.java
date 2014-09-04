package com.askfast.askfastapi;

import java.util.logging.Logger;
import org.junit.Before;


public class TestFramework {
    
    protected Logger LOG = Logger.getLogger(TestFramework.class.getSimpleName());
    
    // Please add your account info here   
    protected String accountId = "";
    protected String refreshToken = "";
    
    protected String accessToken = null;
    
    @Before
    public void setup() {
        
        if(accessToken==null) {
            try {
                AskFastRestClient client = new AskFastRestClient(accountId, refreshToken);
                accessToken = client.getAccessToken();
            } catch (Exception e) {
                LOG.severe("Failed to get accessToken: "+e.getMessage());
            }
        }
    }
}
