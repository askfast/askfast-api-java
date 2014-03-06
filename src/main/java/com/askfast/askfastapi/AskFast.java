package com.askfast.askfastapi;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.askfast.askfastapi.model.Answer;
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
    
	private static final String ASKFAST_JSONRPC = "http://ask-charlotte.appspot.com/rpc";
	
	private Question question = null;
	
	private String baseURL = null;
	private String privateKey = null;
	private String pubKey = null;
	private Map<String, String> params = new HashMap<String, String>();
	
	public AskFast() {
		this(null, null, null, null);
	}
	
	public AskFast(HttpServletRequest req) {
		this(getHost(req));
	}

	public AskFast(String url)
	{
		this(url, null, null, null);
	}
	
	public AskFast(String url, String privateKey, String publicKey, Map<String, String> params ) {
		this.baseURL = url;
		this.privateKey = privateKey;
		this.pubKey = publicKey;
		this.params = params;
		
		if ( this.params == null ) {
		  this.params = new HashMap<String, String>();
		}
		
		if (question == null)
			question = new Question();
	}

    @JsonIgnore
    public Question getQuestion()
    {
        return question;
    }

    @JsonIgnore
    public String getQuestionId()
    {
        return question.getQuestion_id();
    }
        
	/**
	 * creates a response based on the value
	 * 
	 * @param value
	 * @return
	 */
	public void say(String value) {
		say(value, null);
	}
	
	public void say(String value, String next)
	{
		value = formatText(value);
		next = formatURL(next);
		question.setType(Question.QUESTION_TYPE_COMMENT);
		question.setQuestion_text(value);
		
		if(next!=null)
			question.addAnswer(new Answer(null, next));
	}
	
	/**
	 * asks a question
	 * 
	 * @param ask
	 * @return
	 */
	public void ask(String ask)
	{
	    ask(ask, "");
	}
	
	public void askByVoice(String ask, String next)
	{
	    ask( ask, next, Question.QUESTION_TYPE_VOICE_RECORDING );
	}

	/**
	 * asks an open question with text as in field: ask and an answer with callback as in field: next
	 * @param ask
	 * @param next
	 * @return
	 */
	public void ask(String ask, String next)
	{
	    ask( ask, null, next );
	}
	
	/**
     * asks an open question with text as in field: ask and an answer with callback as in field: next
     * and text as in field: answerText
     * @param ask
     * @param next
     * @return
     */
    public void ask(String ask, String answerText, String next)
    {
        ask( ask, answerText, next, Question.QUESTION_TYPE_OPEN );
    }
	
	public void ask(String ask, AskFast askFast)
	{
	    ask( ask, null, null );
	    question.addAnswer( new Answer( "", askFast.question.getQuestion_id() ) );
	}

	/**
	 * adds an answer corresponding to a question asked
	 * 
	 * @param answerText
	 * @param next
	 * @return
	 */
	public void addAnswer(String answer, String next)
	{
		question.setType(Question.QUESTION_TYPE_CLOSED);
		answer = formatText(answer);
		next = formatURL(next);
		question.addAnswer(new Answer(answer, next));
	}
	
	/**
	 * adds an answer by linking the questionid of the askFast parameter as the callbackURL. <br>
	 * Typically used with a Dialog Object (collection of questions are linked to eachother )
	 * @param answer
	 * @param askFast this is linked to the callback of the answer
	 */
	public void addAnswer(String answer, AskFast askFast)
	{
	    question.setType(Question.QUESTION_TYPE_CLOSED);
            answer = formatText(answer);
            question.addAnswer( new Answer( answer, askFast.question.getQuestion_id() ) );
	}
	
	/**
	 * redirect the control to a new agent
	 * 
	 * @return
	 */
	public void redirect(String to)
	{
		redirect(to, null, null);
	}
	
	/**
	 * redirect the control to a new agent
	 * 
	 * @param redirectText
	 *            : can be the text directly or a HTTP based url which contains the text
	 * @return
	 */
	public void redirect(String to, String redirectText)
	{
		redirect(to, redirectText, null);
	}

	/**
	 * redirect the control to a new agent
	 * 
	 * @param redirectText
	 *            : can be the text directly or a HTTP based url which contains the text
	 * @param next
	 *            : the URL where the question for the redirection agent is available
	 * @return
	 */
	public void redirect(String to, String redirectText, String next)
	{
		question.setType(Question.QUESTION_TYPE_REFERRAL);
		
		question.setUrl(to);
		if(redirectText!=null) {
			redirectText = formatText(redirectText);
			question.setQuestion_text(redirectText);
		}
		
		if(next!=null) {
			next = formatURL(next);
			question.addAnswer(new Answer(null, next));
		}
	}
	
	public String render() {		
		return question.toJSON();
	}
	
	/**
	 * overloaded method for any outboundcalls without a subject (Everything except Email)
	 * @param fromAddress address of the sender 
	 * @param toAddress address of the receiver
	 * @param url GET request on this URL has the question 
	 * @throws Exception
	 */
	public String outBoundCall( String fromAddress, String toAddress, String url ) throws Exception
    {
        return this.outBoundCall( fromAddress, null, toAddress, null, url );
    }
	
	/**
     * overloaded method for any outboundcalls with a subject (Everything including Email)
     * @param fromAddress address of the sender 
     * @param toAddress address of the receiver
     * @param url GET request on this URL has the question 
     * @throws Exception
     */
    public String outBoundCall( String fromAddress, String toAddress, String subject, String url ) throws Exception
    {
        return this.outBoundCall( fromAddress, null, toAddress, subject, url );
    }

	public String outBoundCall( String fromAddress, String senderName, String toAddress, String subject, String url ) throws Exception
    {
        return outBoundCall( fromAddress, senderName, Arrays.asList( toAddress), subject, url );
    }
	
	/**
	 * overloaded method for any broadcast outboundcalls with a subject (Everything including an Email)
	 * @param fromAddress address of the sender
	 * @param senderName name of the sender (userful for sending emails)
	 * @param toAddressList collection of all recipient addresses 
	 * @param subject subject of the email
	 * @param url question url
	 * @return
	 * @throws Exception
	 */
	public String outBoundCall( String fromAddress, String senderName, Collection<String> toAddressList, String subject, String url ) throws Exception
    {
	    Map<String, String> toAddressMap = new HashMap<String, String>();
        for ( String toAddress : toAddressList )
        {
            toAddressMap.put( toAddress, "" );
        }
        return outBoundCall( fromAddress, senderName, toAddressMap, subject, url );
    }
	
	/**
     * overloaded method for any broadcast outboundcalls without a subject (Everything except an Email).
     * Makes it backward compatible
     * @param fromAddress address of the sender 
     * @param senderName name of the sender (userful for sending emails)
     * @param toAddressNameMap map containing all recipient address for a Broadcast call
     * @param url GET request on this URL has the question 
     * @return result of this outBound
     * @throws Exception
     */
	public String outBoundCall( String fromAddress, String senderName, Map<String, String> toAddressNameMap, String url ) throws Exception
    {
	    return outBoundCall( fromAddress, senderName, toAddressNameMap, null, url );
    }
	
	/**
     * overloaded method for any broadcast outboundcalls with a subject
     * @param fromAddress address of the sender 
     * @param senderName name of the sender (userful for sending emails)
     * @param toAddressNameMap map containing all recipient address for a Broadcast call
     * @param subject subject used in case this is an outbound email
     * @param url GET request on this URL has the question 
     * @return result of this outBound
     * @throws Exception
     */
    public String outBoundCall( String fromAddress, String senderName, Map<String, String> toAddressNameMap,
        String subject, String url ) throws Exception
    {
        return outBoundCall( fromAddress, senderName, toAddressNameMap, null, null, subject, url );
    }
    
    /**
     * overloaded method for any broadcast outboundcalls with a subject, cc and bcc list (Everything including Email)
     * @param fromAddress address of the sender 
     * @param senderName name of the sender (userful for sending emails)
     * @param toAddressNameMap map containing all recipient address for a Broadcast call
     * @param subject subject used in case this is an outbound email
     * @param url GET request on this URL has the question 
     * @return result of this outBound
     * @throws Exception
     */
    public String outBoundCall( String fromAddress, String senderName, Map<String, String> toAddressNameMap,
        Map<String, String> ccAddressNameMap, Map<String, String> bccAddressNameMap, String subject, String url )
    throws Exception
    {

        if ( privateKey == null || pubKey == null )
        {
            throw new Exception( "Public or Private key isn't set" );
        }

        log.info( String.format(
            "request received to initiate outbound call. From: %s To: %s Cc: %s and Bcc: %s using URL: %s",
            fromAddress, toAddressNameMap, ccAddressNameMap, bccAddressNameMap, url ) );
        url = formatURL( url );

        ObjectMapper om = new ObjectMapper();
        ObjectNode body = om.createObjectNode();
        body.put( "method", "outboundCallWithMap" );

        ObjectNode params = om.createObjectNode();
        params.put( "adapterID", fromAddress );
        params.putPOJO( "addressMap", om.writeValueAsString( toAddressNameMap ) );
        if(ccAddressNameMap != null && !ccAddressNameMap.isEmpty())
        {
            params.putPOJO( "addressCcMap", om.writeValueAsString( ccAddressNameMap ) );
        }
        if(bccAddressNameMap != null && !bccAddressNameMap.isEmpty())
        {
            params.putPOJO( "addressBccMap", om.writeValueAsString( bccAddressNameMap ) );
        }
        params.put( "url", url );
        params.put( "senderName", senderName );
        params.put( "privateKey", privateKey );
        params.put( "publicKey", pubKey );
        params.put( "subject", subject );

        body.put( "params", params );

        log.info( String.format( "request initiated for outbound call at: %s with payload: %s", ASKFAST_JSONRPC,
            body.toString() ) );
        String res = HttpUtil.post( ASKFAST_JSONRPC, body.toString() );

        log.info( String.format( "outbound call response recieved: %s", res ) );
        return res;
    }
        
    public void addEvent(EventType eventType, String callbackURL)
    {
        callbackURL = formatURL(callbackURL);
        question.addEvent_callbacks(eventType, callbackURL);
    }
    
    public void addMediaProperty( MediumType mediumType, MediaPropertyKey propertyKey, String value )
    {
        MediaProperty mediaProperty = new MediaProperty();
        mediaProperty.setMedium( mediumType );
        mediaProperty.addProperty( propertyKey, value );
        question.addMediaProperties( mediaProperty );
    }
        
	public void render(HttpServletResponse response) throws IOException {
		
		String json = render();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		response.getWriter().write(json);
		response.getWriter().flush();
		response.getWriter().close();
	}
	
	public Map<String, String> getParams() {
		return params;
	}
	
	// Private functions
    private String formatText( String text )
    {
        if ( text == null )
            return null;

        if ( !text.startsWith( "http" ) && !text.startsWith( "https" ) )
        {
            if ( text.endsWith( ".wav" ) )
            {
                if ( baseURL != null )
                {
                	try {
                		text = URLEncoder.encode(baseURL + text, "UTF-8");
                	} catch (Exception e) {
                		e.printStackTrace();
                	}
                }
            }
            else if(!text.startsWith( "dtmfKey://" ))
            {
                text = "text://" + text;
            }
        }
        return text;
    }
	
	private String formatURL(String url) {
		if(url==null || url.isEmpty())
			return url;
		
		if((!url.startsWith("http") && !url.startsWith("https")) && baseURL!=null) {
			url = baseURL + url;
		}
		
		url = addQueryString(url);
		
		return url;
	}
	
	private String addQueryString(String url) {
		if(this.params.size()>0) {
			String query = "?";
			if(url.contains("?"))
				query = "&";
			Iterator<Entry<String, String>> it = this.params.entrySet().iterator();
			while(it.hasNext()) {
				Entry<String, String> param = it.next();
				query += param.getKey() + "=" + param.getValue()+"&";
			}
			return url + query.substring(0,query.length()-1);
		}
		
		return url;
	}
	
	private static String getHost(HttpServletRequest req) {
		int port = req.getServerPort();
		if (req.getScheme().equals("http") && port == 80) {
		    port = -1;
		} else if (req.getScheme().equals("https") && port == 443) {
		    port = -1;
		}
		String url = null;
		try {
			URL serverURL = new URL(req.getScheme(), req.getServerName(), port, "");
			url = serverURL.toString();
		} catch(Exception e) {
		}
		return url;
	}
	
	public void setBaseURL(String baseURL)
	{
		this.baseURL = baseURL;
	}
	
    private void ask( String ask, String answerText, String answerCallback, String askType )
    {
        ask = formatText( ask );
        answerCallback = formatURL( answerCallback );
        answerText = answerText != null ? answerText : "";
        question.setQuestion_text( ask );
        question.setType( askType );
        if ( answerCallback != null && !answerCallback.isEmpty() )
        {
            question.setAnswers( new ArrayList<Answer>( Arrays.asList( new Answer( answerText, answerCallback ) ) ) );
        }
    }
}
