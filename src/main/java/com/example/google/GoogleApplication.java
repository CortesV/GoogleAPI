package com.example.google;

import com.google.api.services.gmail.Gmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class GoogleApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(GoogleApplication.class, args);
	}
}
