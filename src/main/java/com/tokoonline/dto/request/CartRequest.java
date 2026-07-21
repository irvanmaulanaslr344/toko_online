package com.tokoonline.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CartRequest {

    @NotNull(message = "ID produk wajib diisi")
    private Long productId;

    @NotNull(message = "Jumlah wajib diisi")
    @Min(value = 1, message = "Jumlah minimal 1")
    private Integer quantity;
}