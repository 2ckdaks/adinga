package com.adinga.trigger_engine_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TriggerEngineServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TriggerEngineServiceApplication.class, args);
	}

}
