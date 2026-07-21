package com.tokoonline.dto.request;

import com.tokoonline.enums.UserRole;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Email wajib diisi")
    @Email(message = "Format email tidak valid")
    private String email;

    @NotBlank(message = "Password wajib diisi")
    @Size(min = 6, message = "Password minimal 6 karakter")
    private String password;

    @NotBlank(message = "Nama wajib diisi")
    private String name;

    @NotNull(message = "Role wajib dipilih")
    private UserRole role;

    private String phone;
}