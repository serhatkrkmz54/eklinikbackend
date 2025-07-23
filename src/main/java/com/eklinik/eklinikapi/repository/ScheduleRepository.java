package com.eklinik.eklinikapi.repository;

import com.eklinik.eklinikapi.enums.ScheduleStatus;
import com.eklinik.eklinikapi.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByDoctorIdAndStartTimeBetweenOrderByStartTimeAsc(
            Long doctorId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );

    List<Schedule> findByDoctorIdAndStartTimeBetweenAndStatusOrderByStartTimeAsc(
            Long doctorId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay,
            ScheduleStatus status
    );

    @Query("SELECT s FROM Schedule s " +
            "LEFT JOIN FETCH s.doctor d " +
            "LEFT JOIN FETCH d.user " +
            "LEFT JOIN FETCH d.clinic " +
            "LEFT JOIN FETCH s.appointment a " +
            "WHERE s.doctor.id = :doctorId AND s.startTime BETWEEN :startTime AND :endTime " +
            "ORDER BY s.startTime ASC")
    List<Schedule> findSchedulesWithDetailsByDoctorAndDateRange(
            @Param("doctorId") Long doctorId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
