package com.ongil.backend.domain.search.entity;

import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.global.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "search_history",
	indexes = {
		@Index(name = "idx_user_id_created_at", columnList = "user_id, created_at DESC")
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SearchHistory extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "search_history_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "keyword", nullable = false, length = 100)
	private String keyword;

	@Builder
	public SearchHistory(User user, String keyword) {
		this.user = user;
		this.keyword = keyword;
	}
}
