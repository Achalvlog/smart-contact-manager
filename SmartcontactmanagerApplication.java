package com.smart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.smart")
@EnableJpaRepositories(basePackages = "com.smart.dao")
@EntityScan(basePackages = "com.smart.entities")
public class SmartcontactmanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartcontactmanagerApplication.class, args);
	}

}
