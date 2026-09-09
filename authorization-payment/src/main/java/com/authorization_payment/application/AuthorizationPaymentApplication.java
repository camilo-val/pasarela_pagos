package com.authorization_payment.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.authorization_payment")
public class AuthorizationPaymentApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthorizationPaymentApplication.class, args);
	}

}
