package com.askfast.model;

public class DialogRequest {

	String fromAddress = null;
	String toAddress = null;
	String url = null;
	
	public DialogRequest () {}
	
	public DialogRequest(String fromAddress, String toAddress, String url) {
		this.fromAddress = fromAddress;
		this.toAddress = toAddress;
		this.url = url;
	}
	
	public String getFromAddress() {
		return fromAddress;
	}
	
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}
	
	public String getToAddress() {
		return toAddress;
	}
	
	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
}
