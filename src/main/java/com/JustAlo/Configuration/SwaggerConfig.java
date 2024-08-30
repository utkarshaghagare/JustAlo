package com.JustAlo.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("My API")
                        .version("1.0.0")
                        .description("API documentation for my application")
                        .contact(new Contact()
                                .name("Your Name")
                                .url("www.example.com")
                                .email("your-email@example.com")));
    }
}
