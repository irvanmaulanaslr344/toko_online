package com.tokoonline.service;

import com.tokoonline.dto.request.CheckoutRequest;
import com.tokoonline.dto.response.OrderDetailResponse;
import com.tokoonline.dto.response.OrderItemResponse;
import com.tokoonline.dto.response.OrderResponse;
import com.tokoonline.dto.response.AddressResponse;
import com.tokoonline.entity.*;
import com.tokoonline.enums.OrderStatus;
import com.tokoonline.exception.BadRequestException;
import com.tokoonline.exception.ResourceNotFoundException;
import com.tokoonline.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final ReviewRepository reviewRepository;
    private final ProductService productService;

    @Transactional
    public Order createOrder(User buyer, CheckoutRequest request) {
        List<Cart> cartItems = cartRepository.findByBuyerId(buyer.getId());

        if (cartItems.isEmpty()) {
            throw new BadRequestException("Keranjang belanja kosong");
        }

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Alamat", request.getAddressId()));

        if (!address.getUser().getId().equals(buyer.getId())) {
            throw new BadRequestException("Alamat tidak valid");
        }

        Order order = Order.builder()
                .buyer(buyer)
                .address(address)
                .status(OrderStatus.PENDING)
                .items(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Cart cartItem : cartItems) {
            Product product = cartItem.getProduct();

            if (product.getStock() < cartItem.getQuantity()) {
                throw new BadRequestException(
                        "Stok '" + product.getName() + "' tidak mencukupi");
            }

            BigDecimal discountedPrice = productService.calculateDiscountedPrice(
                    product.getPrice(), null);
            BigDecimal discountApplied = product.getPrice().subtract(discountedPrice);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .priceAtOrder(product.getPrice())
                    .discountApplied(discountApplied.multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                    .build();

            order.getItems().add(orderItem);

            BigDecimal itemTotal = discountedPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(totalAmount);
        order = orderRepository.save(order);

        cartRepository.deleteByBuyerId(buyer.getId());

        return order;
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getBuyerOrders(User buyer, Pageable pageable) {
        return orderRepository.findByBuyerIdOrderByCreatedAtDesc(buyer.getId(), pageable)
                .map(this::mapToOrderResponse);
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderDetail(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        if (!order.getBuyer().getId().equals(user.getId())
                && !isSellerOfOrder(order, user)) {
            throw new BadRequestException("Akses tidak sah");
        }

        return mapToOrderDetailResponse(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getSellerOrders(User seller, Pageable pageable) {
        return orderRepository.findOrdersBySellerId(seller.getId(), pageable)
                .map(this::mapToOrderResponse);
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, User seller, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        if (!isSellerOfOrder(order, seller)) {
            throw new BadRequestException("Anda tidak memiliki akses ke pesanan ini");
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    private boolean isSellerOfOrder(Order order, User seller) {
        return order.getItems().stream()
                .anyMatch(item -> item.getProduct().getSeller().getId().equals(seller.getId()));
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .totalItems(order.getItems().size())
                .createdAt(order.getCreatedAt())
                .build();
    }

    private OrderDetailResponse mapToOrderDetailResponse(Order order) {
        AddressResponse addressResponse = null;
        if (order.getAddress() != null) {
            addressResponse = AddressResponse.builder()
                    .id(order.getAddress().getId())
                    .label(order.getAddress().getLabel())
                    .fullAddress(order.getAddress().getFullAddress())
                    .city(order.getAddress().getCity())
                    .postalCode(order.getAddress().getPostalCode())
                    .phone(order.getAddress().getPhone())
                    .build();
        }

        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> {
                    String imageUrl = null;
                    if (item.getProduct().getImages() != null && !item.getProduct().getImages().isEmpty()) {
                        imageUrl = item.getProduct().getImages().stream()
                                .filter(img -> img.getIsPrimary() != null && img.getIsPrimary())
                                .findFirst()
                                .map(ProductImage::getImagePath)
                                .orElse(null);
                    }

                    boolean reviewed = reviewRepository.existsByOrderItemId(item.getId());

                    BigDecimal subtotal = item.getPriceAtOrder()
                            .multiply(BigDecimal.valueOf(item.getQuantity()))
                            .subtract(item.getDiscountApplied());

                    return OrderItemResponse.builder()
                            .id(item.getId())
                            .productId(item.getProduct().getId())
                            .productName(item.getProduct().getName())
                            .quantity(item.getQuantity())
                            .priceAtOrder(item.getPriceAtOrder())
                            .discountApplied(item.getDiscountApplied())
                            .subtotal(subtotal)
                            .imageUrl(imageUrl)
                            .reviewed(reviewed)
                            .build();
                })
                .collect(Collectors.toList());

        return OrderDetailResponse.builder()
                .id(order.getId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .address(addressResponse)
                .items(itemResponses)
                .build();
    }
}