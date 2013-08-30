
package com.askfast.askfastapi.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import com.askfast.askfastapi.model.EventPost.EventType;
import com.askfast.model.MediaHint;
import com.askfast.model.ModelBase;

public class Question extends ModelBase{
	
	public static final String QUESTION_TYPE_CLOSED = "closed";
	public static final String QUESTION_TYPE_OPEN = "open";
	public static final String QUESTION_TYPE_COMMENT = "comment";
	public static final String QUESTION_TYPE_REFERRAL = "referral";
	public static final String QUESTION_TYPE_VOICE_RECORDING = "openaudio";
	
    private Collection<MediaHint> media_Hints;

	private String question_id = "";
	private String question_text = null;
	private String type = null;
	private String url = null;
	
	ArrayList<Answer> answers;
	ArrayList<EventCallback> event_callbacks;
	
	public Question() {
		this(UUID.randomUUID().toString(), "", "");
	}
	
	public Question(String id, String text, String type) {
		this.setQuestion_id(id);
		this.setQuestion_text(text);
		this.setType(type);
		
		this.answers = new ArrayList<Answer>();
		this.event_callbacks = new ArrayList<EventCallback>();
	}
	
	public void addAnswer(Answer answer) {
		this.answers.add(answer);
	}

	// Getters/Setters:
	public String getQuestion_id() {
		return this.question_id;
	}

	public String getQuestion_text() {
		return this.question_text;
	}

	public String getType() {
		return this.type;
	}

	public String getUrl() {
		return this.url;
	}

	public ArrayList<Answer> getAnswers() {
		return this.answers;
	}

	public ArrayList<EventCallback> getEvent_callbacks() {
		return this.event_callbacks;
	}

	public void setQuestion_id(String question_id) {
		this.question_id = question_id;
	}

	public void setQuestion_text(String question_text) {
		this.question_text = question_text;
	}

	public void setType(String type) {
		this.type = type;
	}

        public void setUrl( String url )
        {
            if( !url.startsWith( "http" ) && !url.startsWith( "tel:" ))
            {
                url = "tel:" + url;
            }
            this.url = url;
        }
	
	public void setAnswers(ArrayList<Answer> answers) {
		this.answers = answers;
	}

	public void setEvent_callbacks(ArrayList<EventCallback> event_callbacks) {
		this.event_callbacks = event_callbacks;
	}
	
	public static Question fromJson(String json)
	{
	    return fromJSON( json, Question.class );
	}

        public Collection<MediaHint> getMedia_Hints()
        {
            return media_Hints;
        }
    
        public void setMedia_Hints( Collection<MediaHint> media_Hints )
        {
            this.media_Hints = media_Hints;
        }

        public void addMedia_Hint( MediaHint mediaHint )
        {
            media_Hints = media_Hints == null ? new ArrayList<MediaHint>() : media_Hints;
            media_Hints.add( mediaHint );
        }

        public void addEvent_callbacks( EventType eventType, String callbackURL )
        {
            event_callbacks.add( new EventCallback( eventType, callbackURL ) );
        }

        public void addEventCallback( EventCallback eventCallback )
        {
            event_callbacks.add( eventCallback );
        }
}