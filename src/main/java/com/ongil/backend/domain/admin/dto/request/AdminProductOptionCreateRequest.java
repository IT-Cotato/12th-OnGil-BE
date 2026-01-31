package com.ongil.backend.domain.admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "상품 옵션 등록 요청")
public class AdminProductOptionCreateRequest {

	@Schema(description = "상품 ID", example = "1")
	private Long productId;

	@Schema(description = "사이즈", example = "M")
	private String size;

	@Schema(description = "색상", example = "화이트")
	private String color;

	@Schema(description = "재고 수량", example = "100")
	private Integer stock;
}
