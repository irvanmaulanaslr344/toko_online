package com.tokoonline.dto.request;

import com.tokoonline.enums.DiscountType;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DiscountRequest {

    @NotNull(message = "Tipe diskon wajib dipilih")
    private DiscountType discountType;

    @NotNull(message = "Nilai diskon wajib diisi")
    @DecimalMin(value = "1.00", message = "Nilai diskon minimal Rp 1")
    private BigDecimal discountValue;

    @NotNull(message = "Tanggal mulai wajib diisi")
    private LocalDateTime startDate;

    @NotNull(message = "Tanggal berakhir wajib diisi")
    private LocalDateTime endDate;
}