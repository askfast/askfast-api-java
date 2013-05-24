package com.askfast.model;

public class EventCallback {

	public static final String EVENT_TYPE_TIMEOUT = "timeout";
	public static final String EVENT_TYPE_EXCEPTION = "exception";
	public static final String EVENT_TYPE_HANGUP = "hangup";
	
	private String event;
	private String callback;
	
	public EventCallback() {
		this(null, null);
	}
	public EventCallback(String event, String callback) {
		
		this.event = event;
		this.callback = callback;
	}
	
	public String getEvent() { 
		return this.event; 
	}
	public String getCallback() { 
		return this.callback; 
	}
	
	public void setEvent(String event_type) { 
		this.event = event_type; 
	}
	
	public void setCallback(String callback) { 
		this.callback = callback; 
	}
}
