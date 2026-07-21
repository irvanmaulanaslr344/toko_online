package com.tokoonline.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequest {

    @NotBlank(message = "Nama produk wajib diisi")
    private String name;

    private String description;

    @NotNull(message = "Harga wajib diisi")
    @DecimalMin(value = "1.00", message = "Harga minimal Rp 1")
    private BigDecimal price;

    @NotNull(message = "Stok wajib diisi")
    @Min(value = 0, message = "Stok tidak boleh negatif")
    private Integer stock;

    private Long categoryId;
}