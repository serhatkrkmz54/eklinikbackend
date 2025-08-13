package com.eklinik.eklinikapi.service;

import com.eklinik.eklinikapi.enums.AppointmentStatus;
import com.eklinik.eklinikapi.model.Appointment;
import com.eklinik.eklinikapi.repository.AppointmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentCleanUpService {

    private final AppointmentRepository appointmentRepository;

    @Scheduled(cron = "0 */2 * * * *")
    @Transactional
    public void updateMissedAppointments() {
        log.info("Running scheduled task to update missed appointments...");

        LocalDateTime threshold = LocalDateTime.now().minusHours(1);

        List<Appointment> appointmentsToUpdate = appointmentRepository
                .findByStatusAndAppointmentTimeBefore(AppointmentStatus.SCHEDULED, threshold);

        if (appointmentsToUpdate.isEmpty()) {
            log.info("No appointments to update. Task finished.");
            return;
        }

        log.info("Found {} appointments to mark as MISSED.", appointmentsToUpdate.size());

        for (Appointment appointment : appointmentsToUpdate) {
            appointment.setStatus(AppointmentStatus.MISSED);
        }

        appointmentRepository.saveAll(appointmentsToUpdate);
        log.info("Successfully updated {} appointments to MISSED status. Task finished.", appointmentsToUpdate.size());
    }
}
