package com.nhahang.pos.pos_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Cho phép tất cả các đường dẫn API
                .allowedOrigins("*") // Cho phép tất cả các nguồn (Vercel, Localhost...)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Các phương thức được phép
                .allowedHeaders("*") // Cho phép tất cả các Header
                .allowCredentials(false); // Không yêu cầu gửi kèm Cookie/Auth Header nếu dùng dấu *
    }
}