package com.seeds.NergetBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableJpaAuditing
@SpringBootApplication
public class NergetBackendApplication {

	public static void main(String[] args) {

		SpringApplication.run(NergetBackendApplication.class, args);
	}

}
