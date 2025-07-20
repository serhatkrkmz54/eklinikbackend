package com.eklinik.eklinikapi.repository;

import com.eklinik.eklinikapi.model.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicRepository extends JpaRepository<Clinic,Integer> {

}
