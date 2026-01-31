package com.ongil.backend.domain.admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "상품 옵션 등록 요청")
public class AdminProductOptionCreateRequest {

	@NotNull(message = "상품 ID는 필수입니다.")
	@Schema(description = "상품 ID", example = "1")
	private Long productId;

	@NotBlank(message = "사이즈는 필수입니다.")
	@Schema(description = "사이즈", example = "M")
	private String size;

	@NotBlank(message = "색상은 필수입니다.")
	@Schema(description = "색상", example = "화이트")
	private String color;

	@NotNull(message = "재고는 필수입니다.")
	@Schema(description = "재고 수량", example = "100")
	private Integer stock;
}
