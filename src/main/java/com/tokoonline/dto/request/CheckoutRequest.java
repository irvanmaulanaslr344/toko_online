package com.tokoonline.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckoutRequest {

    @NotNull(message = "ID alamat wajib diisi")
    private Long addressId;
}