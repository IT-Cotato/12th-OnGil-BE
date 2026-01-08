package com.ongil.backend.domain.auth.client.google;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.ongil.backend.domain.auth.dto.response.GoogleUserInfoResDto;

@FeignClient(name = "googleApiClient", url = "https://www.googleapis.com")
public interface GoogleApiClient {
	@GetMapping("/oauth2/v3/userinfo")
	GoogleUserInfoResDto getUserInfo(@RequestHeader("Authorization") String accessToken);
}
