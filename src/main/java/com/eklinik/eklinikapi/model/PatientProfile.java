package com.eklinik.eklinikapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "patient_profiles")
@SQLDelete(sql = "UPDATE patient_profiles SET deleted = true WHERE user_id=?")
@Where(clause = "deleted=false")
public class PatientProfile {

    @Id
    @Column(name = "user_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private User user;

    private LocalDate dateOfBirth;
    private Double weight;
    private Double height;
    private Boolean hasChronicIllness;
    private Boolean isMedicationDependent;
    private String birthPlaceCity;
    private String birthPlaceDistrict;
    private String country;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "deleted", nullable = false, columnDefinition = "boolean default false")
    private boolean deleted = false;

}
