package com.manage.contact.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;


@Configuration
public class MiscConfig {
	
	@Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
	
	@Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
			                .info(new Info().title("Contact Management API")
			                        		.description("Spring Boot application for managing contacts"));
    }
}
