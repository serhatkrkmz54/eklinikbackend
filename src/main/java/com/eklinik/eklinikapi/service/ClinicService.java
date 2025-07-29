package com.eklinik.eklinikapi.service;

import com.eklinik.eklinikapi.dto.request.clinics.ClinicRequest;
import com.eklinik.eklinikapi.dto.response.clinics.ClinicResponse;

import java.util.List;

public interface ClinicService {
    ClinicResponse createClinic(ClinicRequest request);
    ClinicResponse updateClinic(Integer id, ClinicRequest request);
    void deleteClinic(Integer id);
    ClinicResponse getClinicById(Integer id);
    List<ClinicResponse> getAllClinics();
    long getTotalClinicCount();
}
