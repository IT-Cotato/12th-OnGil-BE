package com.ongil.backend.domain.navigation.entity;

import com.ongil.backend.domain.navigation.enums.NavigationType;
import com.ongil.backend.global.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "navigations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Navigation extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "navigation_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private NavigationType type;

	@Column(nullable = false, length = 100)
	private String label;

	@Column(nullable = false, length = 200)
	private String route;

	@Column(name = "icon_url", length = 500)
	private String iconUrl;

	@Column(name = "display_order", nullable = false)
	private Integer displayOrder;

	@Column(nullable = false)
	private Boolean enabled = true;

	@Builder
	public Navigation(NavigationType type, String label, String route, String iconUrl, Integer displayOrder, Boolean enabled) {
		this.type = type;
		this.label = label;
		this.route = route;
		this.iconUrl = iconUrl;
		this.displayOrder = displayOrder;
		this.enabled = enabled != null ? enabled : true;
	}
}
