package com.eklinik.eklinikapi.dto.response.user;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EmergencyCallResponse {
    private Long callId;
    private String patientFullName;
    private String address;
    private double latitude;
    private double longitude;
    private int accuracy;
    private LocalDateTime callTime;
}
