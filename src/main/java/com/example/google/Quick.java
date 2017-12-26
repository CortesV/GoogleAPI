package com.example.google;

import com.google.api.services.gmail.Gmail;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(value = "")
public class Quick {

	@Autowired
	private static Quickstart quickstart;

	@RequestMapping(value = "/oauth2callback", method = RequestMethod.GET, produces = "application/json")
	public Integer getPush(@RequestParam("code") String code) throws IOException {
		System.out.println(code);
		/*Gmail service = quickstart.getGmailService(code);
		String user = "me";
		quickstart.labels(service, user);*/
		String s = getRefreshToken(code);
		return 200;
	}

	@RequestMapping(value = "/test", method = RequestMethod.GET, produces = "application/json")
	public Integer get() throws IOException {

		return 200;
	}

	public static String getRefreshToken(String code) {

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

			org.apache.http.HttpResponse response = client.execute(post);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer buffer = new StringBuffer();
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				buffer.append(line);
			}

			JSONObject json = new JSONObject(buffer.toString());
			String refreshToken = json.getString("access_token");
			return refreshToken;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
