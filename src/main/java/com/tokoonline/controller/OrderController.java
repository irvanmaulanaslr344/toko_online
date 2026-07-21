package com.tokoonline.controller;

import com.tokoonline.dto.request.CheckoutRequest;
import com.tokoonline.dto.response.ApiResponse;
import com.tokoonline.dto.response.OrderDetailResponse;
import com.tokoonline.dto.response.OrderResponse;
import com.tokoonline.entity.Order;
import com.tokoonline.entity.User;
import com.tokoonline.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> checkout(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CheckoutRequest request) {
        Order order = orderService.createOrder(user, request);
        OrderDetailResponse response = orderService.getOrderDetail(order.getId(), user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Pesanan berhasil dibuat", response));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMyOrders(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.success("Berhasil", orderService.getBuyerOrders(user, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getOrderDetail(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Berhasil", orderService.getOrderDetail(id, user)));
    }
}