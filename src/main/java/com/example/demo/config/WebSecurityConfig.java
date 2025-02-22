package com.example.demo.config;

import com.example.demo.security.JwtAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@Slf4j
public class WebSecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.disable()) // WebMvcConfig에서 설정했으므로 기본 CORS 설정
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화 (람다를 메소드 참조로 변경)
                .httpBasic(httpBasic -> httpBasic.disable()) // token 사용해서 basic인증 disable
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // session 기반이 아님을 선언
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/auth/**").permitAll() // /와 /auth/** 경로는 인증 안해도 됨.
                        .anyRequest().authenticated() // /와 /auth/**이외의 모든 경로는 인증 해야됨.
                )
                .addFilterAfter(jwtAuthenticationFilter, CorsFilter.class); // JWT 필터 등록
        // 책은 http.addFilterAfter()를 따로 호출했지만 메소드 체이닝으로 연결

        return http.build();
    }
}


