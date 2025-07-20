package com.eklinik.eklinikapi.dto.response.prescription;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrescriptionForPatientResponse {
    private String medicationName;
    private String dosage;
    private String duration;
}
