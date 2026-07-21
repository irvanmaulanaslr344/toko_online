package com.tokoonline.controller;

import com.tokoonline.dto.request.DiscountRequest;
import com.tokoonline.dto.response.ApiResponse;
import com.tokoonline.entity.Discount;
import com.tokoonline.entity.User;
import com.tokoonline.service.DiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seller/discounts")
@RequiredArgsConstructor
public class SellerDiscountController {

    private final DiscountService discountService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Discount>>> getMyDiscounts(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                ApiResponse.success("Berhasil", discountService.getSellerDiscounts(user)));
    }

    @PostMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<Discount>> createDiscount(
            @AuthenticationPrincipal User user,
            @PathVariable Long productId,
            @Valid @RequestBody DiscountRequest request) {
        Discount discount = discountService.createDiscount(productId, user, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Diskon berhasil ditambahkan", discount));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Discount>> updateDiscount(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody DiscountRequest request) {
        Discount discount = discountService.updateDiscount(id, user, request);
        return ResponseEntity.ok(ApiResponse.success("Diskon berhasil diperbarui", discount));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDiscount(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        discountService.deleteDiscount(id, user);
        return ResponseEntity.ok(ApiResponse.success("Diskon berhasil dihapus"));
    }
}