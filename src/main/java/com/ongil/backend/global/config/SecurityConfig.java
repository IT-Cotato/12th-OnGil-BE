package com.ongil.backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
				// [1] 시스템 및 문서화 관련 (Swagger, H2 Console)
				.requestMatchers("/ping", "/h2-console/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

				// [2] 인증 관련 - 인증 필요
				.requestMatchers("/api/auth/logout", "/api/auth/withdraw").authenticated()

				// [3] 인증 관련 - 인증 불필요 (로그인, 소셜로그인, 토큰갱신)
				.requestMatchers("/api/auth/login").permitAll()
				.requestMatchers("/api/auth/oauth/**").permitAll()
				.requestMatchers("/api/auth/token/refresh").permitAll()

				// [4] 홈 & 광고
				.requestMatchers("/api/home").permitAll()
				.requestMatchers("/api/advertisements/**").permitAll()

				// [5] 상품 관련
				.requestMatchers("/api/products/*/size-guide").authenticated()  // 사이즈 가이드는 인증 필요
				.requestMatchers("/api/products/**").permitAll()  // 나머지는 인증 불필요

				// [6] 브랜드 & 카테고리
				.requestMatchers("/api/brands/**").permitAll()
				.requestMatchers("/api/categories/**").permitAll()

				// [7] 검색
				.requestMatchers("/api/search/**").permitAll()

				// [8] 리뷰 - 조회는 permitAll, 작성/수정/삭제는 authenticated
				.requestMatchers(HttpMethod.GET, "/api/products/*/reviews").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/products/*/reviews/summary").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/reviews/*/details").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/reviews/*/helpful").authenticated()
				.requestMatchers("/api/users/me/reviews/**").authenticated()

				// [9] 장바구니 & 찜 - 전체 인증 필요
				.requestMatchers("/api/carts/**").authenticated()
				.requestMatchers("/api/wishlists/**").authenticated()

				// [10] 사용자 관련
				.requestMatchers(HttpMethod.GET, "/api/users/body-info/**").permitAll()  // 사이즈 옵션, 약관 조회는 인증 불필요
				.requestMatchers("/api/users/me/**").authenticated()
				.requestMatchers("/api/users/**").authenticated()

				// [11] 그 외 모든 요청은 인증 필요
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