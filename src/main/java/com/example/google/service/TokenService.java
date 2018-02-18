package com.example.google.service;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TokenService {

    private static final Logger LOG = Logger.getLogger(TokenService.class.getName());

    public Map<String, String> getRefreshToken(String code) {
        Map<String, String> tokens = new HashMap<>();
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("https://accounts.google.com/o/oauth2/token");
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
            nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
            nameValuePairs.add(new BasicNameValuePair("client_id", "597484739378-7s1d50hk05mu077umbgd215ilfi1rd06.apps.googleusercontent.com"));
            nameValuePairs.add(new BasicNameValuePair("client_secret", "8Y20xmqTeEB1HZV5vUwh1BE9"));
            nameValuePairs.add(new BasicNameValuePair("code", code));
            nameValuePairs.add(new BasicNameValuePair("redirect_uri", "http://localhost:8080/xml/services/json/google/gmail/oauth2callback"));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            org.apache.http.HttpResponse response = client.execute(post);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer buffer = new StringBuffer();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                buffer.append(line);
            }

            JSONObject json = new JSONObject(buffer.toString());
            tokens.put("access_token", json.getString("access_token"));
            tokens.put("refresh_token", json.getString("refresh_token"));
            return tokens;
        } catch (Exception e) {
            LOG.error("Problem with getting of access token for gmail", e);
        }
        return tokens;
    }

    public String getAccessToken(String refreshToken) {

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

        } catch (Exception e) {
            LOG.error("Problem with refreshing of access token for gmail", e);
        }
        return null;
    }

}
