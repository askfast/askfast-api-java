package com.askfast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.askfast.model.Answer;
import com.askfast.model.Question;

/**
 * A stateless implementation of the dialog handler
 * 
 * @author Shravan
 * 
 */
public class AskFast
{
	private Question question = null;
	private String baseURL = null;

	public void setBaseURL(String baseURL)
	{
		this.baseURL = baseURL;
	}
	
	public AskFast(HttpServletRequest req) {
		try {
			this.baseURL = getHost(req);
		} catch(Exception e) {
		}
		if (question == null)
			question = new Question();
	}

	public AskFast(String url)
	{
		setBaseURL(url);
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
	 * @param askText
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
	
	public void render(HttpServletResponse response) throws IOException {
		
		String json = render();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		response.getWriter().write(json);
		response.getWriter().flush();
		response.getWriter().close();
	}
	
	private String formatText(String text) {
		if(text==null)
			return null;
		
		if(text.startsWith("/") && baseURL!=null) {
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
		
		if(url.startsWith("/") && baseURL!=null) {
			url = baseURL + url;
		}
		
		return url;
	}
	
	private String getHost(HttpServletRequest req) throws MalformedURLException {
		int port = req.getServerPort();
		if (req.getScheme().equals("http") && port == 80) {
		    port = -1;
		} else if (req.getScheme().equals("https") && port == 443) {
		    port = -1;
		}
		URL serverURL = new URL(req.getScheme(), req.getServerName(), port, "");
		return serverURL.toString();
	}
}
