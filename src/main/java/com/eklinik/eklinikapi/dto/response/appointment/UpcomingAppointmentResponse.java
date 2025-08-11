package com.eklinik.eklinikapi.dto.response.appointment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UpcomingAppointmentResponse {
    private Long appointmentId;
    private String patientFullName;
    private String doctorFullName;
    private String clinicName;
    private LocalDateTime appointmentTime;
}
