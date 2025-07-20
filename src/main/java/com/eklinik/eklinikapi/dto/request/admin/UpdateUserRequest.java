package com.eklinik.eklinikapi.dto.request.admin;

import com.eklinik.eklinikapi.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @Email(message = "Geçerli bir e-posta adresi giriniz.")
    private String email;

    private String firstName;

    private String lastName;

    @Pattern(regexp = "^\\+90\\d{10}$", message = "Telefon numarası '+905xxxxxxxxx' formatında olmalıdır.")
    private String phoneNumber;

    private UserRole role;
}
