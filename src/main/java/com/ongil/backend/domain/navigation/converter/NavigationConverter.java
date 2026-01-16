package com.ongil.backend.domain.navigation.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ongil.backend.domain.navigation.dto.response.NavigationResponse;
import com.ongil.backend.domain.navigation.entity.Navigation;

@Component
public class NavigationConverter {

	/**
	 * Navigation → NavigationResponse
	 */
	public NavigationResponse toResponse(Navigation navigation) {
		return NavigationResponse.builder()
			.navigationId(navigation.getId())
			.type(navigation.getType())
			.label(navigation.getLabel())
			.route(navigation.getRoute())
			.iconUrl(navigation.getIconUrl())
			.displayOrder(navigation.getDisplayOrder())
			.enabled(navigation.getEnabled())
			.build();
	}

	/**
	 * Navigation List → NavigationResponse List
	 */
	public List<NavigationResponse> toResponseList(List<Navigation> navigations) {
		return navigations.stream()
			.map(this::toResponse)
			.collect(Collectors.toList());
	}
}
