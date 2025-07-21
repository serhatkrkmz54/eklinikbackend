package com.eklinik.eklinikapi.dto.response.doctor;

import com.eklinik.eklinikapi.dto.response.user.UserResponse;
import com.eklinik.eklinikapi.dto.response.clinics.ClinicResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoctorResponse {
    private Long doctorId;
    private String title;
    private UserResponse user;
    private ClinicResponse clinic;
}
