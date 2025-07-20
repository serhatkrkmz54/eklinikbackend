package com.eklinik.eklinikapi.dto.request.admin;

import com.eklinik.eklinikapi.enums.UserRole;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank(message = "TC Kimlik Numarası boş olamaz.")
    @Size(min = 11, max = 11, message = "TC Kimlik Numarası 11 haneli olmalıdır.")
    private String nationalId;

    @NotBlank(message = "E-posta boş olamaz.")
    @Email(message = "Geçerli bir e-posta adresi giriniz.")
    private String email;

    @NotBlank(message = "Şifre boş olamaz.")
    @Size(min = 6, message = "Şifre en az 6 karakter olmalıdır.")
    private String password;

    @NotBlank(message = "İsim boş olamaz.")
    private String firstName;

    @NotBlank(message = "Soyisim boş olamaz.")
    private String lastName;

    @NotBlank(message = "Telefon numarası boş olamaz.")
    @Pattern(regexp = "^\\+90\\d{10}$", message = "Telefon numarası '+905xxxxxxxxx' formatında olmalıdır.")
    private String phoneNumber;

    @NotNull(message = "Kullanıcı rolü belirtilmelidir.")
    private UserRole role;
}
