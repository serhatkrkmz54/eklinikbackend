package com.eklinik.eklinikapi.dto.response.appointment;

import com.eklinik.eklinikapi.dto.response.prescription.MedicalRecordForPatientResponse;
import com.eklinik.eklinikapi.enums.AppointmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AppointmentDetailForPatientResponse {
    private Long appointmentId;
    private LocalDateTime appointmentTime;
    private AppointmentStatus status;
    private String doctorFullName;
    private String clinicName;
    private MedicalRecordForPatientResponse medicalRecord;
}
