package com.ongil.backend.domain.category.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.ongil.backend.domain.category.dto.response.CategoryRandomResponse;
import com.ongil.backend.domain.category.dto.response.CategoryResponse;
import com.ongil.backend.domain.category.dto.response.CategorySimpleResponse;
import com.ongil.backend.domain.category.dto.response.SubCategoryResponse;
import com.ongil.backend.domain.category.service.CategoryService;
import com.ongil.backend.global.common.dto.DataResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Category", description = "카테고리 API")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;

	@Operation(summary = "전체 카테고리 조회", description = "상위 카테고리와 하위 카테고리 트리 구조로 조회합니다.")
	@GetMapping
	public DataResponse<List<CategoryResponse>> getAllCategories() {
		List<CategoryResponse> categories = categoryService.getAllCategories();
		return DataResponse.from(categories);
	}

	@Operation(summary = "하위 카테고리 조회", description = "특정 상위 카테고리의 하위 카테고리 목록을 조회합니다.")
	@GetMapping("/{categoryId}/sub-categories")
	public DataResponse<List<SubCategoryResponse>> getSubCategories(
		@PathVariable Long categoryId
	) {
		List<SubCategoryResponse> subCategories = categoryService.getSubCategories(categoryId);
		return DataResponse.from(subCategories);
	}

	@Operation(summary = "랜덤 카테고리 조회", description = "홈 화면용 랜덤 카테고리를 조회합니다. 상품 이미지 포함.")
	@GetMapping("/random")
	public DataResponse<List<CategoryRandomResponse>> getRandomCategories(
		@RequestParam(defaultValue = "8") int count
	) {
		List<CategoryRandomResponse> categories = categoryService.getRandomCategories(count);
		return DataResponse.from(categories);
	}

	@Operation(summary = "추천 하위 카테고리 조회", description = "카테고리 탭용 추천 하위 카테고리를 조회합니다. 고정된 목록.")
	@GetMapping("/recommended-sub")
	public DataResponse<List<CategorySimpleResponse>> getRecommendedSubCategories(
		@RequestParam(defaultValue = "8") int count
	) {
		List<CategorySimpleResponse> categories = categoryService.getRecommendedSubCategories(count);
		return DataResponse.from(categories);
	}
}
