package com.example.engineary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class EnginearyApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnginearyApplication.class, args);
	}

}
