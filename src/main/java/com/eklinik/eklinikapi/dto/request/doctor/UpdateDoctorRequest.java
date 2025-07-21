package com.eklinik.eklinikapi.dto.request.doctor;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateDoctorRequest {
    @Size(min = 2, message = "Unvan en az 2 karakter olmalıdır.")
    private String title;

    private Integer clinicId;
}
