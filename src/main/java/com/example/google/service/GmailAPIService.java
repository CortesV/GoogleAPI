package com.example.google.service;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListThreadsResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.google.api.services.gmail.model.ModifyMessageRequest;
import com.google.api.services.gmail.model.Thread;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class GmailAPIService {

    public List<String> labels(Gmail service, String user) throws IOException {
        ListLabelsResponse listResponse = service.users().labels().list(user).execute();
        List<Label> labels = listResponse.getLabels();
        List<String> labelId = new ArrayList<>(labels.size());
        if (labels.size() == 0) {
            System.out.println("No labels found.");
        } else {
            System.out.println("Labels:");
            for (Label label : labels) {
                System.out.printf("- %s\n", label.getName());
                labelId.add(label.getId());
            }
        }
        return labelId;
    }

    public void listThreadsMatchingQuery(Gmail service, String userId, String query) throws IOException {
        ListThreadsResponse response = service.users().threads().list(userId).setQ(query).execute();

        for (Thread thread : response.getThreads()) {
            System.out.println(thread.toPrettyString());
        }
    }

    public void listThreadsWithLabels(Gmail service, String userId, List<String> labelIds) throws IOException {
        ListThreadsResponse response = service.users().threads().list(userId).setLabelIds(labelIds).execute();

        for (Thread thread : response.getThreads()) {
            System.out.println(thread.toPrettyString());
        }
    }

    public void getThread(Gmail service, String userId,
                                 String threadId) throws IOException {
        Thread response = service.users().threads().get(userId, threadId).execute();
        System.out.println(response.toPrettyString());
    }


    public String getMessage (Gmail service, String userId, String threadId, String messageId, String typeMessage) throws IOException {
        Thread thread = service.users().threads().get(userId, threadId).execute();
        Message message = thread.getMessages().stream().filter(messageData -> messageData.getId().equalsIgnoreCase(messageId)).findFirst().orElse(new Message());
        MessagePart messagePart = message.getPayload().getParts().stream().filter(part -> part.getMimeType().equalsIgnoreCase(typeMessage)).findFirst().orElse(null);
        if (messagePart == null) {
            return StringUtils.EMPTY;
        }
        byte[] decodedBytes = Base64.decodeBase64(messagePart.getBody().encodeData(messagePart.getBody().getData().getBytes()).decodeData());
        return new String(decodedBytes);
    }

    public String getDataFromHeaders (Gmail service, String userId, String threadId, String messageId, String name) throws IOException {
        Thread thread = service.users().threads().get(userId, threadId).execute();
        Message message = thread.getMessages().stream().filter(messageData -> messageData.getId().equalsIgnoreCase(messageId)).findFirst().orElse(new Message());
        if(message.getPayload() == null || CollectionUtils.isEmpty(message.getPayload().getHeaders())) {
            return StringUtils.EMPTY;
        }
        return message.getPayload().getHeaders().stream().filter(messagePartHeader -> messagePartHeader.getName().equalsIgnoreCase(name)).findFirst().map(MessagePartHeader::getValue).orElse("");
    }

    public String getMessageHeader(Message gmailMessage, String headerName) {
        return gmailMessage.getPayload().getHeaders().stream().filter(messagePartHeader -> messagePartHeader.getName().equals(headerName)).findFirst().map(MessagePartHeader::getValue).orElse(StringUtils.EMPTY);
    }

    public MimeMessage createEmail(String subject, String message, String fromEmail, String toEmail) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(fromEmail));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(toEmail));
        email.setSubject(subject);
        email.setText(message);
        email.setHeader("In-Reply-To", String.format("<%s>", toEmail));
        email.setHeader("References", String.format("<%s>", toEmail));
        email.setHeader("To", toEmail);
        email.setHeader("From", fromEmail);
        email.setHeader("Subject", subject);
        email.setHeader("MIME-Version", "1.0");
        email.setHeader("Content-Type", "text/plain");
        return email;
    }

    public Message createMessageWithEmail(MimeMessage emailContent) throws IOException, MessagingException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    public Message sendMessage(Gmail service, String userId, String threadId, String subject, String text, String fromEmail, String toEmail) throws IOException, MessagingException {
        Message message = createMessageWithEmail(createEmail(subject, text, fromEmail, toEmail));
        //message.setThreadId(threadId);
        message = service.users().messages().send(userId, message).execute();
        return message;
    }

    public void setMessageAsViewed(Gmail service, String userId, String messageId) throws IOException {
        ModifyMessageRequest mods = new ModifyMessageRequest().setRemoveLabelIds(Arrays.asList("UNREAD"));
        Message message = service.users().messages().modify(userId, messageId, mods).execute();
    }
}
