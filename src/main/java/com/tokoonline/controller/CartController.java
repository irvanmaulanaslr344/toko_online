package com.tokoonline.controller;

import com.tokoonline.dto.request.CartRequest;
import com.tokoonline.dto.response.ApiResponse;
import com.tokoonline.dto.response.CartResponse;
import com.tokoonline.entity.User;
import com.tokoonline.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CartResponse>>> getCart(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("Berhasil", cartService.getCartItems(user)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CartRequest request) {
        CartResponse response = cartService.addToCart(user, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Produk ditambahkan ke keranjang", response));
    }

    @PutMapping("/{cartId}")
    public ResponseEntity<ApiResponse<Void>> updateCart(
            @AuthenticationPrincipal User user,
            @PathVariable Long cartId,
            @RequestParam Integer quantity) {
        cartService.updateCartQuantity(cartId, user, quantity);
        return ResponseEntity.ok(ApiResponse.success("Keranjang diperbarui"));
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(
            @AuthenticationPrincipal User user,
            @PathVariable Long cartId) {
        cartService.removeFromCart(cartId, user);
        return ResponseEntity.ok(ApiResponse.success("Produk dihapus dari keranjang"));
    }
}