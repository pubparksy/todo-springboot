package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // 이 어노테이션이 있는 클래스를 스프링 빈으로 만들게 등록
public class WebMvcConfig implements WebMvcConfigurer {
    private final long MAX_AGE_SECS = 3600; // 60분?

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 모든 경로에 대해서 (:8080/todo 말고 :8080/notice 등 모든 주소에)
        registry.addMapping("/**")
                // 요청하는 Origin이 http:localhost:3000일 경우 허락. 추가 가능
                .allowedOrigins("http://localhost:3000")
                // HTTP Request에 REST 함수 GET,POST,PUT,PATCH,DELETE,OPTIONS만 허락.
                .allowedMethods("GET","POST","PUT","PATCH","DELETE","OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true) // 인증에 관한 정보
                .maxAge(MAX_AGE_SECS);
    }
}
