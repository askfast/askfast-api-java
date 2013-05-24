package com.askfast.gae;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.askfast.AskFast;


@SuppressWarnings("serial")
public class SimpleExampleServlet extends HttpServlet
{
	private ArrayList<String> cSampleAnswers = new ArrayList<String>(Arrays.asList("Yup", "Nope"));
	private ArrayList<String> cSampleResponses = new ArrayList<String>(
																		Arrays.asList(	"Thanks for your accepting!",
																						"Thanks for your reply!",
																						"Something went wrong in this conversation.."));
	AskFast cAskFast;
	static final Logger log = Logger.getLogger(SimpleExampleServlet.class.getName());

	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	{
		cAskFast = new AskFast(req);
		firstQuestion();
		try {
			cAskFast.render(resp);
		} catch (Exception e){
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		String questionId = req.getParameter("question_id");
		String preferredMedium = req.getParameter("preferred_medium");

		log.setLevel(Level.INFO);
		log.info(String.format(	"question_id: %s with and preferred_medium %s",
								questionId, preferredMedium));

		cAskFast = new AskFast(req);
		answerQuestion(questionId, preferredMedium);
		
		Logger log = Logger.getLogger(SimpleExampleServlet.class.getName());
		log.setLevel(Level.INFO);
		log.info("ending dialog " + cAskFast.render());
		
		cAskFast.render(resp);
	}

	public void answerQuestion(String answerId, String preferred_medium)
	{
		
		if (answerId.equals("1"))
		{
			cAskFast.ask("Are you coming to my bday party?", null);
			cAskFast.addAnswer(cSampleAnswers.get(0), getServletPath() + "?question_id=10");
			cAskFast.addAnswer(cSampleAnswers.get(1), getServletPath() + "?question_id=11");
		}
		else if (answerId.equals("10"))
		{
			cAskFast.say(cSampleResponses.get(0));
		}
		else if (answerId.equals("11"))
		{
			cAskFast.say(cSampleResponses.get(1));
		}
		else if (answerId.equals("3"))
		{
			cAskFast.say(cSampleResponses.get(2));
		}		
	}

	public void firstQuestion()
	{
		cAskFast.ask("Any plans for the day?", null);
		cAskFast.addAnswer(cSampleAnswers.get(0), getServletPath() + "?question_id=10");
		cAskFast.addAnswer(cSampleAnswers.get(1), getServletPath() + "?question_id=11");
	}
	
	public String getServletPath() {
		return "/simple_example";
	}
}
