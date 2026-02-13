package com.ongil.backend.global.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.theokanning.openai.service.OpenAiService;

@Configuration
public class OpenAiConfig {
	@Value("${openai.api-key}")
	private String apiKey;

	@Bean
	public OpenAiService openAiService() {
		return new OpenAiService(apiKey, Duration.ofSeconds(60));
	}
}
