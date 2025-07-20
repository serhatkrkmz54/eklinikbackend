package com.eklinik.eklinikapi.dto.response.medicalrecord;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MedicalRecordResponse {
    private Long recordId;
    private Long appointmentId;
    private String diagnosis;
    private String notes;
    private List<String> prescriptions;
}
