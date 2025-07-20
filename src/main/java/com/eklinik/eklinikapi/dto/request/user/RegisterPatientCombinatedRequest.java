package com.eklinik.eklinikapi.dto.request.user;

import lombok.Data;

@Data
public class RegisterPatientCombinatedRequest {
    private RegisterRequest userRequest;

    private PatientProfileRequest profileRequest;

}
