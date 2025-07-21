package com.eklinik.eklinikapi.repository;

import com.eklinik.eklinikapi.model.Doctor;
import com.eklinik.eklinikapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor,Long> {
    List<Doctor> findByClinicId(Integer clinicId);
    Optional<Doctor> findByUser(User user);
    boolean existsByUserId(Long userId);
}
