package com.example.google;

import com.example.google.configuration.GmailServiceWithFile;
import com.example.google.configuration.GmailServiceWithToken;
import com.example.google.service.GmailAPIService;
import com.example.google.service.TokenService;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Arrays;

@SpringBootApplication
public class GoogleApplication {

	private static TokenService tokenService = new TokenService();
	private static GmailServiceWithToken gmailService = new GmailServiceWithToken();
	private static GmailServiceWithFile gmailServiceFile = new GmailServiceWithFile();
	private static GmailAPIService gmailAPIService = new GmailAPIService();

	public static void main(String[] args) throws IOException, MessagingException {
		SpringApplication.run(GoogleApplication.class, args);
		Gmail service = gmailService.getGmailService(tokenService.getAccessToken("1/YkMZLVYMQZzWrkrKGSq4cEQI472oHjvDSjj-UOOIVG8"));
		String user = "me";

		System.out.println("Labels: \n");
		gmailAPIService.labels(service, user);
		System.out.println("\n");

		System.out.println("List thread with labels: \n");
		gmailAPIService.listThreadsWithLabels(service, user, Arrays.asList("UNREAD", "INBOX", "CATEGORY_PERSONAL"));
		System.out.println("\n");


		System.out.println("List threads: \n");
		gmailAPIService.listThreadsMatchingQuery(service, user, "from:speedgear250t@gmail.com");
		System.out.println("\n");


		System.out.println("List messages: \n");
		ListMessagesResponse messages = service.users().messages().list(user).setLabelIds(Arrays.asList("UNREAD", "INBOX", "CATEGORY_PERSONAL")).execute();
		for (Message message : messages.getMessages()) {
			System.out.println(message.toPrettyString());
		}
		System.out.println("\n");


		System.out.println("Thread: \n");
		gmailAPIService.getThread(service, user, "161a490c452861b1");
		System.out.println("\n");


		System.out.println("Message: \n");
		Message message = service.users().messages().get(user, "161a490c452861b1").execute();
		System.out.println(message.toPrettyString());
		System.out.println("\n");


		System.out.println("Message content: \n");
		System.out.println(gmailAPIService.getMessage(service, user, "161a490c452861b1", "161a490c452861b1", "text/plain"));
		System.out.println("\n");


		System.out.println("Message header: \n");
		System.out.println(gmailAPIService.getDataFromHeaders(service, user, "161a490c452861b1", "161a490c452861b1", "Subject"));
		System.out.println("\n");

		gmailAPIService.sendMessage(service, user, null, "Test send message", "Bla bla bla", "vitya2018test@gmail.com",
				"speedgear250t@gmail.com");


		gmailAPIService.setMessageAsViewed(service, user, "161aa234e1501682");
	}
}
