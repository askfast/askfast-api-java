
package com.askfast.askfastapi.model;

import com.askfast.model.ModelBase;




public class EventPost extends ModelBase
{

    private String responder;
    private String question_id;
    private String event;
    private String message;
    private Object extras;

    public enum EventType
    {
        delivered, read, answered, timeout, hangup, cancelled, exception, preconnect;
    }

    public EventPost()
    {
    }

    public EventPost( String responder, String question_id, String event, String message,
                      Object extras )
    {
        this.responder = responder;
        this.question_id = question_id;
        this.event = event;
        this.message = message;
        this.extras = extras;
    }

    public String getResponder()
    {
        return responder;
    }

    public String getQuestion_id()
    {
        return question_id;
    }

    public String getEvent()
    {
        return event;
    }

    public Object getMessage()
    {
        return message;
    }

    public Object getExtras()
    {
        return extras;
    }

    public void setExtras( Object extras )
    {
        this.extras = extras;
    }

    public void setResponder( String responder )
    {
        this.responder = responder;
    }

    public void setQuestion_id( String question_id )
    {
        this.question_id = question_id;
    }

    public void setEvent( String event )
    {
        this.event = event;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }

}