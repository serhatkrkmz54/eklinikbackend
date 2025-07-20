package com.eklinik.eklinikapi.dto.response.doctor;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PatientHistoryItemResponse {
    private Long appointmentId;
    private LocalDateTime appointmentTime;
    private String diagnosis;
}
