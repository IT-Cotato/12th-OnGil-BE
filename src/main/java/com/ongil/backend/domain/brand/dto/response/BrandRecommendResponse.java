package com.ongil.backend.domain.brand.dto.response;

import com.ongil.backend.domain.product.dto.response.ProductSimpleResponse;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class BrandRecommendResponse {
    private Long id;
    private String name;
    private String logoImageUrl;
    private List<ProductSimpleResponse> products;
}