package com.eklinik.eklinikapi.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateMyUserRequest {
    @Email(message = "Geçerli bir e-posta adresi giriniz.")
    private String email;

    private String firstName;

    private String lastName;

    @Pattern(regexp = "^\\+90\\d{10}$", message = "Telefon numarası '+905xxxxxxxxx' formatında olmalıdır.")
    private String phoneNumber;
}
