package com.askfast.askfastapi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.client.utils.URIBuilder;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import com.askfast.askfastapi.model.Answer;
import com.askfast.askfastapi.model.EventPost;
import com.askfast.askfastapi.model.EventPost.EventType;
import com.askfast.askfastapi.model.MediaProperty;
import com.askfast.askfastapi.model.MediaProperty.MediaPropertyKey;
import com.askfast.askfastapi.model.MediaProperty.MediumType;
import com.askfast.askfastapi.model.Question;
import com.askfast.askfastapi.util.HttpUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * A stateless implementation of the dialog handler
 * @author Shravan
 */
public class AskFast 
{
    private static final Logger log = Logger.getLogger( AskFast.class.getName() );
	
    private String ASKFAST_JSONRPC = "http://live.ask-fast.com/dialoghandler/agents/dialog";
    private String ASKFAST_KEYSERVER = "http://live.ask-fast.com/keyserver/token";

    private Question question = null;
    private String baseURL = null;
    private String accountID = null;
    private String bearerToken = null;
    private String refreshToken = null;
    private Map<String, String> params = new HashMap<String, String>();

    public AskFast() {
        this( "", null, null, null );
    }

    public AskFast( HttpServletRequest req ) {
        this( getHost( req ) );
    }

    public AskFast( String url ) {
        this( url, null, null, null );
    }
	
    public AskFast( HttpServletRequest req, String accountID, String refreshToken, Map<String, String> params ){
        this(getHost( req ), accountID, refreshToken, params);
    }
	
    public AskFast( String url, String accountID, String refreshToken, Map<String, String> params ) {
        this.baseURL = url;
        this.accountID = accountID;
        this.refreshToken = refreshToken;
        this.params = params;

        if ( this.params == null ) {
            this.params = new HashMap<String, String>();
        }

        if ( question == null )
            question = new Question();
    }

    @JsonIgnore
    public String getQuestionId() {
        return question.getQuestion_id();
    }

    @JsonIgnore
    public Question getQuestion()
    {
        return question;
    }
    
    /**
     * Creates a comment question if the next parameter is null. If not creates
     * a closed question.
     * 
     * @param value
     *            Either a string value (TTS in case of a phonecall) or a url to
     *            an audio file, which explains your question or statement
     */
    public void say( String value ) {
        say( value, null );
    }

    /**
     * Creates a comment question if the next parameter is null. If not creates
     * a closed question.
     * 
     * @param value
     *            Either a string value (TTS in case of a phonecall) or a url to
     *            an audio file, which explains your question or statement
     * @param next
     *            The next URL that must be fetched. This url is added as the
     *            answer callback
     */
    public void say(String value, String next) {

        value = formatText(value);
        next = formatURL(next);
        question.setType(Question.QUESTION_TYPE_COMMENT);
        question.setQuestion_text(value);

        if (next != null)
            question.addAnswer(new Answer(null, next));
    }

    /**
     * Asks a question of Type {@link Question#QUESTION_TYPE_OPEN}
     * 
     * @param ask
     *            Either a string value (TTS in case of a phonecall) or a url to
     *            an audio file, which explains your question or statement
     */
    public void ask(String ask) {

        ask(ask, "");
    }

    /**
     * Creates an open question with media property as audio. This is used to
     * record voice notes. The location of the recorded audio is sent to the
     * <i> next </i> callback argument
     * 
     * @param ask
     *            Either a string value (TTS in case of a phonecall) or a url to
     *            an audio file, which explains your question or statement
     * @param next
     *            The next URL that must be fetched. This url is added as the
     *            answer callback
     */
    public void askByVoice(String ask, String next) {

        ask(ask, "", next);
        addMediaProperty(MediumType.BROADSOFT, MediaPropertyKey.TYPE, Question.QUESTION_TYPE_VOICE_RECORDING);
    }

    /**
     * Asks an open question with text as in field: ask and an answer with
     * callback as in field: next
     * 
     * @param ask
     *            Either a string value (TTS in case of a phonecall) or a url to
     *            an audio file, which explains your question or statement
     * @param next
     *            The next URL that must be fetched. This url is added as the
     *            answer callback
     */
    public void ask(String ask, String next) {

        ask(ask, null, next);
    }
	
    /**
     * Asks an open question with text as in field: ask and an answer with
     * callback as in field: next and text as in field: answerText
     * 
     * @param ask
     *            Either a string value (TTS in case of a phonecall) or a url to
     *            an audio file, which explains your question or statement
     * @param answerText
     *            In an open question, this is not very relevant as information
     *            specific to an answer can also be added to the ask param
     * @param next
     *            The next URL that must be fetched. This url is added as the
     *            answer callback
     */
    public void ask(String ask, String answerText, String next) {

        ask(ask, answerText, next, Question.QUESTION_TYPE_OPEN);
    }
    
    /**
     * Ends an existing conversation
     * 
     * @param exitURL Text or url to be played before exit
     */
    public void exit(String exitURL) {

        question.setType(Question.QUESTION_TYPE_EXIT);
        question.setQuestion_text(exitURL);
    }
    
    /**
     * Reject this conversation. Does not start a conversation if rendered as
     * the first question
     */
    public void reject() {

        question.setType(Question.QUESTION_TYPE_REJECT);
    }
    
    /**
     * Sets up a conference. Either the person called is pushed to a conference,
     * or the the number being called is pushed to a conference
     * 
     * @param ask
     *            Either a string value (TTS in case of a phonecall) or a url to
     *            an audio file, which explains your question or statement
     * @param next
     *            The next URL that must be fetched. This url is added as the
     *            answer callback
     */
    public void conference(String ask, String next) {

        ask(ask, null, next, Question.QUESTION_TYPE_CONFERENCE);
    }

    /**
     * Adds an answer corresponding to a question asked
     * 
     * @param answerText
     *            Either a string value (TTS in case of a phonecall) or a url to
     *            an audio file, which explains this answer or statement
     * @param next
     *            The next URL that must be fetched. This url is added as the
     *            answer callback
     */
    public void addAnswer(String answer, String next) {

        question.setType(Question.QUESTION_TYPE_CLOSED);
        answer = formatText(answer);
        next = formatURL(next);
        question.addAnswer(new Answer(answer, next));
    }

    /**
     * Redirect the control to a new agent/url
     * 
     * @param to
     *            You can redirect to a phonenumber, client or another http URL.
     */
    public void redirect(String to) {

        redirect(to, null, null, null);
    }

    /**
     * Redirect the control to a new agent
     * 
     * @param to
     *            You can redirect to a phonenumber, client or another http URL.
     * @param redirectText
     *            Either a string value (TTS in case of a phonecall) or a url to
     *            an audio file, which is played out before the redirect happens
     */
    public void redirect(String to, String redirectText) {

        redirect(to, redirectText, null, null);
    }

    /**
     * Redirect the control to a new agent
     * 
     * @param to
     *            You can redirect to a phonenumber, client or another http URL.
     * @param redirectText
     *            Either a string value (TTS in case of a phonecall) or a url to
     *            an audio file, which is played out before the redirect happens
     * @param next
     *            The next URL that must be fetched. This url is added as the
     *            answer callback
     */
    public void redirect( String to, String redirectText, String next ) {
        redirect(to, redirectText, next, null);
    }
	
    /**
     * Redirect the control to a new phone.
     * 
     * @param to
     *            You can redirect to a phonenumber, client or another http URL.
     * @param redirectText
     *            Either a string value (TTS in case of a phonecall) or a url to
     *            an audio file, which is played out before the redirect happens
     * @param next
     *            The next URL that must be fetched. This url is added as the
     *            answer callback
     * @param preconnectURL
     *            this url is used to execute a question at the callee side
     *            before connecting him to the caller. E.g. Now the callee can
     *            pick the phone, listen to a menu, then connect the call. This
     *            is useful only with special calling adapters.
     */
    public void redirect(String to, String redirectText, String next, String preconnectURL) {

        question.setType(Question.QUESTION_TYPE_REFERRAL);
        to = formatPhoneUrl(to);
        question.setUrl(Arrays.asList(to));
        if (redirectText != null) {
            redirectText = formatText(redirectText);
            question.setQuestion_text(redirectText);
        }

        if (next != null) {
            next = formatURL(next);
            question.addAnswer(new Answer(null, next));
        }
        if (preconnectURL != null && !preconnectURL.isEmpty()) {
            question.addEvent_callbacks(EventType.preconnect, preconnectURL);
            //add use preconnect media property
            question.addProperty(MediumType.BROADSOFT, MediaPropertyKey.USE_PRECONNECT, "true");
        }
    }
    
    /**
     * Redirect the control to multiple agents
     * 
     * @param addresses
     *            Redirect the phone call to all these addresses
     * @param redirectText
     *            Either a string value (TTS in case of a phonecall) or a url to
     *            an audio file, which is played out before the redirect happens
     * @param next
     *            The next URL that must be fetched. This url is added as the
     *            answer callback
     * @param preconnectURL
     *            this url is used to execute a question at the callee side
     *            before connecting him to the caller. E.g. Now the callee can
     *            pick the phone, listen to a menu, then connect the call. This
     *            is useful only with special calling adapters.
     */
    public void redirect(List<String> addresses, String redirectText, String next, String preconnectURL) {

        question.setType(Question.QUESTION_TYPE_REFERRAL);

        List<String> formatedAddresses = new ArrayList<String>();
        for (String address : addresses) {
            formatedAddresses.add(formatPhoneUrl(address));
        }
        question.setUrl(formatedAddresses);
        if (redirectText != null) {
            redirectText = formatText(redirectText);
            question.setQuestion_text(redirectText);
        }

        if (next != null) {
            next = formatURL(next);
            question.addAnswer(new Answer(null, next));
        }
        if (preconnectURL != null && !preconnectURL.isEmpty()) {
            question.addEvent_callbacks(EventType.preconnect, preconnectURL);
            //add use preconnect media property
            question.addProperty(MediumType.BROADSOFT, MediaPropertyKey.USE_PRECONNECT, "true");
        }
    }
	
    /**
     * Serializer the question prepared till now. 
     * @return Question JSON 
     */
    public String render() {
        return question.toJSON();
    }

    /**
     * @deprecated Use the one of:
     *             {@link AskFastRestClient#startDialog(String, com.askfast.model.AdapterType, String, String, String)}
     *             {@link AskFastRestClient#startDialog(String, String, String, String, String)}
     *             ,
     *             {@link AskFastRestClient#startDialog(Map, Map, Map, com.askfast.model.AdapterType, String, String, String, String)}
     *             ,
     *             {@link AskFastRestClient#startEmailDialog(String, String, String, String)}
     *             , {@link AskFastRestClient#startPhoneDialog(String, String)},
     *             {@link AskFastRestClient#startSMSDialog(String, String, String)}
     *             <br>
     *             Overloaded method for any outboundcalls without a subject
     *             (Everything except Email)
     * 
     * @param fromAddress
     *            address of the sender
     * @param toAddress
     *            address of the receiver
     * @param url
     *            GET request on this URL has the question
     * @return Address and SessionKey, if the call is successful, if not the
     *         reason is given
     * @throws Exception
     */
    @Deprecated
    public String outBoundCall(String fromAddress, String toAddress, String url) throws Exception {

        return this.outBoundCall(fromAddress, null, toAddress, null, url);
    }

    /**
     * @deprecated Use the one of:
     *             {@link AskFastRestClient#startDialog(String, com.askfast.model.AdapterType, String, String, String)}
     *             {@link AskFastRestClient#startDialog(String, String, String, String, String)}
     *             ,
     *             {@link AskFastRestClient#startDialog(Map, Map, Map, com.askfast.model.AdapterType, String, String, String, String)}
     *             ,
     *             {@link AskFastRestClient#startEmailDialog(String, String, String, String)}
     *             , {@link AskFastRestClient#startPhoneDialog(String, String)},
     *             {@link AskFastRestClient#startSMSDialog(String, String, String)}
     *             <br>
     *             overloaded method for any outboundcalls with a subject
     *             (Everything including Email)
     * 
     * @param fromAddress
     *            address of the sender
     * @param toAddress
     *            address of the receiver
     * @param url
     *            GET request on this URL has the question
     * @return Address and SessionKey, if the call is successful, if not the
     *         reason is given
     * @throws Exception
     */
    public String outBoundCall(String fromAddress, String toAddress, String subject, String url) throws Exception {

        return this.outBoundCall(fromAddress, null, toAddress, subject, url);
    }

    /**
     * @deprecated Use the one of:
     *             {@link AskFastRestClient#startDialog(String, com.askfast.model.AdapterType, String, String, String)}
     *             {@link AskFastRestClient#startDialog(String, String, String, String, String)}
     *             ,
     *             {@link AskFastRestClient#startDialog(Map, Map, Map, com.askfast.model.AdapterType, String, String, String, String)}
     *             ,
     *             {@link AskFastRestClient#startEmailDialog(String, String, String, String)}
     *             , {@link AskFastRestClient#startPhoneDialog(String, String)},
     *             {@link AskFastRestClient#startSMSDialog(String, String, String)}
     *             <br>
     * @param fromAddress
     * @param senderName
     * @param toAddress
     * @param subject
     * @param url
     * @return Address and SessionKey, if the call is successful, if not the
     *         reason is given
     * @throws Exception
     */
    public String outBoundCall(String fromAddress, String senderName, String toAddress, String subject, String url)
        throws Exception {

        return outBoundCall(fromAddress, senderName, Arrays.asList(toAddress), subject, url);
    }

    /**
     * @deprecated Use the one of:
     *             {@link AskFastRestClient#startDialog(String, com.askfast.model.AdapterType, String, String, String)}
     *             {@link AskFastRestClient#startDialog(String, String, String, String, String)}
     *             ,
     *             {@link AskFastRestClient#startDialog(Map, Map, Map, com.askfast.model.AdapterType, String, String, String, String)}
     *             ,
     *             {@link AskFastRestClient#startEmailDialog(String, String, String, String)}
     *             , {@link AskFastRestClient#startPhoneDialog(String, String)},
     *             {@link AskFastRestClient#startSMSDialog(String, String, String)}
     *             <br>
     *             overloaded method for any broadcast outboundcalls with a
     *             subject (Everything including an Email)
     * 
     * @param fromAddress
     *            address of the sender
     * @param senderName
     *            name of the sender (userful for sending emails)
     * @param toAddressList
     *            collection of all recipient addresses
     * @param subject
     *            subject of the email
     * @param url
     *            question url
     * @return Address and SessionKey, if the call is successful, if not the
     *         reason is given
     * @throws Exception
     */
    public String outBoundCall(String fromAddress, String senderName, Collection<String> toAddressList, String subject,
        String url) throws Exception {

        Map<String, String> toAddressMap = new HashMap<String, String>();
        for (String toAddress : toAddressList) {
            toAddressMap.put(toAddress, "");
        }
        return outBoundCall(fromAddress, senderName, toAddressMap, subject, url);
    }

    /**
     * @deprecated Use the one of:
     *             {@link AskFastRestClient#startDialog(String, com.askfast.model.AdapterType, String, String, String)}
     *             {@link AskFastRestClient#startDialog(String, String, String, String, String)}
     *             ,
     *             {@link AskFastRestClient#startDialog(Map, Map, Map, com.askfast.model.AdapterType, String, String, String, String)}
     *             ,
     *             {@link AskFastRestClient#startEmailDialog(String, String, String, String)}
     *             , {@link AskFastRestClient#startPhoneDialog(String, String)},
     *             {@link AskFastRestClient#startSMSDialog(String, String, String)} <br>
     * overloaded method for any broadcast outboundcalls without a subject
     * (Everything except an Email). Makes it backward compatible
     * 
     * @param fromAddress
     *            address of the sender
     * @param senderName
     *            name of the sender (userful for sending emails)
     * @param toAddressNameMap
     *            map containing all recipient address for a Broadcast call
     * @param url
     *            GET request on this URL has the question
     * @return result of this outBound
     * @throws Exception
     */
    public String outBoundCall( String fromAddress, String senderName, Map<String, String> toAddressNameMap, String url ) throws Exception {
        return outBoundCall( fromAddress, senderName, toAddressNameMap, null, url );
    }
	
    /**
     * @deprecated Use the one of:
     *             {@link AskFastRestClient#startDialog(String, com.askfast.model.AdapterType, String, String, String)}
     *             {@link AskFastRestClient#startDialog(String, String, String, String, String)}
     *             ,
     *             {@link AskFastRestClient#startDialog(Map, Map, Map, com.askfast.model.AdapterType, String, String, String, String)}
     *             ,
     *             {@link AskFastRestClient#startEmailDialog(String, String, String, String)}
     *             , {@link AskFastRestClient#startPhoneDialog(String, String)},
     *             {@link AskFastRestClient#startSMSDialog(String, String, String)}
     *             <br>
     *             overloaded method for any broadcast outboundcalls with a
     *             subject
     * @param fromAddress
     *            address of the sender
     * @param senderName
     *            name of the sender (userful for sending emails)
     * @param toAddressNameMap
     *            map containing all recipient address for a Broadcast call
     * @param subject
     *            subject used in case this is an outbound email
     * @param url
     *            GET request on this URL has the question
     * @return result of this outBound
     * @throws Exception
     */
    public String outBoundCall(String fromAddress, String senderName, Map<String, String> toAddressNameMap,
        String subject, String url) throws Exception {

        return outBoundCall(fromAddress, senderName, toAddressNameMap, null, null, subject, url);
    }
    
    /**
     * @deprecated Use the one of:
     *             {@link AskFastRestClient#startDialog(String, com.askfast.model.AdapterType, String, String, String)}
     *             {@link AskFastRestClient#startDialog(String, String, String, String, String)}
     *             ,
     *             {@link AskFastRestClient#startDialog(Map, Map, Map, com.askfast.model.AdapterType, String, String, String, String)}
     *             ,
     *             {@link AskFastRestClient#startEmailDialog(String, String, String, String)}
     *             , {@link AskFastRestClient#startPhoneDialog(String, String)},
     *             {@link AskFastRestClient#startSMSDialog(String, String, String)}
     *             <br>
     *             overloaded method for any broadcast outboundcalls with a
     *             subject, cc and bcc list (Everything including Email)
     * @param fromAddress
     *            address of the sender
     * @param senderName
     *            name of the sender (userful for sending emails)
     * @param toAddressNameMap
     *            map containing all recipient address for a Broadcast call
     * @param subject
     *            subject used in case this is an outbound email
     * @param url
     *            GET request on this URL has the question
     * @return result of this outBound
     * @throws Exception
     */
    public String outBoundCall(String fromAddress, String senderName, Map<String, String> toAddressNameMap,
        Map<String, String> ccAddressNameMap, Map<String, String> bccAddressNameMap, String subject, String url)
            throws Exception {

        if (bearerToken == null) {
            obtainAccessToken();
        }
        if (accountID == null || bearerToken == null) {
            throw new Exception("AccountID or BearerToken isn't set, please obtainAccessToken() first!");
        }

        log.info(String.format(
            "request received to initiate outbound call. From: %s To: %s Cc: %s and Bcc: %s using URL: %s", fromAddress,
            toAddressNameMap, ccAddressNameMap, bccAddressNameMap, url));

        url = formatURL(url);

        ObjectMapper om = new ObjectMapper();
        ObjectNode body = om.createObjectNode();
        body.put("id", UUID.randomUUID().toString());
        body.put("method", "outboundCallWithMap");

        ObjectNode params = om.createObjectNode();
        params.put("adapterID", fromAddress);
        params.putPOJO("addressMap", om.writeValueAsString(toAddressNameMap));
        if (ccAddressNameMap != null && !ccAddressNameMap.isEmpty()) {
            params.putPOJO("addressCcMap", om.writeValueAsString(ccAddressNameMap));
        }
        if (bccAddressNameMap != null && !bccAddressNameMap.isEmpty()) {
            params.putPOJO("addressBccMap", om.writeValueAsString(bccAddressNameMap));
        }
        params.put("url", url);
        params.put("senderName", senderName);
        params.put("accountID", accountID);
        params.put("bearerToken", bearerToken);
        params.put("subject", subject);
        body.set("params", params);
        log.info(String.format("request initiated for outbound call at: %s with payload: %s", ASKFAST_JSONRPC,
            body.toString()));
        String res = HttpUtil.post(ASKFAST_JSONRPC, body.toString());
        if (res != null && res.startsWith("{") || res.trim().startsWith("{")) {
            ObjectNode json = om.readValue(res, ObjectNode.class);
            if (json.has("error") && json.get("error").get("message").textValue().equals("Invalid token given")) {
                throw new Exception("Please re-obtain AccessToken!");
            }
        }
        log.info(String.format("outbound call response recieved: %s", res));
        return res;
    }
        
    /**
     * Adds an event callback
     * 
     * @param eventType
     *            Type of the event
     * @param callbackURL
     *            The callback to which {@link EventPost} is POSTed to
     */
    public void addEvent(EventType eventType, String callbackURL) {

        callbackURL = formatURL(callbackURL);
        question.addEvent_callbacks(eventType, callbackURL);
    }
    
    /**
     * Adds some communication channel specific properties. E.g. TIMEOUT for
     * phonecalls etc
     * 
     * @param mediumType
     *            The type of the communication channel
     * @param propertyKey
     *            The type of the property added
     * @param value
     *            The value of the property added
     */
    public void addMediaProperty(MediumType mediumType, MediaPropertyKey propertyKey, String value) {

        MediaProperty mediaProperty = new MediaProperty();
        mediaProperty.setMedium(mediumType);
        mediaProperty.addProperty(propertyKey, value);
        question.addMediaProperties(mediaProperty);
    }

    /**
     * @deprecated
     * Recommended use {@link AskFastRestClient#getAccessToken()} instead
     * @return The accessToken by connecting to the ASK-Fast backend
     * @throws Exception
     */
    public String obtainAccessToken() throws Exception {

        if ( accountID == null || refreshToken == null ) {
            throw new Exception( "AccountID or ResfreshToken isn't set." );
        }
        // First obtaining accessToken from Keyserver
        OAuthClientRequest request = OAuthClientRequest.tokenLocation( ASKFAST_KEYSERVER ).setGrantType( GrantType.REFRESH_TOKEN ).setClientId( accountID ).setClientSecret( "blabla" ).setRefreshToken( refreshToken ).buildQueryMessage();

        // create OAuth client that uses custom http client under the hood
        OAuthClient oAuthClient = new OAuthClient( new URLConnectionClient() );
        OAuthJSONAccessTokenResponse response = oAuthClient.accessToken( request );
        if ( response.getAccessToken() != null ) {
            bearerToken = response.getAccessToken();
            return bearerToken;
        }
        return null;
    }

    /**
     * Set the accountId for this instance
     * @param accountID
     */
    public void setAccountID( String accountID ) {
        this.accountID = accountID;
    }
    
    public String getAccountID() {
        return accountID;
    }

    public String getBearerToken() {
        return bearerToken;
    }

    public void setBearerToken( String bearerToken ) {
        this.bearerToken = bearerToken;
    }
    
    public void setPreferredLanguage(String language) {
        this.question.setPreferred_language( language );
    }

    public void render( HttpServletResponse response ) throws IOException {

        String json = render();
        response.setContentType( "application/json" );
        response.setCharacterEncoding( "UTF-8" );

        response.getWriter().write( json );
        response.getWriter().flush();
        response.getWriter().close();
    }

    public Map<String, String> getParams() {
        return params;
    }
	
	// Private functions
    private String formatText(String text) {

        if (text == null)
            return null;

        if (!text.startsWith("http") && !text.startsWith("https")) {
            if (text.endsWith(".wav") && baseURL != null && !baseURL.isEmpty()) {
                text = baseURL + text;
            }
            else if (!text.startsWith("dtmfKey://")) {
                text = "text://" + text;
            }
        }
        return text;
    }
    
    private String formatPhoneUrl( String url ) {
        if (url != null && !url.startsWith("http") && !url.startsWith("tel:")) {
            url = "tel:" + url;
        }
        return url;
    }
	
    protected String formatURL( String url ) {
        if ( url == null || url.isEmpty() )
            return url;

        if ((!url.startsWith("http") && !url.startsWith("https")) && baseURL != null && !baseURL.isEmpty()) {
            url = baseURL + url;
        }
        url = addQueryString( url );
        return url;
    }
    
    /**
     * Returns the url by adding the queryKey=queryValue based on if a query
     * param is already seen in the url
     * 
     * @return
     * @throws UnsupportedEncodingException
     */
    private String addQueryString(String url) {

        if (params != null && !params.isEmpty()) {
            try {
                url = url.replace(" ", "%20");
                URIBuilder uriBuilder = new URIBuilder(new URI(url));
                for (String queryKey : params.keySet()) {
                    uriBuilder.addParameter(queryKey, params.get(queryKey));
                }
                return uriBuilder.toString();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return url;
    }

    private static String getHost( HttpServletRequest req ) {
        int port = req.getServerPort();
        if ( req.getScheme().equals( "http" ) && port == 80 ) {
            port = -1;
        }
        else if ( req.getScheme().equals( "https" ) && port == 443 ) {
            port = -1;
        }
        String url = null;
        try {
            URL serverURL = new URL( req.getScheme(), req.getServerName(), port, "" );
            url = serverURL.toString();
        }
        catch ( Exception e ) {
        }
        return url;
    }

    public String getBaseURL() {
        return baseURL;
    }
	
    public void setBaseURL( String baseURL ) {
        this.baseURL = baseURL;
    }
	
    /**
     * Asks an question of given type, with text as in field: ask and an answer
     * with callback as in field: answerCallback and text as in field:
     * answerText
     * 
     * @param ask
     *            Either a string value (TTS in case of a phonecall) or a url to
     *            an audio file, which explains your question or statement
     * @param answerText
     *            In an open question, this is not very relevant as information
     *            specific to an answer can also be added to the ask param
     * @param answerCallback
     *            The next URL that must be fetched. This url is added as the
     *            answer callback
     * @param askType
     *            The question Type
     */
    private void ask(String ask, String answerText, String answerCallback, String askType) {

        ask = formatText(ask);
        answerCallback = formatURL(answerCallback);
        answerText = answerText != null ? answerText : "";
        question.setQuestion_text(ask);
        question.setType(askType);
        if (answerCallback != null && !answerCallback.isEmpty()) {
            question.setAnswers(new ArrayList<Answer>(Arrays.asList(new Answer(answerText, answerCallback))));
        }
    }

    public String getASKFAST_JSONRPC() {

        return ASKFAST_JSONRPC;
    }

    public void setASKFAST_JSONRPC(String aSKFAST_JSONRPC) {

        ASKFAST_JSONRPC = aSKFAST_JSONRPC;
    }

    public String getASKFAST_KEYSERVER() {

        return ASKFAST_KEYSERVER;
    }

    public void setASKFAST_KEYSERVER(String aSKFAST_KEYSERVER) {

        ASKFAST_KEYSERVER = aSKFAST_KEYSERVER;
    }
}
