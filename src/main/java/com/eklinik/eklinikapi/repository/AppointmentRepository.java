package com.eklinik.eklinikapi.repository;

import com.eklinik.eklinikapi.dto.response.clinics.ClinicAppointmentCountDataResponse;
import com.eklinik.eklinikapi.enums.AppointmentStatus;
import com.eklinik.eklinikapi.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientIdOrderByAppointmentTimeDesc(Long patientId);
    List<Appointment> findByDoctorIdAndAppointmentTimeBetweenOrderByAppointmentTimeAsc(
            Long doctorId, LocalDateTime start, LocalDateTime end);
    List<Appointment> findByPatientIdAndDoctorIdAndStatusOrderByAppointmentTimeDesc(
            Long patientId, Long doctorId, AppointmentStatus status);

    boolean existsByPatientIdAndDoctorIdAndStatusNotAndAppointmentTimeAfter(
            Long patientId,
            Long doctorId,
            AppointmentStatus status,
            LocalDateTime appointmentTime
    );

    Optional<Appointment> findByScheduleId(Long scheduleId);
    long countByStatusInAndAppointmentTimeBetween(List<AppointmentStatus> statuses, LocalDateTime start, LocalDateTime end);

    @Query("SELECT new com.eklinik.eklinikapi.dto.response.clinics.ClinicAppointmentCountDataResponse(d.clinic.name, COUNT(a)) " +
            "FROM Appointment a JOIN a.doctor d " +
            "WHERE a.appointmentTime >= :startOfMonth AND a.appointmentTime < :endOfMonth " +
            "AND a.status IN ('SCHEDULED', 'COMPLETED') " +
            "GROUP BY d.clinic.name " +
            "ORDER BY COUNT(a) DESC")
    List<ClinicAppointmentCountDataResponse> findAppointmentCountsByClinicForMonth(
            @Param("startOfMonth") LocalDateTime startOfMonth,
            @Param("endOfMonth") LocalDateTime endOfMonth
    );

    List<Appointment> findTop5ByStatusAndAppointmentTimeAfterOrderByAppointmentTimeAsc(
            AppointmentStatus status,
            LocalDateTime time
    );

    @Query("SELECT DISTINCT CAST(a.appointmentTime AS LocalDate) FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentTime >= :startOfMonth AND a.appointmentTime < :endOfMonth")
    List<LocalDate> findDistinctAppointmentDatesByDoctorForMonth(
            @Param("doctorId") Long doctorId,
            @Param("startOfMonth") LocalDateTime startOfMonth,
            @Param("endOfMonth") LocalDateTime endOfMonth
    );

    List<Appointment> findTop5ByDoctorIdAndStatusAndAppointmentTimeAfterOrderByAppointmentTimeAsc(
            Long doctorId,
            AppointmentStatus status,
            LocalDateTime time
    );

}
