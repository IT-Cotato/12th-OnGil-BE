package com.ongil.backend.global.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

	private static final List<String> ALLOWED_ORIGINS = List.of(
		"https://ongil-fe.vercel.app",
		"http://localhost:3000",
		"http://3.38.199.67:8080",
		"http://localhost:8080"
	);

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins(ALLOWED_ORIGINS.toArray(new String[0]))
			.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
			.allowedHeaders("*")
			.exposedHeaders("Authorization", "accessToken", "refreshToken")
			.allowCredentials(true)
			.maxAge(3600);
	}

	// SecurityFilterChain에서 사용할 Bean
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(ALLOWED_ORIGINS);
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
		config.setAllowedHeaders(List.of("*"));
		config.setExposedHeaders(List.of("Authorization", "accessToken", "refreshToken"));
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}