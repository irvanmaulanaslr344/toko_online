package com.tokoonline.repository;

import com.tokoonline.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    Optional<Discount> findByProductId(Long productId);

    @Query("SELECT d FROM Discount d WHERE d.product.id = :productId AND d.isActive = true " +
           "AND d.startDate <= CURRENT_TIMESTAMP AND d.endDate >= CURRENT_TIMESTAMP")
    Optional<Discount> findActiveDiscountByProductId(@Param("productId") Long productId);
}