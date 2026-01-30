package com.ongil.backend.domain.magazine.entity;

import java.time.LocalDateTime;
import java.util.*;

import com.ongil.backend.domain.magazine.enums.*;
import com.ongil.backend.global.common.entity.BaseEntity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(
	name = "magazines",
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_magazine_url", columnNames = "url")
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
@Builder
public class Magazine extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "magazine_id")
	private Long id;

	@Column(nullable = false, length = 200)
	private String title;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Column(unique = true)
	private String url;

	private LocalDateTime publishedAt;

	private String press;

	@Column(name = "thumbnail_image_url", length = 500)
	private String thumbnailImageUrl;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MagazineCategory category;

	@Column(name = "author_name", length = 50)
	private String authorName;

	@Column(name = "view_count", nullable = false)
	private Integer viewCount = 0;

	@OneToMany(mappedBy = "magazine")
	private List<MagazineComment> comments = new ArrayList<>();

	public void addViewCount() {
		this.viewCount++;
	}
}
