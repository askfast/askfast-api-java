package com.askfast.askfastapi;

import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests to check the AskFast instance
 * 
 * @author Shravan
 */
public class AskFastTest extends TestFramework {

    /**
     * Check if query parameters added to the Askfast instance is added to urls
     * 
     * @throws Exception
     */
    @Test
    public void checkQueryAddedTest() throws Exception {

        HashMap<String, String> queryMap = new HashMap<String, String>();
        queryMap.put("query", "queryValue");
        String host = "http://test.example.com";
        String pathName = "/testURL";

        AskFast askFast = new AskFast(host, "accountId", "refreshToken", queryMap);
        String formattedURL = askFast.formatURL(pathName);
        Assert.assertTrue(formattedURL.equals(host + pathName + "?query=queryValue"));
    }
}
