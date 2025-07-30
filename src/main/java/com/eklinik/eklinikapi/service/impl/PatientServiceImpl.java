package com.eklinik.eklinikapi.service.impl;

import com.eklinik.eklinikapi.dto.response.user.UserResponse;
import com.eklinik.eklinikapi.dto.response.appointment.AppointmentDetailForPatientResponse;
import com.eklinik.eklinikapi.dto.response.appointment.AppointmentResponse;
import com.eklinik.eklinikapi.dto.response.clinics.ClinicResponse;
import com.eklinik.eklinikapi.dto.response.doctor.DoctorResponse;
import com.eklinik.eklinikapi.dto.response.prescription.MedicalRecordForPatientResponse;
import com.eklinik.eklinikapi.dto.response.prescription.PrescriptionForPatientResponse;
import com.eklinik.eklinikapi.dto.response.schedule.ScheduleResponse;
import com.eklinik.eklinikapi.enums.AppointmentStatus;
import com.eklinik.eklinikapi.enums.ScheduleStatus;
import com.eklinik.eklinikapi.enums.UserRole;
import com.eklinik.eklinikapi.exception.AppointmentRuleException;
import com.eklinik.eklinikapi.model.*;
import com.eklinik.eklinikapi.repository.*;
import com.eklinik.eklinikapi.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final ClinicRepository clinicRepository;
    private final DoctorRepository doctorRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public List<ClinicResponse> getAllClinics() {
        return clinicRepository.findAll().stream()
                .map(clinic -> ClinicResponse.builder().id(clinic.getId()).name(clinic.getName()).build())
                .collect(toList());
    }

    @Override
    public List<DoctorResponse> getDoctorsByClinic(Integer clinicId) {
        return doctorRepository.findByClinicId(clinicId).stream()
                .map(this::mapToDoctorResponse)
                .collect(toList());
    }

//    @Override
//    public List<ScheduleResponse> getSlotsByDoctorAndDate(Long doctorId, LocalDate date) {
//        LocalDateTime startOfDay = date.atStartOfDay();
//        LocalDateTime endOfDay = date.atTime(23, 59, 59);
//
//        return scheduleRepository
//                .findByDoctorIdAndStartTimeBetweenOrderByStartTimeAsc(doctorId, startOfDay, endOfDay)
//                .stream()
//                .map(schedule -> ScheduleResponse.builder()
//                        .id(schedule.getId())
//                        .startTime(schedule.getStartTime())
//                        .endTime(schedule.getEndTime())
//                        .status(schedule.getStatus())
//                        .build())
//                .collect(toList());
//    }

    @Override
    @Transactional(readOnly = true)
    public Map<LocalDate, List<ScheduleResponse>> getSlotsForDateRange(Long doctorId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<Schedule> schedules = scheduleRepository
                .findSchedulesWithDetailsByDoctorAndDateRange(
                        doctorId,
                        startDateTime,
                        endDateTime
                );

        return schedules.stream()
                .map(this::mapToScheduleResponse)
                .collect(groupingBy(
                        schedule -> schedule.getStartTime().toLocalDate(),
                        toList()
                ));
    }

    @Override
    @Transactional
    public AppointmentResponse bookAppointment(UserDetails currentUser, Long scheduleId) {
        User patient = userRepository.findByNationalId(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        Schedule scheduleToBook = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new AppointmentRuleException("Randevu zaman dilimi bulunamadı."));

        if (scheduleToBook.getStartTime().isBefore(LocalDateTime.now())) {
            throw new AppointmentRuleException("Geçmiş tarihli bir saate randevu alamazsınız.");
        }

        if (scheduleToBook.getStatus() == ScheduleStatus.BOOKED) {
            throw new AppointmentRuleException("Bu zaman dilimi zaten dolu.");
        }

        Doctor doctor = scheduleToBook.getDoctor();
        LocalDateTime fifteenDaysAgo = LocalDateTime.now().minusDays(15);

        boolean hasRecentAppointment = appointmentRepository
                .existsByPatientIdAndDoctorIdAndStatusNotAndAppointmentTimeAfter(
                        patient.getId(),
                        doctor.getId(),
                        AppointmentStatus.CANCELLED,
                        fifteenDaysAgo
                );

        if (hasRecentAppointment) {
            throw new AppointmentRuleException("Aynı doktordan 15 gün içinde sadece bir randevu alabilirsiniz.");
        }

        scheduleToBook.setStatus(ScheduleStatus.BOOKED);
        Schedule updatedSchedule = scheduleRepository.save(scheduleToBook);

        Appointment newAppointment = Appointment.builder()
                .patient(patient)
                .doctor(scheduleToBook.getDoctor())
                .schedule(scheduleToBook)
                .appointmentTime(scheduleToBook.getStartTime())
                .status(AppointmentStatus.SCHEDULED)
                .build();

        Appointment savedAppointment = appointmentRepository.save(newAppointment);
        String topic = String.format("/topic/slots/%d/%s",
                doctor.getId(),
                updatedSchedule.getStartTime().toLocalDate().toString()
        );
        messagingTemplate.convertAndSend(topic, new ScheduleResponse(updatedSchedule));


        return mapToAppointmentResponse(savedAppointment);
    }

    @Override
    public List<AppointmentResponse> getMyHistory(UserDetails currentUser) {
        User patient = userRepository.findByNationalId(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        return appointmentRepository.findByPatientIdOrderByAppointmentTimeDesc(patient.getId())
                .stream()
                .map(this::mapToAppointmentResponse)
                .collect(toList());
    }

    @Override
    @Transactional
    public void cancelAppointment(UserDetails currentUser, Long appointmentId) {
        User patient = userRepository.findByNationalId(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        Appointment appointmentToCancel = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentRuleException("Randevu bulunamadı."));

        if (!appointmentToCancel.getPatient().getId().equals(patient.getId())) {
            throw new AppointmentRuleException("Bu randevuyu iptal etme yetkiniz yok.");
        }

        appointmentToCancel.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointmentToCancel);

        Schedule scheduleToFree = appointmentToCancel.getSchedule();
        scheduleToFree.setStatus(ScheduleStatus.AVAILABLE);
        scheduleRepository.save(scheduleToFree);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentDetailForPatientResponse getMyAppointmentDetails(UserDetails currentUser, Long appointmentId) {
        User patient = userRepository.findByNationalId(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentRuleException("Randevu bulunamadı."));

        if (!appointment.getPatient().getId().equals(patient.getId())) {
            throw new AppointmentRuleException("Bu randevu detayını görme yetkiniz yok.");
        }

        Doctor doctor = appointment.getDoctor();
        String doctorFullName = doctor.getTitle() + " " + doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName();

        MedicalRecordForPatientResponse medicalRecordResponse = medicalRecordRepository
                .findByAppointmentId(appointmentId)
                .map(this::mapToMedicalRecordForPatientResponse)
                .orElse(null);

        return AppointmentDetailForPatientResponse.builder()
                .appointmentId(appointment.getId())
                .appointmentTime(appointment.getAppointmentTime())
                .status(appointment.getStatus())
                .doctorFullName(doctorFullName)
                .clinicName(doctor.getClinic().getName())
                .medicalRecord(medicalRecordResponse)
                .build();
    }

    @Override
    public long getTotalPatientCount() {
        return userRepository.countByRole(UserRole.ROLE_PATIENT);
    }

    private AppointmentResponse mapToAppointmentResponse(Appointment appointment) {
        Doctor doctor = appointment.getDoctor();
        String fullName = doctor.getTitle() + " " + doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName();
        return AppointmentResponse.builder()
                .appointmentId(appointment.getId())
                .appointmentTime(appointment.getAppointmentTime())
                .status(appointment.getStatus())
                .doctorFullName(fullName)
                .clinicName(doctor.getClinic().getName())
                .build();
    }

    private DoctorResponse mapToDoctorResponse(Doctor doctor) {
        UserResponse userResponse = UserResponse.builder()
                .id(doctor.getUser().getId())
                .nationalId(doctor.getUser().getNationalId())
                .email(doctor.getUser().getEmail())
                .firstName(doctor.getUser().getFirstName())
                .lastName(doctor.getUser().getLastName())
                .phoneNumber(doctor.getUser().getPhoneNumber())
                .role(doctor.getUser().getRole())
                .build();

        ClinicResponse clinicResponse = ClinicResponse.builder()
                .id(doctor.getClinic().getId())
                .name(doctor.getClinic().getName())
                .build();

        return DoctorResponse.builder()
                .doctorId(doctor.getId())
                .title(doctor.getTitle())
                .user(userResponse)
                .clinic(clinicResponse)
                .build();
    }

    private MedicalRecordForPatientResponse mapToMedicalRecordForPatientResponse(MedicalRecord record) {
        List<PrescriptionForPatientResponse> prescriptions = record.getPrescriptions().stream()
                .map(p -> PrescriptionForPatientResponse.builder()
                        .medicationName(p.getMedicationName())
                        .dosage(p.getDosage())
                        .duration(p.getDuration())
                        .build())
                .collect(toList());

        return MedicalRecordForPatientResponse.builder()
                .diagnosis(record.getDiagnosis())
                .notes(record.getNotes())
                .prescriptions(prescriptions)
                .build();
    }

    private ScheduleResponse mapToScheduleResponse(Schedule schedule) {
        return new ScheduleResponse(
                schedule.getId(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getStatus()
        );
    }
}
