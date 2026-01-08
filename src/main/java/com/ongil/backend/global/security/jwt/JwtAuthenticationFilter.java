package com.ongil.backend.global.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// 이후 모든 요청에서 JWT를 검증하고 인증 상태로 만들어줌
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		// 1. 요청 헤더에서 토큰 추출
		String token = resolveToken(request);

		// 2. 토큰이 유효한지 검사
		if (token != null && jwtTokenProvider.validateToken(token)) {
			Long userId = jwtTokenProvider.getUserId(token);

			// 3. 스프링 시큐리티 인증 객체 생성
			List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

			Authentication auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);

			// 4. SecurityContext에 인증 정보 저장 (이 요청이 실행되는 동안만 유효)
			SecurityContextHolder.getContext().setAuthentication(auth);
		}

		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
}
