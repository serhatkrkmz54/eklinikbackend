package com.eklinik.eklinikapi.service.impl;

import com.eklinik.eklinikapi.dto.request.schedule.ScheduleGenerationRequest;
import com.eklinik.eklinikapi.dto.response.schedule.ScheduleResponse;
import com.eklinik.eklinikapi.enums.ScheduleStatus;
import com.eklinik.eklinikapi.model.Doctor;
import com.eklinik.eklinikapi.model.Schedule;
import com.eklinik.eklinikapi.repository.DoctorRepository;
import com.eklinik.eklinikapi.repository.ScheduleRepository;
import com.eklinik.eklinikapi.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;

    @Override
    @Transactional
    public void generateSchedules(ScheduleGenerationRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı!"));

        List<Schedule> schedulesToCreate = new ArrayList<>();
        LocalDate currentDate = request.getStartDate();

        while (!currentDate.isAfter(request.getEndDate())) {
            if (request.getWorkDays().contains(currentDate.getDayOfWeek())) {

                LocalDateTime workdayStart = currentDate.atTime(request.getWorkStartTime());
                LocalDateTime workdayEnd = currentDate.atTime(request.getWorkEndTime());

                LocalDateTime lunchStart = (request.getLunchStartTime() != null) ? currentDate.atTime(request.getLunchStartTime()) : null;
                LocalDateTime lunchEnd = (request.getLunchEndTime() != null) ? currentDate.atTime(request.getLunchEndTime()) : null;

                LocalDateTime currentSlotStart = workdayStart;

                while (currentSlotStart.isBefore(workdayEnd)) {
                    LocalDateTime currentSlotEnd = currentSlotStart.plusMinutes(request.getSlotDurationInMinutes());

                    if (currentSlotEnd.isAfter(workdayEnd)) {
                        break;
                    }

                    boolean isDuringLunch = lunchStart != null && lunchEnd != null &&
                            currentSlotStart.isBefore(lunchEnd) &&
                            currentSlotEnd.isAfter(lunchStart);

                    if (!isDuringLunch) {
                        schedulesToCreate.add(Schedule.builder()
                                .doctor(doctor)
                                .startTime(currentSlotStart)
                                .endTime(currentSlotEnd)
                                .status(ScheduleStatus.AVAILABLE)
                                .build());
                    }
                    currentSlotStart = currentSlotEnd;
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        if (!schedulesToCreate.isEmpty()) {
            scheduleRepository.saveAll(schedulesToCreate);
        }
    }

    @Override
    public List<ScheduleResponse> getSchedulesForDoctorByDate(Long doctorId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<Schedule> schedules = scheduleRepository.findByDoctorIdAndStartTimeBetweenOrderByStartTimeAsc(
                doctorId, startOfDay, endOfDay);

        return schedules.stream()
                .map(this::mapToScheduleResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteScheduleSlot(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Randevu zaman dilimi bulunamadı!"));

        if (schedule.getStatus() == ScheduleStatus.BOOKED) {
            throw new RuntimeException("Dolu olan bir randevu silinemez!");
        }

        scheduleRepository.delete(schedule);
    }

    private ScheduleResponse mapToScheduleResponse(Schedule schedule) {
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .status(schedule.getStatus())
                .build();
    }
}
