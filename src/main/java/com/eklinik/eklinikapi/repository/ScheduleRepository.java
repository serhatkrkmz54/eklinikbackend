package com.eklinik.eklinikapi.repository;

import com.eklinik.eklinikapi.enums.ScheduleStatus;
import com.eklinik.eklinikapi.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
