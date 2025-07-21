package com.eklinik.eklinikapi.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateMyUserRequest {
    @Size(min = 3, message = "İsim en az 3 karakter olmalıdır.")
    private String firstName;

    @Size(min = 3, message = "Soyisim en az 3 karakter olmalıdır.")
    private String lastName;

    @Email(message = "Geçerli bir e-posta adresi giriniz.")
    private String email;

    @Pattern(regexp = "^\\+90\\d{10}$", message = "Telefon numarası '+905xxxxxxxxx' formatında olmalıdır.")
    private String phoneNumber;

    private LocalDate dateOfBirth;
    private Double weight;
    private Double height;
    private Boolean hasChronicIllness;
    private Boolean isMedicationDependent;
    private String birthPlaceCity;
    private String birthPlaceDistrict;
    private String address;
    private String country;
}
