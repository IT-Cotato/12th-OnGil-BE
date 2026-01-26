package com.ongil.backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.ongil.backend.global.security.jwt.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final CorsConfigurationSource corsConfigurationSource;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors(cors -> cors.configurationSource(corsConfigurationSource))
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)

			.authorizeHttpRequests(auth -> auth
				// [1] 시스템 및 문서화 관련 (Swagger 등)
				.requestMatchers("/ping", "/h2-console/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

				// [2] 인증이 꼭 필요한 기능 (로그아웃, 회원탈퇴)
				.requestMatchers("/auth/logout", "/auth/withdraw").authenticated()

				// [3] 로그인/회원가입 관련 (모두 허용)
				.requestMatchers("/auth/oauth/kakao", "/auth/oauth/google", "/auth/token/refresh").permitAll()
				.requestMatchers("/auth/**").permitAll()

				// [4] 하단 네비게이션 및 주요 도메인
				.requestMatchers("/home").permitAll()          // 홈 화면 (로그인 없이 접근 가능하게 변경)
				.requestMatchers("/magazines/**").permitAll()  // 매거진 (추후 구현 시 로그인 없이 목록 조회 가능하도록 미리 추가)

				// [5] 기존 API 설정
				.requestMatchers("/api/products/*/size-guide").authenticated()
				.requestMatchers("/api/products/**").permitAll()
				.requestMatchers("/api/brands/**").permitAll()
				.requestMatchers("/api/categories/**").permitAll()
				.requestMatchers("/api/search/**").permitAll()

				// [6] 그 외 모든 요청은 로그인(인증) 필요
				.anyRequest().authenticated()
			)

			// JWT 필터 적용
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}