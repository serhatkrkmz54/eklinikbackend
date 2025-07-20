package com.eklinik.eklinikapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EklinikapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EklinikapiApplication.class, args);
	}

}
