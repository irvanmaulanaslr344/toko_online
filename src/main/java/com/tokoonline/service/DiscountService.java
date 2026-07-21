package com.tokoonline.service;

import com.tokoonline.dto.request.DiscountRequest;
import com.tokoonline.entity.Discount;
import com.tokoonline.entity.Product;
import com.tokoonline.entity.User;
import com.tokoonline.exception.BadRequestException;
import com.tokoonline.exception.ResourceNotFoundException;
import com.tokoonline.repository.DiscountRepository;
import com.tokoonline.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountService {

    private final DiscountRepository discountRepository;
    private final ProductRepository productRepository;

    public Discount createDiscount(Long productId, User seller, DiscountRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produk", productId));

        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new BadRequestException("Anda tidak memiliki akses ke produk ini");
        }

        if (discountRepository.findByProductId(productId).isPresent()) {
            throw new BadRequestException("Produk ini sudah memiliki diskon aktif");
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("Tanggal berakhir harus setelah tanggal mulai");
        }

        Discount discount = Discount.builder()
                .product(product)
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isActive(true)
                .build();

        return discountRepository.save(discount);
    }

    public Discount updateDiscount(Long discountId, User seller, DiscountRequest request) {
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new ResourceNotFoundException("Diskon", discountId));

        if (!discount.getProduct().getSeller().getId().equals(seller.getId())) {
            throw new BadRequestException("Anda tidak memiliki akses ke diskon ini");
        }

        discount.setDiscountType(request.getDiscountType());
        discount.setDiscountValue(request.getDiscountValue());
        discount.setStartDate(request.getStartDate());
        discount.setEndDate(request.getEndDate());

        return discountRepository.save(discount);
    }

    public void deleteDiscount(Long discountId, User seller) {
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new ResourceNotFoundException("Diskon", discountId));

        if (!discount.getProduct().getSeller().getId().equals(seller.getId())) {
            throw new BadRequestException("Anda tidak memiliki akses ke diskon ini");
        }

        discountRepository.delete(discount);
    }

    public List<Discount> getSellerDiscounts(User seller) {
        return discountRepository.findAll().stream()
                .filter(d -> d.getProduct().getSeller().getId().equals(seller.getId()))
                .toList();
    }
}