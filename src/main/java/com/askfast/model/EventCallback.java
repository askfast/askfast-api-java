package com.askfast.model;

import com.askfast.model.EventPost.EventType;

public class EventCallback extends ModelBase {

	public static final String EVENT_TYPE_TIMEOUT = "timeout";
	public static final String EVENT_TYPE_EXCEPTION = "exception";
	public static final String EVENT_TYPE_HANGUP = "hangup";
	
	private EventType event;
	private String callback;
	
	public EventCallback() {
		this(null, null);
	}
	public EventCallback(EventType event, String callback) {
		
		this.event = event;
		this.callback = callback;
	}
	
	public EventType getEvent() { 
		return this.event; 
	}
	public String getCallback() { 
		return this.callback; 
	}
	
	public void setEvent(EventType event_type) { 
		this.event = event_type; 
	}
	
	public void setCallback(String callback) { 
		this.callback = callback; 
	}
	
        public static EventCallback fromJSON( String json )
        {
            return fromJSON( json, EventCallback.class );
        }
}
