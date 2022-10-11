package com.arydz.stockfinder;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "SF Demo API", version = "1.0", description = "Stock Finder Service (Demo) Documentation API v1.0"))
public class StockFinderDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockFinderDemoApplication.class, args);
	}

}
