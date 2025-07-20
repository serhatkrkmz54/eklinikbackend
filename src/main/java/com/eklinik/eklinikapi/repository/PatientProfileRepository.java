package com.eklinik.eklinikapi.repository;

import com.eklinik.eklinikapi.model.PatientProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientProfileRepository extends JpaRepository<PatientProfile, Long> {
}
