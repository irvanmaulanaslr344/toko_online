package com.tokoonline.service;

import com.tokoonline.dto.response.DashboardResponse;
import com.tokoonline.entity.User;
import com.tokoonline.repository.DiscountRepository;
import com.tokoonline.repository.OrderRepository;
import com.tokoonline.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final DiscountRepository discountRepository;

    public DashboardResponse getSellerDashboard(User seller) {
        Long totalProducts = productRepository.countBySellerId(seller.getId());
        Long totalOrders = orderRepository.countBySellerId(seller.getId());
        BigDecimal totalRevenue = orderRepository.sumRevenueBySellerId(seller.getId());
        Long activeDiscounts = discountRepository.findAll().stream()
                .filter(d -> d.getProduct().getSeller().getId().equals(seller.getId())
                        && d.getIsActive())
                .count();

        return DashboardResponse.builder()
                .totalProducts(totalProducts)
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .activeDiscounts(activeDiscounts)
                .build();
    }
}