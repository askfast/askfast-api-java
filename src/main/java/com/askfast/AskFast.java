package com.askfast;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.askfast.model.Answer;
import com.askfast.model.Question;
import com.askfast.util.HttpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * A stateless implementation of the dialog handler
 * 
 * @author Shravan
 * 
 */
public class AskFast
{
	private static final String ASKFAST_JSONRPC = "http://ask-charlotte.appspot.com/rpc";
	private Question question = null;
	
	private String baseURL = null;
	private String privateKey = null;
	private String pubKey = null;
	private Map<String, String> params = null;
	
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
	
	public AskFast(String url, String privateKey, String publicKey, Map<String, String> params) {
		this.baseURL = url;
		this.privateKey = privateKey;
		this.pubKey = publicKey;
		this.params = params;
		
		if(this.params == null) 
			this.params = new HashMap<String, String>();
		
		if (question == null)
			question = new Question();
	}

	/**
	 * creates a response based on the value. Also ends the dialog
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
		ask(ask, null);
	}

	/**
	 * asks a question
	 * 
	 * @param ask
	 * @param next
	 * @return
	 */
	public void ask(String ask, String next)
	{
		ask = formatText(ask);
		next = formatURL(next);
		
		question.setQuestion_text(ask);
		question.setType(Question.QUESTION_TYPE_OPEN);
		if (next != null)
		{
			question.setAnswers(new ArrayList<Answer>(Arrays.asList(new Answer("", next))));
		}
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
	
	public String outBoundCall(String fromAddress, String toAddress, String url) throws Exception {
		
		if(privateKey==null || pubKey==null) {
			throw new Exception("Public or Private key isn't set");
		}
		
		url = formatURL(url);
		
		ObjectMapper om = new ObjectMapper();
		ObjectNode body = om.createObjectNode();
		body.put("method", "outboundCall");
		
		ObjectNode params = om.createObjectNode();
		params.put("adapterID", fromAddress);
		params.put("address", toAddress);
		params.put("url", url);
		
		params.put("privateKey", privateKey);
		params.put("publicKey", pubKey);
		body.put("params", params);
		
		String res = HttpUtil.post(ASKFAST_JSONRPC, body.toString());
		return res;
	}
	
	public void render(HttpServletResponse response) throws IOException {
		
		String json = render();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		response.getWriter().write(json);
		response.getWriter().flush();
		response.getWriter().close();
	}
	
	// Private functions
	
	private String formatText(String text) {
		if(text==null)
			return null;
		
		if(text.endsWith(".wav") && baseURL!=null) {
			text = baseURL + text;
		}
		
		if(!text.endsWith(".wav")) {
			text = "text://"+text;
		}
		
		return text;
	}
	
	private String formatURL(String url) {
		if(url==null)
			return null;
		
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
	
	// Getters and setters
	
	public void setBaseURL(String baseURL)
	{
		this.baseURL = baseURL;
	}
}
