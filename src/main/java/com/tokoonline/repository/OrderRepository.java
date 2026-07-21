package com.tokoonline.repository;

import com.tokoonline.entity.Order;
import com.tokoonline.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Order oleh buyer
    Page<Order> findByBuyerIdOrderByCreatedAtDesc(Long buyerId, Pageable pageable);

    // Order yang mengandung produk seller tertentu
    @Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.product.seller.id = :sellerId " +
           "ORDER BY o.createdAt DESC")
    Page<Order> findOrdersBySellerId(@Param("sellerId") Long sellerId, Pageable pageable);

    // Hitung order per seller
    @Query("SELECT COUNT(DISTINCT o) FROM Order o JOIN o.items i WHERE i.product.seller.id = :sellerId")
    Long countBySellerId(@Param("sellerId") Long sellerId);

    // Total pendapatan seller
    @Query("SELECT COALESCE(SUM(i.priceAtOrder * i.quantity - i.discountApplied), 0) " +
           "FROM OrderItem i WHERE i.product.seller.id = :sellerId AND i.order.status = 'DELIVERED'")
    BigDecimal sumRevenueBySellerId(@Param("sellerId") Long sellerId);
}