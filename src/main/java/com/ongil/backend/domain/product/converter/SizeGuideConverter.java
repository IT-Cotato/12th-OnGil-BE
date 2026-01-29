package com.ongil.backend.domain.product.converter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ongil.backend.domain.product.dto.response.SizeGuideResponse.SimilarCustomer;
import com.ongil.backend.domain.product.dto.response.SizeGuideResponse.SizeStatistic;
import com.ongil.backend.domain.product.dto.response.SizeGuideResponse.UserBodyInfo;
import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.user.entity.User;

@Component
public class SizeGuideConverter {

	// Object[] → SizeStatistic 변환
	public List<SizeStatistic> toSizeStatistics(List<Object[]> rawStatistics) {
		if (rawStatistics == null || rawStatistics.isEmpty()) {
			return Collections.emptyList();
		}

		return rawStatistics.stream()
			.map(obj -> SizeStatistic.builder()
				.size((String)obj[0])
				.purchaseCount((Long)obj[1])
				.build())
			.collect(Collectors.toList());
	}

	// Object[] → SimilarCustomer 변환
	public List<SimilarCustomer> toSimilarCustomers(List<Object[]> rawCustomers) {
		if (rawCustomers == null || rawCustomers.isEmpty()) {
			return Collections.emptyList();
		}

		return rawCustomers.stream()
			.map(obj -> SimilarCustomer.builder()
				.height((Integer)obj[0])
				.weight((Integer)obj[1])
				.purchasedSize((String)obj[2])
				.build())
			.collect(Collectors.toList());
	}

	// User + Product → UserBodyInfo 변환 (카테고리별 평소 사이즈 선택)
	public UserBodyInfo toUserBodyInfo(User user, Product product) {
		String categoryName = product.getCategory().getName();
		String usualSize = selectUsualSizeByCategory(user, categoryName);

		return UserBodyInfo.builder()
			.height(user.getHeight())
			.weight(user.getWeight())
			.usualSize(usualSize)
			.build();
	}

	// 카테고리에 따라 적절한 평소 사이즈 필드 선택
	private String selectUsualSizeByCategory(User user, String categoryName) {
		if (categoryName.contains("상의") || categoryName.contains("아우터")) {
			return user.getUsualTopSize();
		} else if (categoryName.contains("하의")) {
			return user.getUsualBottomSize();
		} else if (categoryName.contains("신발")) {
			return user.getUsualShoeSize();
		}
		// 가방, 악세서리 등은 null
		return null;
	}

	// 추천 사이즈 계산 (가장 많이 구매된 사이즈, 동률인 경우 모두 반환)
	public List<String> calculateRecommendedSizes(List<SizeStatistic> statistics) {
		if (statistics == null || statistics.isEmpty()) {
			return null;
		}

		// 최대 구매 횟수 찾기
		Long maxCount = statistics.stream()
			.map(SizeStatistic::getPurchaseCount)
			.max(Long::compareTo)
			.orElse(0L);

		// 최대 구매 횟수와 동일한 사이즈들만 필터링
		return statistics.stream()
			.filter(stat -> stat.getPurchaseCount().equals(maxCount))
			.map(SizeStatistic::getSize)
			.collect(Collectors.toList());
	}
}