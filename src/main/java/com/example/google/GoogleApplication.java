package com.example.google;

import com.google.api.services.gmail.Gmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class GoogleApplication {

	@Autowired
	private static Quickstart quickstart;

	public static void main(String[] args) throws IOException {
		SpringApplication.run(GoogleApplication.class, args);

		Gmail service = quickstart.getGmailService();
		String user = "me";
		quickstart.getThread(service, user, "160736772b79645e");
	}
}
