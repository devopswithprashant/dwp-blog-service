package com.devopswithprashant.api.blog;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // registry.addMapping("/**") // Allow all endpoints
                //         .allowedOrigins("http://localhost:3000") // React app's origin
                //         .allowedMethods("GET", "POST", "PUT", "DELETE") // Allowed HTTP methods
                //         .allowedHeaders("*"); // Allowed headers
                registry.addMapping("/**") // Allow all endpoints
                        .allowedOrigins("*") // React app's origin
                        .allowedMethods("GET", "POST", "PUT", "DELETE") // Allowed HTTP methods
                        .allowedHeaders("*"); // Allowed headers
            }
        };
    }
}
