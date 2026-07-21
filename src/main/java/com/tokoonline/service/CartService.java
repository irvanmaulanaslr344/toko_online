package com.tokoonline.service;

import com.tokoonline.dto.request.CartRequest;
import com.tokoonline.dto.response.CartResponse;
import com.tokoonline.entity.Cart;
import com.tokoonline.entity.Product;
import com.tokoonline.entity.ProductImage;
import com.tokoonline.entity.User;
import com.tokoonline.exception.BadRequestException;
import com.tokoonline.exception.ResourceNotFoundException;
import com.tokoonline.repository.CartRepository;
import com.tokoonline.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public List<CartResponse> getCartItems(User buyer) {
        return cartRepository.findByBuyerId(buyer.getId()).stream()
                .map(this::mapToCartResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CartResponse addToCart(User buyer, CartRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Produk", request.getProductId()));

        if (product.getStock() < request.getQuantity()) {
            throw new BadRequestException("Stok tidak mencukupi");
        }

        Cart cart = cartRepository.findByBuyerIdAndProductId(buyer.getId(), product.getId())
                .orElse(Cart.builder()
                        .buyer(buyer)
                        .product(product)
                        .quantity(0)
                        .build());

        cart.setQuantity(cart.getQuantity() + request.getQuantity());
        cart = cartRepository.save(cart);

        return mapToCartResponse(cart);
    }

    @Transactional
    public void updateCartQuantity(Long cartId, User buyer, Integer quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", cartId));

        if (!cart.getBuyer().getId().equals(buyer.getId())) {
            throw new BadRequestException("Akses tidak sah");
        }

        if (quantity <= 0) {
            cartRepository.delete(cart);
        } else {
            if (cart.getProduct().getStock() < quantity) {
                throw new BadRequestException("Stok tidak mencukupi");
            }
            cart.setQuantity(quantity);
            cartRepository.save(cart);
        }
    }

    @Transactional
    public void removeFromCart(Long cartId, User buyer) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", cartId));

        if (!cart.getBuyer().getId().equals(buyer.getId())) {
            throw new BadRequestException("Akses tidak sah");
        }

        cartRepository.delete(cart);
    }

    @Transactional
    public void clearCart(User buyer) {
        cartRepository.deleteByBuyerId(buyer.getId());
    }

    private CartResponse mapToCartResponse(Cart cart) {
        Product product = cart.getProduct();
        
        // Eager load images
        BigDecimal discountedPrice = product.getPrice();
        String imageUrl = null;
        
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            imageUrl = product.getImages().stream()
                    .filter(img -> img.getIsPrimary() != null && img.getIsPrimary())
                    .findFirst()
                    .map(ProductImage::getImagePath)
                    .orElse(product.getImages().get(0).getImagePath());
        }

        BigDecimal subtotal = discountedPrice.multiply(BigDecimal.valueOf(cart.getQuantity()));

        return CartResponse.builder()
                .id(cart.getId())
                .productId(product.getId())
                .productName(product.getName())
                .price(product.getPrice())
                .discountedPrice(discountedPrice)
                .quantity(cart.getQuantity())
                .subtotal(subtotal)
                .imageUrl(imageUrl)
                .build();
    }
}