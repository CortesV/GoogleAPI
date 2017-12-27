package com.example.google;

import com.google.api.client.http.HttpHeaders;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Thread;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/xml/services/json/google/gmail")
public class Quick {

	@Autowired
	private static Quickstart quickstart;

	private static String token;
	private static String refresh;

	@RequestMapping(value = "/oauth2callback", method = RequestMethod.GET, produces = "application/json")
	public List<Thread> getCode(@RequestParam("code") String code) throws IOException {
		List<Thread> threads = new ArrayList<>();
		if (token == null) {
			token = getRefreshToken(code);
		}
		if (token != null) {
			Gmail service = quickstart.getGmailService(getAccessToken("1/zWWravKPmcWTK5Y43M5WbBc2XqA1bZqZ99OpuI28HSI"));
			String user = "me";
			threads = quickstart.listThreadsWithLabels(service, user, null);

		}
		return threads;
	}

	public static String getRefreshToken(String code) throws IOException {

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("https://accounts.google.com/o/oauth2/token");
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
			nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
			nameValuePairs.add(new BasicNameValuePair("client_id", "597484739378-7s1d50hk05mu077umbgd215ilfi1rd06.apps.googleusercontent.com"));
			nameValuePairs.add(new BasicNameValuePair("client_secret", "8Y20xmqTeEB1HZV5vUwh1BE9"));
			nameValuePairs.add(new BasicNameValuePair("code", code));
			nameValuePairs.add(new BasicNameValuePair("redirect_uri", "http://localhost:8080/oauth2callback"));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			org.apache.http.HttpResponse response = client.execute(post);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer buffer = new StringBuffer();
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				buffer.append(line);
			}

			JSONObject json = new JSONObject(buffer.toString());
			String accessToken = json.getString("access_token");
			refresh = json.getString("refresh_token");
			return accessToken;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String getAccessToken(String refreshToken) {

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("https://accounts.google.com/o/oauth2/token");
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
			nameValuePairs.add(new BasicNameValuePair("grant_type", "refresh_token"));
			nameValuePairs.add(new BasicNameValuePair("client_id", "597484739378-7s1d50hk05mu077umbgd215ilfi1rd06.apps.googleusercontent.com"));
			nameValuePairs.add(new BasicNameValuePair("client_secret", "8Y20xmqTeEB1HZV5vUwh1BE9"));
			nameValuePairs.add(new BasicNameValuePair("refresh_token", refreshToken));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			org.apache.http.HttpResponse response = client.execute(post);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer buffer = new StringBuffer();
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				buffer.append(line);
			}

			JSONObject json = new JSONObject(buffer.toString());
			String accessToken = json.getString("access_token");

			return accessToken;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}
}
