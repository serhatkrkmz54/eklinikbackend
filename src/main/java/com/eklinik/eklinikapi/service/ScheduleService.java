package com.eklinik.eklinikapi.service;

import com.eklinik.eklinikapi.dto.request.schedule.ScheduleGenerationRequest;
import com.eklinik.eklinikapi.dto.response.schedule.ScheduleResponse;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    void generateSchedules(ScheduleGenerationRequest request);
    List<ScheduleResponse> getSchedulesForDoctorByDate(Long doctorId, LocalDate date);
    void deleteScheduleSlot(Long scheduleId);
}
