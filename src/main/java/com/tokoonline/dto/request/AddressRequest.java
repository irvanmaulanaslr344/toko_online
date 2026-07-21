package com.tokoonline.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressRequest {

    @NotBlank(message = "Label wajib diisi")
    private String label;

    @NotBlank(message = "Alamat lengkap wajib diisi")
    private String fullAddress;

    @NotBlank(message = "Kota wajib diisi")
    private String city;

    private String postalCode;

    @NotBlank(message = "Nomor telepon wajib diisi")
    private String phone;
}