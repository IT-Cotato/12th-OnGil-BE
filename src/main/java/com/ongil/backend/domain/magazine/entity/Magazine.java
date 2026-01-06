package com.ongil.backend.domain.magazine.entity;

import java.util.*;

import com.ongil.backend.domain.magazine.enums.*;
import com.ongil.backend.global.common.entity.BaseEntity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "magazines")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Magazine extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "magazine_id")
	private Long id;

	@Column(nullable = false, length = 200)
	private String title;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Column(name = "thumbnail_image_url", length = 500)
	private String thumbnailImageUrl;

	@Column(name = "magazine_image_urls", columnDefinition = "TEXT")
	private String magazineImageUrls;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MagazineCategory category;

	@Column(name = "author_name", length = 50)
	private String authorName;

	@Column(name = "view_count", nullable = false)
	private Integer viewCount = 0;

	@OneToMany(mappedBy = "magazine")
	private List<MagazineComment> comments = new ArrayList<>();

	@Builder
	public Magazine(String title, String content, String thumbnailImageUrl,
		String magazineImageUrls, MagazineCategory category, String authorName) {
		this.title = title;
		this.content = content;
		this.thumbnailImageUrl = thumbnailImageUrl;
		this.magazineImageUrls = magazineImageUrls;
		this.category = category;
		this.authorName = authorName;
	}
}
