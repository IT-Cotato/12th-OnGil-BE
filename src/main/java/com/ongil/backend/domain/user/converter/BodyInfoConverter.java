package com.ongil.backend.domain.user.converter;

import com.ongil.backend.domain.user.dto.response.BodyInfoResponse;
import com.ongil.backend.domain.user.dto.response.SizeOptionsResponse;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.domain.user.enums.BottomSize;
import com.ongil.backend.domain.user.enums.ShoeSize;
import com.ongil.backend.domain.user.enums.TopSize;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BodyInfoConverter {

	/**
	 * User 엔티티를 BodyInfoResponse로 변환
	 * 5개 필드 중 하나라도 null이면 hasBodyInfo = false
	 */
	public static BodyInfoResponse toResponse(User user) {
		boolean hasBodyInfo = user.getHeight() != null
			&& user.getWeight() != null
			&& user.getUsualTopSize() != null
			&& user.getUsualBottomSize() != null
			&& user.getUsualShoeSize() != null;

		return BodyInfoResponse.builder()
			.hasBodyInfo(hasBodyInfo)
			.height(user.getHeight())
			.weight(user.getWeight())
			.topSize(user.getUsualTopSize())
			.bottomSize(user.getUsualBottomSize())
			.shoeSize(user.getUsualShoeSize())
			.build();
	}

	/**
	 * Enum에서 사이즈 옵션 목록 생성
	 */
	public static SizeOptionsResponse toSizeOptionsResponse() {
		return SizeOptionsResponse.builder()
			.topSizes(TopSize.getAllDisplayNames())
			.bottomSizes(BottomSize.getAllDisplayNames())
			.shoeSizes(ShoeSize.getAllDisplayNames())
			.build();
	}
}