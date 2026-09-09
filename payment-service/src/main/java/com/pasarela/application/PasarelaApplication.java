package com.pasarela.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication(scanBasePackages = "com.pasarela")
@EnableReactiveMongoRepositories(
        basePackages = "com.pasarela.infrastructure.drivenadapter.mongo.data"
)
public class PasarelaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PasarelaApplication.class, args);
	}

}
