package com.eklinik.eklinikapi.repository;

import com.eklinik.eklinikapi.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PresciptionRepository extends JpaRepository<Prescription, Long> {

}
