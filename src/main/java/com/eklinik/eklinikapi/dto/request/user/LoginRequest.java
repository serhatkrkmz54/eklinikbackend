package com.eklinik.eklinikapi.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "T.C. Kimlik Numarası boş olamaz.")
    @Size(min = 11, max = 11, message = "T.C. Kimlik Numarası 11 haneli olmalıdır.")
    private String nationalId;

    @NotBlank(message = "Şifre alanı boş olamaz.")
    private String password;
}
