package com.tokoonline.dto.request;

import com.tokoonline.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {

    @NotNull(message = "Status wajib diisi")
    private OrderStatus status;
}