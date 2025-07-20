package com.eklinik.eklinikapi.dto.request.user;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientProfileRequest {
    private LocalDate dateOfBirth;
    private Double weight;
    private Double height;
    private Boolean hasChronicIllness;
    private Boolean isMedicationDependent;
    private String birthPlaceCity;
    private String birthPlaceDistrict;
    private String address;
    private String country;
}
