package com.tokoonline.service;

import com.tokoonline.dto.request.ReviewRequest;
import com.tokoonline.dto.response.ReviewResponse;
import com.tokoonline.entity.OrderItem;
import com.tokoonline.entity.Review;
import com.tokoonline.entity.User;
import com.tokoonline.exception.BadRequestException;
import com.tokoonline.exception.ResourceNotFoundException;
import com.tokoonline.repository.OrderItemRepository;
import com.tokoonline.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;

    public Review createReview(Long orderItemId, User buyer, ReviewRequest request) {
        if (reviewRepository.existsByOrderItemId(orderItemId)) {
            throw new BadRequestException("Produk ini sudah direview");
        }

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item", orderItemId));

        if (!orderItem.getOrder().getBuyer().getId().equals(buyer.getId())) {
            throw new BadRequestException("Anda tidak bisa mereview pesanan ini");
        }

        Review review = Review.builder()
                .orderItem(orderItem)
                .buyer(buyer)
                .product(orderItem.getProduct())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        return reviewRepository.save(review);
    }

    public Page<ReviewResponse> getProductReviews(Long productId, Pageable pageable) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable)
                .map(this::mapToReviewResponse);
    }

    private ReviewResponse mapToReviewResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .buyerName(review.getBuyer().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}