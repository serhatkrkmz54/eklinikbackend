package com.eklinik.eklinikapi.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "TC Kimlik Numarası boş olamaz.")
    @Size(min = 11, max = 11, message = "TC Kimlik Numarası 11 haneli olmalıdır.")
    private String nationalId;

    @NotBlank(message = "İsim boş olamaz.")
    @Size(min = 3, message = "İsim en az 3 karakter olmalıdır.")
    private String firstName;

    @NotBlank(message = "Soyisim boş olamaz.")
    @Size(min = 3, message = "Soyisim en az 3 karakter olmalıdır.")
    private String lastName;

    @NotBlank(message = "Şifre boş olamaz.")
    @Size(min = 6, message = "Şifre en az 6 karakter olmalıdır.")
    private String password;

    @NotBlank(message = "E-posta boş olamaz.")
    @Email(message = "Geçerli bir e-posta adresi giriniz.")
    private String email;

    @NotBlank(message = "Telefon numarası boş olamaz.")
    @Pattern(regexp = "^\\+90\\d{10}$", message = "Telefon numarası '+905xxxxxxxxx' formatında olmalıdır.")
    private String phoneNumber;


}
