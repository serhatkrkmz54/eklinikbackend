package com.eklinik.eklinikapi.dto.request.doctor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DoctorRequest {
    @NotNull(message = "Doktor yapılacak kullanıcı ID'si boş olamaz.")
    private Long userId;

    @NotNull(message = "Klinik ID'si boş olamaz.")
    private Integer clinicId;

    @NotBlank(message = "Doktor unvanı boş olamaz.")
    private String title;
}
