package com.askfast.model;

import java.util.UUID;

public class Answer extends ModelBase {
	
	private String answer_id;
	private String answer_text;
	private String callback;
	
	public Answer(){
	}
	
	public Answer(String answer_text){
		this(answer_text, null);
	}
	
	public Answer(String answer_text, String callback){
		
		this.answer_id = UUID.randomUUID().toString();
		this.answer_text = answer_text;
		this.callback = callback;
	}
	
	
	public String getAnswer_id() { 
		return this.answer_id; 
	}
	
	public String getAnswer_text() { 
		return this.answer_text; 
	}
	
	public String getCallback() { 
		return this.callback; 
	}
		
	public void setAnswer_id(String answer_id) { 
		this.answer_id = answer_id;
	}
	
	public void setAnswer_text(String answer_text) { 
		this.answer_text = answer_text; 
	}
	
	public void setCallback(String callback) { 
		this.callback = callback; 
	}
	
	public static Answer fromJSON(String json)
	{
	    return fromJSON( json, Answer.class );
	}
}
