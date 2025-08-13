package com.eklinik.eklinikapi.dto.response.doctor;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoctorPatientStatsResponse {
    private long totalUniquePatientCount;
    private long newPatientsThisMonth;
}
