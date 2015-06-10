package com.askfast.askfastapi.util;

/**
 * @file HttpUtil.java
 * 
 * @brief 
 * HttpUtil is a single class containing methods to conveniently perform HTTP 
 * requests. HttpUtil only uses regular java io and net functionality and does 
 * not depend on external libraries. 
 * The class contains methods to perform a get, post, put, and delete request,
 * and supports posting forms. Optionally, one can provide headers.
 *
 * Example usage:
 * 
 *     // get
 *     String res = HttpUtil.get("http://www.google.com");
 * 
 *     // post
 *     String res = HttpUtil.post("http://sendmedata.com", "This is the data");
 *
 *     // post form
 *     Map<String, String> params = new HashMap<String, String>();
 *     params.put("firstname", "Joe");
 *     params.put("lastname", "Smith");
 *     params.put("age", "28");
 *     String res = HttpUtil.postForm("http://site.com/newuser", params);
 *
 *     // append query parameters to url
 *     String url = "http://mydatabase.com/users";
 *     Map<String, String> params = new HashMap<String, String>();
 *     params.put("orderby", "name");
 *     params.put("limit", "10");
 *     String fullUrl = HttpUtil.appendQueryParams(url, params);
 *     // fullUrl = "http://mydatabase.com/user?orderby=name&limit=10"
 *
 * @license
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy 
 * of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Copyright (c) 2012 Almende B.V.
 *
 * @author 	Jos de Jong, <jos@almende.org>
 * @date	  2012-05-14
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class HttpUtil {
    
    private OkHttpClient client = null;
    private Response response = null;
    
    public HttpUtil() {

        client = new OkHttpClient();
    }

    /**
     * Send a get request
     * 
     * @param url
     * @return response
     * @throws IOException
     */
    static public String get(String url) throws IOException {

        return get(url, null);
    }

    /**
     * Send a get request
     * 
     * @param url
     *            Url as string
     * @param headers
     *            Optional map with headers
     * @return response Response as string
     * @throws IOException
     */
    static public String get(String url, Map<String, String> headers) throws IOException {

        HttpUtil httpUtil = new HttpUtil();
        Request request = httpUtil.getBuilderWIthHeaders(url, headers).build();
        httpUtil.response = httpUtil.client.newCall(request).execute();
        return httpUtil.response.body().string();
    }

    /**
     * Send a post request
     * 
     * @param url
     *            Url as string
     * @param body
     *            Request body as string
     * @param headers
     *            Optional map with headers
     * @return response Response as string
     * @throws IOException
     */
    static public String post(String url, String body, Map<String, String> headers) throws IOException {

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(mediaType, body);
        HttpUtil httpUtil = new HttpUtil();
        Request request = httpUtil.getBuilderWIthHeaders(url, headers).post(requestBody).build();
        httpUtil.response = httpUtil.client.newCall(request).execute();
        return httpUtil.response.body().string();
    }

    /**
     * Send a post request
     * 
     * @param url
     *            Url as string
     * @param body
     *            Request body as string
     * @return response Response as string
     * @throws IOException
     */
    static public String post(String url, String body) throws IOException {

        return post(url, body, null);
    }

    /**
     * Post a form with parameters
     * 
     * @param url
     *            Url as string
     * @param params
     *            map with parameters/values
     * @return response Response as string
     * @throws IOException
     */
    static public String postForm(String url, Map<String, String> params) throws IOException {

        return postForm(url, params, null);
    }

    /**
     * Post a form with parameters
     * 
     * @param url
     *            Url as string
     * @param params
     *            Map with parameters/values
     * @param headers
     *            Optional map with headers
     * @return response Response as string
     * @throws IOException
     */
    static public String postForm(String url, Map<String, String> params, Map<String, String> headers) 
    throws IOException {

        // set content type
        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        headers.put("Content-Type", "application/x-www-form-urlencoded");

        // parse parameters
        String body = "";
        if (params != null) {
            boolean first = true;
            for (String param : params.keySet()) {
                if (first) {
                    first = false;
                }
                else {
                    body += "&";
                }
                String value = params.get(param);
                body += URLEncoder.encode(param, "UTF-8") + "=";
                body += URLEncoder.encode(value, "UTF-8");
            }
        }
        return post(url, body, headers);
    }

    /**
     * Send a put request
     * 
     * @param url
     *            Url as string
     * @param body
     *            Request body as string
     * @param headers
     *            Optional map with headers
     * @return response Response as string
     * @throws IOException
     */
    static public String put(String url, String body, Map<String, String> headers) throws IOException {

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(mediaType, body);
        HttpUtil httpUtil = new HttpUtil();
        Request request = httpUtil.getBuilderWIthHeaders(url, null).put(requestBody).build();
        httpUtil.response = httpUtil.client.newCall(request).execute();
        return httpUtil.response.body().string();
    }

    /**
     * Send a put request
     * 
     * @param url
     *            Url as string
     * @return response Response as string
     * @throws IOException
     */
    static public String put(String url, String body) throws IOException {

        return put(url, body, null);
    }

    /**
     * Send a delete request
     * 
     * @param url
     *            Url as string
     * @param headers
     *            Optional map with headers
     * @return response Response as string
     * @throws IOException
     */
    static public String delete(String url, Map<String, String> headers) throws IOException {

        HttpUtil httpUtil = new HttpUtil();
        Request request = httpUtil.getBuilderWIthHeaders(url, null).delete().build();
        httpUtil.response = httpUtil.client.newCall(request).execute();
        return httpUtil.response.body().string();
    }

    /**
     * Send a delete request
     * 
     * @param url
     *            Url as string
     * @return response Response as string
     * @throws IOException
     */
    static public String delete(String url) throws IOException {

        return delete(url, null);
    }

    /**
     * Append query parameters to given url
     * 
     * @param url
     *            Url as string
     * @param params
     *            Map with query parameters
     * @return url Url with query parameters appended
     * @throws IOException
     */
    static public String appendQueryParams(String url, Map<String, String> params) throws IOException {

        String fullUrl = new String(url);

        if (params != null) {
            boolean first = (fullUrl.indexOf('?') == -1);
            for (String param : params.keySet()) {
                if (first) {
                    fullUrl += '?';
                    first = false;
                }
                else {
                    fullUrl += '&';
                }
                String value = params.get(param);
                fullUrl += URLEncoder.encode(param, "UTF-8") + '=';
                fullUrl += URLEncoder.encode(value, "UTF-8");
            }
        }
        return fullUrl;
    }

    /**
     * Retrieve the query parameters from given url
     * 
     * @param url
     *            Url containing query parameters
     * @return params Map with query parameters
     * @throws IOException
     */
    static public Map<String, String> getQueryParams(String url) throws IOException {

        Map<String, String> params = new HashMap<String, String>();
        try {
            url = url.replace(" ", URLEncoder.encode(" ", "UTF-8"));
            URIBuilder uriBuilder = new URIBuilder(new URI(url));
            for (NameValuePair nameValue : uriBuilder.getQueryParams()) {

                params.put(nameValue.getName(), nameValue.getValue());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    /**
     * Returns the url without query parameters
     * 
     * @param url
     *            Url containing query parameters
     * @return url Url without query parameters
     * @throws IOException
     */
    static public String removeQueryParams(String url) throws IOException {

        try {
            url = url.replace(" ", URLEncoder.encode(" ", "UTF-8"));
            URIBuilder uriBuilder = new URIBuilder(new URI(url));
            return uriBuilder.removeQuery().toString();
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Send a request
     * 
     * @param method
     *            HTTP method, for example "GET" or "POST"
     * @param url
     *            Url as string
     * @param body
     *            Request body as string
     * @param headers
     *            Optional map with headers
     * @return response Response as string
     * @throws IOException
     */
    static public String fetch(String method, String url, String body, Map<String, String> headers) throws IOException {

        String response = null;
        if ("GET".equalsIgnoreCase(method)) {
            response = get(url, headers);
        }
        else if ("POST".equalsIgnoreCase(method)) {
            response = post(url, body, headers);
        }
        else if ("PUT".equalsIgnoreCase(method)) {
            response = put(url, body, headers);
        }
        else if ("DELETE".equalsIgnoreCase(method)) {
            response = delete(url, headers);
        }
        else if ("POST-FORM".equalsIgnoreCase(method)) {
            response = postForm(url, getQueryParams(url), headers);
        }
        return response;
    }
	
    /**
     * Simply adds the given headers to the request
     * @param url
     * @param headers
     * @return
     */
    private Builder getBuilderWIthHeaders(String url, Map<String, String> headers) {

        Builder builder = new Request.Builder().url(url);
        if (headers != null) {
            for (String headerKey : headers.keySet()) {
                builder.addHeader(headerKey, headers.get(headerKey));
            }
        }
        return builder;
    }

	/**
	 * Read an input stream into a string
	 * @param in
	 * @return
	 * @throws IOException
	 */
	static public String streamToString(InputStream in) throws IOException {
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}
}