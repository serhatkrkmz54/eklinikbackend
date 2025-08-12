package com.eklinik.eklinikapi.dto.response.appointment;

import com.eklinik.eklinikapi.enums.AppointmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UpcomingAppointmentForDoctorResponse {
    private Long appointmentId;
    private String patientFullName;
    private LocalDateTime appointmentTime;
    private AppointmentStatus status;
}
