package com.ongil.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // csrf().disable() 신버전 문법
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ping").permitAll()  // 오류 없이 허용
                        .anyRequest().permitAll()              // 임시로 전체 허용
                )
                .formLogin(form -> form.disable()); // 기본 /login 페이지 비활성화

        return http.build();
    }
}
