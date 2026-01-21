package com.ongil.backend.global.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

	@Value("${spring.data.redis.host}")
	private String host;

	@Value("${spring.data.redis.port}")
	private int port;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(host, port);
	}

	@Bean
	public StringRedisTemplate stringRedisTemplate(
		RedisConnectionFactory connectionFactory
	) {
		return new StringRedisTemplate(connectionFactory);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(
		RedisConnectionFactory connectionFactory
	) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);

		StringRedisSerializer stringSerializer = new StringRedisSerializer();
		template.setKeySerializer(stringSerializer);
		template.setHashKeySerializer(stringSerializer);

		GenericJackson2JsonRedisSerializer jsonSerializer =
			new GenericJackson2JsonRedisSerializer();
		template.setValueSerializer(jsonSerializer);
		template.setHashValueSerializer(jsonSerializer);

		template.afterPropertiesSet();
		return template;
	}
}