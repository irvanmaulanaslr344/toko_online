package com.tokoonline.controller;

import com.tokoonline.dto.request.UpdateOrderStatusRequest;
import com.tokoonline.dto.response.ApiResponse;
import com.tokoonline.dto.response.OrderDetailResponse;
import com.tokoonline.dto.response.OrderResponse;
import com.tokoonline.entity.User;
import com.tokoonline.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller/orders")
@RequiredArgsConstructor
public class SellerOrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getSellerOrders(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.success("Berhasil",
                        orderService.getSellerOrders(user, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getOrderDetail(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Berhasil", orderService.getOrderDetail(id, user)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> updateOrderStatus(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        orderService.updateOrderStatus(id, user, request.getStatus());
        OrderDetailResponse response = orderService.getOrderDetail(id, user);
        return ResponseEntity.ok(ApiResponse.success("Status pesanan diperbarui", response));
    }
}