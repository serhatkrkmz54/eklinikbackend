package com.eklinik.eklinikapi.dto.response.user;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PatientProfileResponse {
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
