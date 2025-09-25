package com.seeds.NergetBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
public class NergetBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(NergetBackendApplication.class, args);
	}

}
