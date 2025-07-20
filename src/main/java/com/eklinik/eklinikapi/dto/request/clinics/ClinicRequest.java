package com.eklinik.eklinikapi.dto.request.clinics;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClinicRequest {
    @NotBlank(message = "Klinik adı boş olamaz.")
    private String name;
}
