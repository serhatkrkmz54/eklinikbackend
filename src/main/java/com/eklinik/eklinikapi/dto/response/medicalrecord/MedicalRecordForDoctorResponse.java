package com.eklinik.eklinikapi.dto.response.medicalrecord;

import com.eklinik.eklinikapi.dto.response.prescription.PresciptionForDoctorResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MedicalRecordForDoctorResponse {
    private String diagnosis;
    private String notes;
    private List<PresciptionForDoctorResponse> prescriptions;

}
