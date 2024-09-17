package com.vw.tide_eye;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class TideEyeApplication {

	public static void main(String[] args) {
		SpringApplication.run(TideEyeApplication.class, args);
	}

}
