package com.eklinik.eklinikapi.dto.response.appointment;

import com.eklinik.eklinikapi.enums.AppointmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AppointmentResponse {
    private Long appointmentId;
    private LocalDateTime appointmentTime;
    private AppointmentStatus status;
    private String doctorFullName;
    private String clinicName;
}
