package com.tokoonline.controller;

import com.tokoonline.dto.request.ReviewRequest;
import com.tokoonline.dto.response.ApiResponse;
import com.tokoonline.dto.response.ReviewResponse;
import com.tokoonline.entity.Review;
import com.tokoonline.entity.User;
import com.tokoonline.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/order-items/{orderItemId}")
    public ResponseEntity<ApiResponse<Review>> createReview(
            @AuthenticationPrincipal User user,
            @PathVariable Long orderItemId,
            @Valid @RequestBody ReviewRequest request) {
        Review review = reviewService.createReview(orderItemId, user, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review berhasil ditambahkan", review));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getProductReviews(
            @PathVariable Long productId,
            Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.success("Berhasil",
                        reviewService.getProductReviews(productId, pageable)));
    }
}