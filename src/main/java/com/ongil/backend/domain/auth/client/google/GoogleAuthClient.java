package com.ongil.backend.domain.auth.client.google;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ongil.backend.domain.auth.dto.response.GoogleTokenResDto;

@FeignClient(name = "googleAuthClient", url = "https://oauth2.googleapis.com")
public interface GoogleAuthClient {
	@PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	GoogleTokenResDto getAccessToken(
		@RequestParam("grant_type") String grantType,
		@RequestParam("client_id") String clientId,
		@RequestParam("client_secret") String clientSecret,
		@RequestParam("redirect_uri") String redirectUri,
		@RequestParam("code") String code
	);
}
