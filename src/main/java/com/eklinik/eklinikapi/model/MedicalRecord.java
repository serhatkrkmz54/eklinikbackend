package com.eklinik.eklinikapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "medical_records")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @OneToMany(mappedBy = "medicalRecord",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Prescription> prescriptions;

}
