package com.eklinik.eklinikapi.service;

import com.eklinik.eklinikapi.dto.request.user.PatientProfileRequest;
import com.eklinik.eklinikapi.dto.response.user.PatientProfileResponse;
import org.springframework.security.core.userdetails.UserDetails;

public interface PatientProfileService {
    PatientProfileResponse getPatientProfile(UserDetails userDetails);
    PatientProfileResponse updatePatientProfile(UserDetails userDetails, PatientProfileRequest request);

}
