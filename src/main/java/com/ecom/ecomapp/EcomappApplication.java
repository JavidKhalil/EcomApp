package com.ecom.ecomapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EcomappApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcomappApplication.class, args);
	}

}
