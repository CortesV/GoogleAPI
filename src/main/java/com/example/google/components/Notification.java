package com.example.google.components;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Notification {

	@JsonProperty("message")
	private Message message;

	@JsonProperty("subscription")
	private String subscription;

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public String getSubscription() {
		return subscription;
	}

	public void setSubscription(String subscription) {
		this.subscription = subscription;
	}
}
