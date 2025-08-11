package com.eklinik.eklinikapi.repository;

import com.eklinik.eklinikapi.model.EmergencyCall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmergencyCallRepository extends JpaRepository<EmergencyCall, Long> {
    List<EmergencyCall> findAllByOrderByCallTimeDesc();
}
