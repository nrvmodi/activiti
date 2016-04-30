package com.nirav.modi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.nirav.modi.service.MyService;

@SpringBootApplication
public class SpringActivitiApplication {

	@Autowired
	private MyService myService;

	public static void main(String[] args) {
		SpringApplication.run(SpringActivitiApplication.class, args);
	}

	@Bean
	public CommandLineRunner init() {

		return new CommandLineRunner() {
			public void run(String... strings) throws Exception {
				myService.createDemoUsers();
			}
		};

	}
}
