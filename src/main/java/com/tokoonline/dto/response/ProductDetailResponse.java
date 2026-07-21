package com.tokoonline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private String discountType;
    private BigDecimal discountValue;
    private Integer stock;
    private String categoryName;
    private String sellerName;
    private List<String> images;
    private Double averageRating;
    private Long totalReviews;
}