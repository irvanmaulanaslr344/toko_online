package com.tokoonline.controller;

import com.tokoonline.dto.request.ProductRequest;
import com.tokoonline.dto.response.ApiResponse;
import com.tokoonline.dto.response.ProductDetailResponse;
import com.tokoonline.dto.response.ProductResponse;
import com.tokoonline.entity.Product;
import com.tokoonline.entity.User;
import com.tokoonline.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/seller/products")
@RequiredArgsConstructor
public class SellerProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getMyProducts(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.success("Berhasil",
                        productService.getSellerProducts(user.getId(), pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getMyProduct(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Berhasil", productService.getProductDetail(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ProductRequest request) {
        Product product = productService.createProduct(user, request, null);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Produk berhasil ditambahkan", product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        Product product = productService.updateProduct(id, user, request);
        return ResponseEntity.ok(ApiResponse.success("Produk berhasil diperbarui", product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        productService.deleteProduct(id, user);
        return ResponseEntity.ok(ApiResponse.success("Produk berhasil dihapus"));
    }
}