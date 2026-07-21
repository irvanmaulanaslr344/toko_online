package com.tokoonline.service;

import com.tokoonline.dto.request.ProductRequest;
import com.tokoonline.dto.response.ProductDetailResponse;
import com.tokoonline.dto.response.ProductResponse;
import com.tokoonline.entity.*;
import com.tokoonline.exception.BadRequestException;
import com.tokoonline.exception.ResourceNotFoundException;
import com.tokoonline.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final DiscountRepository discountRepository;
    private final ReviewRepository reviewRepository;
    private final ProductImageRepository productImageRepository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(String search, Long categoryId,
                                              BigDecimal minPrice, BigDecimal maxPrice,
                                              Pageable pageable) {
        return productRepository.findProducts(search, categoryId, minPrice, maxPrice, pageable)
                .map(this::mapToProductResponse);
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getProductDetail(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produk", id));

        return mapToProductDetailResponse(product);
    }

    @Transactional
    public Product createProduct(User seller, ProductRequest request, List<MultipartFile> images) {
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Kategori", request.getCategoryId()));
        }

        Product product = Product.builder()
                .seller(seller)
                .category(category)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .images(new ArrayList<>())
                .build();

        product = productRepository.save(product);

        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                String filename = fileStorageService.storeFile(images.get(i));
                ProductImage productImage = ProductImage.builder()
                        .product(product)
                        .imagePath(filename)
                        .isPrimary(i == 0)
                        .build();
                product.getImages().add(productImage);
            }
            productRepository.save(product);
        }

        return product;
    }

    @Transactional
    public Product updateProduct(Long id, User seller, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produk", id));

        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new BadRequestException("Anda tidak memiliki akses ke produk ini");
        }

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Kategori", request.getCategoryId()));
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(category);

        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id, User seller) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produk", id));

        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new BadRequestException("Anda tidak memiliki akses ke produk ini");
        }

        List<ProductImage> images = productImageRepository.findByProductId(product.getId());
        for (ProductImage image : images) {
            fileStorageService.deleteFile(image.getImagePath());
        }

        productRepository.delete(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getSellerProducts(Long sellerId, Pageable pageable) {
        return productRepository.findBySellerId(sellerId, pageable)
                .map(this::mapToProductResponse);
    }

    private ProductResponse mapToProductResponse(Product product) {
        Discount discount = discountRepository.findActiveDiscountByProductId(product.getId())
                .orElse(null);

        BigDecimal discountedPrice = calculateDiscountedPrice(product.getPrice(), discount);

        String primaryImage = null;
        List<ProductImage> images = productImageRepository.findByProductId(product.getId());
        if (!images.isEmpty()) {
            primaryImage = images.stream()
                    .filter(ProductImage::getIsPrimary)
                    .findFirst()
                    .map(ProductImage::getImagePath)
                    .orElse(images.get(0).getImagePath());
        }

        String sellerName = null;
        if (product.getSeller() != null) {
            sellerName = product.getSeller().getName();
        }

        String categoryName = null;
        if (product.getCategory() != null) {
            categoryName = product.getCategory().getName();
        }

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .discountedPrice(discountedPrice)
                .stock(product.getStock())
                .categoryName(categoryName)
                .primaryImage(primaryImage)
                .sellerName(sellerName)
                .build();
    }

    private ProductDetailResponse mapToProductDetailResponse(Product product) {
        Discount discount = discountRepository.findActiveDiscountByProductId(product.getId())
                .orElse(null);

        BigDecimal discountedPrice = calculateDiscountedPrice(product.getPrice(), discount);

        List<ProductImage> images = productImageRepository.findByProductId(product.getId());
        List<String> imagePaths = images.stream()
                .map(ProductImage::getImagePath)
                .collect(Collectors.toList());

        List<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(
                product.getId(), Pageable.unpaged()).getContent();

        Double avgRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        String sellerName = null;
        if (product.getSeller() != null) {
            sellerName = product.getSeller().getName();
        }

        String categoryName = null;
        if (product.getCategory() != null) {
            categoryName = product.getCategory().getName();
        }

        return ProductDetailResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .discountedPrice(discountedPrice)
                .discountType(discount != null ? discount.getDiscountType().name() : null)
                .discountValue(discount != null ? discount.getDiscountValue() : null)
                .stock(product.getStock())
                .categoryName(categoryName)
                .sellerName(sellerName)
                .images(imagePaths)
                .averageRating(Math.round(avgRating * 10.0) / 10.0)
                .totalReviews((long) reviews.size())
                .build();
    }

    public BigDecimal calculateDiscountedPrice(BigDecimal price, Discount discount) {
        if (discount == null || !discount.getIsActive()) {
            return price;
        }

        return switch (discount.getDiscountType()) {
            case PERCENTAGE -> {
                BigDecimal discountAmount = price.multiply(discount.getDiscountValue())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                yield price.subtract(discountAmount);
            }
            case FIXED -> price.subtract(discount.getDiscountValue()).max(BigDecimal.ZERO);
        };
    }
}