package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties.class)
public class EmailClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmailClientApplication.class, args);
	}

}
