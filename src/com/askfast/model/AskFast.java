package com.askfast.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.askcs.dialog.sdk.QuestionBuilder;
import com.askcs.dialog.sdk.model.Answer;
import com.askcs.dialog.sdk.model.AnswerPost;
import com.askcs.dialog.sdk.model.Question;
import com.askcs.dialog.sdk.model.QuestionType;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
	private Question cQuestion;
	private String baseURL;

	private String getBaseURL()
	{
		return baseURL;
	}

	private void setBaseURL(String baseURL)
	{
		this.baseURL = baseURL;
	}

	public AskFast(String url)
	{
		setBaseURL(url);
		if (cQuestion == null)
		{
			cQuestion = new Question("1", "text://", null);
		}
	}

	/**
	 * creates a response based on the value. Also ends the dialog
	 * 
	 * @param value
	 * @return
	 */
	public Response say(String value)
	{
		cQuestion = new Question(UUID.randomUUID().toString(), value, QuestionType.COMMENT);
		return endDialog();
	}

	/**
	 * asks a question
	 * 
	 * @param askText
	 * @param next
	 * @return
	 */
	public Response ask(String askText, QuestionType askType, String next)
	{
		cQuestion.setQuestion_text(askText);
		cQuestion.setType(askType);
		if (next != null)
		{
			cQuestion.setAnswers(new ArrayList<Answer>(Arrays.asList(new Answer("", next))));
		}
		return Response.ok().build();
	}

	/**
	 * adds an answer corresponding to a question asked
	 * 
	 * @param answerText
	 * @param next
	 * @return
	 */
	public Response addAnswer(String answerText, String next)
	{
		if (cQuestion == null || next == null)
		{
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}
		ArrayList<Answer> tAllAnswers = cQuestion.getAnswers();
		if (tAllAnswers == null)
		{
			tAllAnswers = new ArrayList<Answer>();
		}

		Answer answer = new Answer(answerText, next);
		tAllAnswers.add(answer);
		cQuestion.setAnswers(tAllAnswers);

		return Response.ok(tAllAnswers).build();
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
	public Response redirect(String redirectText, String next)
	{
		ObjectMapper om = new ObjectMapper();
		ObjectNode node = om.createObjectNode();
		node.put("type", QuestionType.REFERRAL.getName());
		node.put("url", next);
		if (redirectText.contains("http") || redirectText.contains("https"))
		{
			node.put("question_text", redirectText);
		}
		else
		{
			node.put("question_text", "text://" + redirectText);
		}
		return Response.ok(node.toString()).build();
	}

	/**
	 * ends a dialog
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public Response endDialog()
	{
		String questionBuiltString = QuestionBuilder.build(cQuestion, getBaseURL(), null);
		return Response.ok(questionBuiltString).build();
	}

	public String getAnswerTextForOpenQuestion(String answer_json)
	{
		AnswerPost answer = parseJSON(answer_json, AnswerPost.class);
		if (answer != null)
		{
			return answer.getAnswer_text();
		}
		else
		{
			return null;
		}
	}

	public static <T> T parseJSON(String json_scenario, Class<T> DeserializeClass)
	{
		try
		{
			T createdScenario = new ObjectMapper().readValue(json_scenario, DeserializeClass);
			return createdScenario;
		}
		catch (JsonParseException e)
		{
			e.printStackTrace();
		}
		catch (JsonMappingException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
