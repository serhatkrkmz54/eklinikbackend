package com.eklinik.eklinikapi.dto.response.doctor;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class PatientDetailForDoctorResponse {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private Double weight;
    private Double height;
    private Boolean hasChronicIllness;
    private Boolean isMedicationDependent;
    private String address;
    private List<PatientHistoryItemResponse> history;

}
