package com.ongil.backend.domain.search.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.product.repository.ProductRepository;
import com.ongil.backend.domain.search.document.ProductDocument;
import com.ongil.backend.domain.search.repository.ProductSearchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchIndexingService {
	private final ProductRepository productRepository;
	private final ProductSearchRepository productSearchRepository;

	// 초기 데이터 전체 동기화
	@Transactional(readOnly = true)
	public void indexAllProducts() {
		List<Product> products = productRepository.findAll();

		List<ProductDocument> documents = products.stream()
			.map(this::toDocument)
			.collect(Collectors.toList());

		productSearchRepository.saveAll(documents);
	}

	// 개별 상품 동기화 (관리자 페이지가 없으므로 현재는 사용 X)
	@Transactional(readOnly = true)
	public void indexProduct(Product product) {
		productSearchRepository.save(toDocument(product));
	}

	// 변환 로직 (MySQL Entity -> ES Document)
	private ProductDocument toDocument(Product product) {
		return ProductDocument.builder()
			.id(product.getId())
			.name(product.getName())
			.colors(product.getColors())
			.brandName(product.getBrand().getName())
			.categoryName(product.getCategory().getName())
			.price(product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getPrice())
			.build();
	}
}
