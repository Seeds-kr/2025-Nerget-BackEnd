package com.seeds.NergetBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class NergetBackendApplication {

	public static void main(String[] args) {

		SpringApplication.run(NergetBackendApplication.class, args);
	}

}
