package com.eklinik.eklinikapi.dto.response.doctor;

import com.eklinik.eklinikapi.dto.request.admin.UserResponse;
import com.eklinik.eklinikapi.enums.AppointmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AppointmentForDoctorResponse {
    private Long appointmentId;
    private LocalDateTime appointmentTime;
    private AppointmentStatus status;
    private UserResponse patientInfo;
}
