package com.example.google.controller;

import com.example.google.configuration.GmailServiceWithToken;
import com.example.google.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/xml/services/json/google/")
public class GoogleAPICallback {

    @Autowired
    private GmailServiceWithToken gmailService;

    @Autowired
    private TokenService tokenService;

    @RequestMapping(value = "gmail/oauth2callback", method = RequestMethod.GET, produces = "application/json")
    public Map<String, String> oauth2callback(@RequestParam("code") String code){
        return tokenService.getRefreshToken(code);
    }
}
