package com.example.google.controller;

import com.example.google.components.Notification;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/notification")
public class PushNotificationController {

	@RequestMapping(value = "/push", method = RequestMethod.POST, produces = "application/json")
	public Integer getPush(@RequestBody Notification notification){
		return 200;
	}
}
