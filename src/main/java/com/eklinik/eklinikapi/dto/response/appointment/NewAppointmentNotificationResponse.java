package com.eklinik.eklinikapi.dto.response.appointment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NewAppointmentNotificationResponse {
    private String patientFullName;
    private LocalDateTime appointmentTime;
    private String message;
}
