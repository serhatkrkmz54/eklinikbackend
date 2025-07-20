package com.eklinik.eklinikapi.repository;

import com.eklinik.eklinikapi.enums.AppointmentStatus;
import com.eklinik.eklinikapi.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientIdOrderByAppointmentTimeDesc(Long patientId);
    List<Appointment> findByDoctorIdAndAppointmentTimeBetweenOrderByAppointmentTimeAsc(
            Long doctorId, LocalDateTime start, LocalDateTime end);
    List<Appointment> findByPatientIdAndDoctorIdAndStatusOrderByAppointmentTimeDesc(
            Long patientId, Long doctorId, AppointmentStatus status);
}
