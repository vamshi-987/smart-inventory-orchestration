package com.vamshi.stockflow_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class StockflowBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockflowBackendApplication.class, args);
	}

}
