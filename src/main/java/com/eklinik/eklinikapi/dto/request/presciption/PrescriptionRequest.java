package com.eklinik.eklinikapi.dto.request.presciption;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PrescriptionRequest {
    @NotBlank(message = "İlaç adı boş olamaz.")
    private String medicationName;

    @NotBlank(message = "Dozaj bilgisi boş olamaz.")
    private String dosage;

    @NotBlank(message = "Kullanım süresi boş olamaz.")
    private String duration;
}
