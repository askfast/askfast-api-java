package com.askfast.model;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AnswerPost extends ModelBase{
	
	String dialog_id;
	String question_id;
	String answer_id;
	String answer_text;
	String responder;

	private AnswerPost(){}
		
	public static AnswerPost createInstance(HttpServletRequest req) {
		AnswerPost ap = null;
		try {
			InputStream is = req.getInputStream();
			ObjectMapper om = new ObjectMapper();
			ap = om.readValue(is, AnswerPost.class);
		} catch(Exception e) {
		}
		
		return ap;
	}
	
	public String getDialog_id() {
		return dialog_id;
	}
	public String getQuestion_id() {
		return question_id;
	}
	public String getAnswer_id() {
		return answer_id;
	}
	public String getAnswer_text() {
		return answer_text;
	}
	public String getResponder() {
		return responder;
	}
	
	public void setDialog_id(String dialog_id) {
		this.dialog_id = dialog_id;
	}
	public void setQuestion_id(String question_id) {
		this.question_id = question_id;
	}
	public void setAnswer_id(String answer_id) {
		this.answer_id = answer_id;
	}
	public void setAnswer_text(String answer_text) {
		this.answer_text = answer_text;
	}
	public void setResponder(String responder) {
		this.responder = responder;
	}
	
        public static AnswerPost fromJSON( String json )
        {
            return fromJSON( json, AnswerPost.class );
        }
}