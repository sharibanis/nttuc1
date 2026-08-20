package com.sharib.nttuc1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class Nttuc1Application {
	private static final Logger log = LoggerFactory.getLogger(Nttuc1Application.class);

	public static void main(String[] args) {
		log.info("Starting Nttuc1Application... ");
		SpringApplication.run(Nttuc1Application.class, args);
	}

}
