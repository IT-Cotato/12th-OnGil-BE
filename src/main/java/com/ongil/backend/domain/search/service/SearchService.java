package com.ongil.backend.domain.search.service;

import static com.ongil.backend.domain.search.validator.SearchValidator.*;

import com.ongil.backend.domain.search.document.ProductDocument;
import com.ongil.backend.domain.search.document.SearchLogDocument;
import com.ongil.backend.domain.search.dto.response.SearchResDto;
import com.ongil.backend.domain.search.repository.SearchLogRepository;
import com.ongil.backend.domain.search.validator.SearchValidator;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

	private final ElasticsearchOperations elasticsearchOperations;
	private final SearchLogRepository searchLogRepository;
	private final RecentSearchService recentSearchService;

	// 통합 검색 (현재는 사용X, product에서 처리)
	public SearchResDto search(String query, Long userId) {
		String keyword = SearchValidator.normalize(query);
		if (keyword.isEmpty()) return SearchResDto.of(List.of(), List.of());

		NativeQuery nativeQuery = buildSearchQuery(keyword);
		SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(nativeQuery, ProductDocument.class);

		List<ProductDocument> products = searchHits.getSearchHits().stream()
			.map(SearchHit::getContent)
			.toList();

		if (!products.isEmpty()) {
			saveSearchLog(keyword, userId);
			if (userId != null) {
				try {
					recentSearchService.saveRecentSearch(userId, keyword);
				} catch (Exception e) {
					log.error("Redis 최근 검색어 저장 실패: {}", e.getMessage());
				}
			}
			return SearchResDto.of(products, List.of());
		}

		List<String> alternatives = recommendAlternatives(keyword, 4);
		return SearchResDto.of(List.of(), alternatives);
	}

	public List<String> recommendAlternatives(String keyword, int limit) {
		NativeQuery nativeQuery = NativeQuery.builder()
			.withQuery(q -> q.fuzzy(f -> f
				.field("keyword")
				.value(keyword)
				.fuzziness("2")
				.prefixLength(1)
			))
			.withMinScore(0.1f)
			.withMaxResults(limit * 2)
			.build();

		SearchHits<SearchLogDocument> hits = elasticsearchOperations.search(nativeQuery, SearchLogDocument.class);

		return hits.getSearchHits().stream()
			.map(hit -> hit.getContent().getKeyword())
			.filter(k -> !k.equals(keyword))
			.distinct()
			.limit(limit)
			.toList();
	}

	public void saveSearchLog(String keyword, Long userId) {
		if (isNoiseKeyword(keyword)) return;

		SearchLogDocument log = SearchLogDocument.builder()
			.id(UUID.randomUUID().toString())
			.keyword(keyword)
			.timestamp(LocalDateTime.now())
			.userId(userId)
			.build();

		searchLogRepository.save(log);
	}

	// 실시간 자동완성
	public List<String> getAutocomplete(String query) {
		NativeQuery nativeQuery = NativeQuery.builder()
			.withQuery(q -> q.bool(b -> b
				.should(s -> s.match(m -> m.field("name.autocomplete").query(query).boost(3.0f)))
				.should(s -> s.match(m -> m.field("categoryName.autocomplete").query(query).boost(2.0f)))
				.should(s -> s.match(m -> m.field("brandName.autocomplete").query(query).boost(1.0f)))
			))
			.withMaxResults(20)
			.build();

		SearchHits<ProductDocument> hits = elasticsearchOperations.search(nativeQuery, ProductDocument.class);

		// ES 점수 순서 그대로 카테고리/상품명/브랜드 모두 수집
		LinkedHashSet<String> suggestions = new LinkedHashSet<>();
		for (SearchHit<ProductDocument> hit : hits) {
			ProductDocument doc = hit.getContent();
			if (doc.getCategoryName() != null) suggestions.add(doc.getCategoryName());
			if (doc.getName() != null) suggestions.add(doc.getName());
			if (doc.getBrandName() != null) suggestions.add(doc.getBrandName());
		}

		return suggestions.stream()
			.limit(12)
			.toList();
	}

	public List<String> getTopKeywords() {
		NativeQuery query = NativeQuery.builder()
			.withMaxResults(0)
			.withAggregation("top_keywords", Aggregation.of(a -> a
				.terms(t -> t
					.field("keyword")
					.size(50)
				)
			))
			.build();

		SearchHits<SearchLogDocument> hits = elasticsearchOperations.search(query, SearchLogDocument.class);
		if (hits.getAggregations() == null) return List.of();

		ElasticsearchAggregations aggregations = (ElasticsearchAggregations) hits.getAggregations();
		ElasticsearchAggregation aggregation = aggregations.get("top_keywords");
		if (aggregation == null) return List.of();

		var aggregate = aggregation.aggregation().getAggregate();
		if (aggregate != null && aggregate.isSterms()) {
			StringTermsAggregate terms = aggregate.sterms();
			List<String> rawKeywords = terms.buckets().array().stream()
				.map(bucket -> bucket.key().stringValue())
				.toList();

			return rawKeywords.stream()
				.filter(k -> k.length() >= 2) // 1글자 제거
				.filter(k -> {
					// 리스트 전체를 뒤져서 더 긴 단어가 있는지 확인
					return rawKeywords.stream()
						.noneMatch(other -> !k.equals(other) && other.startsWith(k));
				})
				.limit(5)
				.toList();
		}
		return List.of();
	}

	// 검색어를 정규화된 대표 키워드로 변환
	public String extractRepresentativeKeyword(String query) {
		NativeQuery nativeQuery = NativeQuery.builder()
			.withQuery(q -> q.bool(b -> b
				.should(s -> s.match(m -> m.field("brandName.autocomplete").query(query).boost(3.0f)))
				.should(s -> s.match(m -> m.field("categoryName.autocomplete").query(query).boost(2.0f)))
				.should(s -> s.match(m -> m.field("name.autocomplete").query(query).boost(1.0f)))
			))
			.withMaxResults(1)
			.build();

		SearchHits<ProductDocument> hits = elasticsearchOperations.search(nativeQuery, ProductDocument.class);
		if (hits.isEmpty()) return null;

		ProductDocument top = hits.getSearchHits().get(0).getContent();

		// 가장 점수 높은 문서에서 query와 가장 유사한 필드 값 반환
		String q = query.toLowerCase();
		if (top.getBrandName() != null && top.getBrandName().toLowerCase().contains(q)) return top.getBrandName();
		if (top.getCategoryName() != null && top.getCategoryName().toLowerCase().contains(q)) return top.getCategoryName();
		if (top.getName() != null && top.getName().toLowerCase().contains(q)) return top.getName();

		// 완전 일치 없으면 카테고리 > 브랜드 순으로 대표값 반환
		if (top.getCategoryName() != null) return top.getCategoryName();
		if (top.getBrandName() != null) return top.getBrandName();
		return null;
	}
	public List<Long> getProductIdsByQuery(String query) {
		String keyword = normalize(query);
		if (keyword.isEmpty()) return List.of();

		NativeQuery nativeQuery = buildSearchQuery(keyword);

		SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(nativeQuery, ProductDocument.class);
		return searchHits.getSearchHits().stream()
			.map(hit -> hit.getContent().getId())
			.collect(Collectors.toList());
	}

	// 공통 쿼리 생성 로직
	private NativeQuery buildSearchQuery(String keyword) {
		return NativeQuery.builder()
			.withQuery(q -> q.bool(b -> b
				// 형태소 분석 기반 검색
				.should(s -> s.multiMatch(mm -> mm
					.query(keyword)
					.fields("name^5", "brandName^3", "categoryName^2", "colors^1")
					.operator(Operator.Or)
				))
				// 자동완성 필드 매칭
				.should(s -> s.match(m -> m
					.field("name.autocomplete")
					.query(keyword)
					.boost(2.0f)
				))
				// 부분 일치 검색
				.should(s -> s.wildcard(w -> w
					.field("name.autocomplete")
					.value("*" + keyword + "*")
					.boost(0.8f)
				))
			))
			.withMinScore(0.2f)
			.build();
	}
}
