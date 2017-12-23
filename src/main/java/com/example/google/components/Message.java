package com.example.google.components;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {

	@JsonProperty("data")
	private String data;

	@JsonProperty("message_id")
	private String messageId;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
}
