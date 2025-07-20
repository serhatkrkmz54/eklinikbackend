package com.eklinik.eklinikapi.dto.response.prescription;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MedicalRecordForPatientResponse {
    private String diagnosis;
    private String notes;
    private List<PrescriptionForPatientResponse> prescriptions;
}
