package com.eklinik.eklinikapi.repository;

import com.eklinik.eklinikapi.model.PatientProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientProfileRepository extends JpaRepository<PatientProfile, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            value = "UPDATE patient_profiles SET deleted = false WHERE user_id = :id",
            nativeQuery = true
    )
    void reactivateProfileById(@Param("id") Long id);
}
