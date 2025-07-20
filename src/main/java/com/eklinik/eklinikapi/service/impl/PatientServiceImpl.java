package com.eklinik.eklinikapi.service.impl;

import com.eklinik.eklinikapi.dto.request.admin.UserResponse;
import com.eklinik.eklinikapi.dto.response.appointment.AppointmentDetailForPatientResponse;
import com.eklinik.eklinikapi.dto.response.appointment.AppointmentResponse;
import com.eklinik.eklinikapi.dto.response.clinics.ClinicResponse;
import com.eklinik.eklinikapi.dto.response.doctor.DoctorResponse;
import com.eklinik.eklinikapi.dto.response.prescription.MedicalRecordForPatientResponse;
import com.eklinik.eklinikapi.dto.response.prescription.PrescriptionForPatientResponse;
import com.eklinik.eklinikapi.dto.response.schedule.ScheduleResponse;
import com.eklinik.eklinikapi.enums.AppointmentStatus;
import com.eklinik.eklinikapi.enums.ScheduleStatus;
import com.eklinik.eklinikapi.model.*;
import com.eklinik.eklinikapi.repository.*;
import com.eklinik.eklinikapi.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<ClinicResponse> getAllClinics() {
        return clinicRepository.findAll().stream()
                .map(clinic -> ClinicResponse.builder().id(clinic.getId()).name(clinic.getName()).build())
                .collect(Collectors.toList());
    }

    @Override
    public List<DoctorResponse> getDoctorsByClinic(Integer clinicId) {
        // Bu metot DoctorServiceImpl'deki mapToDoctorResponse'un bir kopyasıdır.
        // Daha büyük projelerde bu tür map'leme işlemleri için merkezi bir Mapper sınıfı oluşturulur.
        return doctorRepository.findByClinicId(clinicId).stream()
                .map(this::mapToDoctorResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleResponse> getAvailableSlots(Long doctorId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        return scheduleRepository
                .findByDoctorIdAndStartTimeBetweenAndStatusOrderByStartTimeAsc(doctorId, startOfDay, endOfDay, ScheduleStatus.AVAILABLE)
                .stream()
                .map(schedule -> ScheduleResponse.builder()
                        .id(schedule.getId())
                        .startTime(schedule.getStartTime())
                        .endTime(schedule.getEndTime())
                        .status(schedule.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AppointmentResponse bookAppointment(UserDetails currentUser, Long scheduleId) {
        User patient = userRepository.findByNationalId(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        Schedule scheduleToBook = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Randevu zaman dilimi bulunamadı."));

        if (scheduleToBook.getStatus() == ScheduleStatus.BOOKED) {
            throw new RuntimeException("Bu zaman dilimi zaten dolu.");
        }

        // 1. Zaman diliminin durumunu GÜNCELLE
        scheduleToBook.setStatus(ScheduleStatus.BOOKED);
        scheduleRepository.save(scheduleToBook);

        // 2. Yeni bir Randevu (Appointment) OLUŞTUR
        Appointment newAppointment = Appointment.builder()
                .patient(patient)
                .doctor(scheduleToBook.getDoctor())
                .schedule(scheduleToBook)
                .appointmentTime(scheduleToBook.getStartTime())
                .status(AppointmentStatus.SCHEDULED)
                .build();

        Appointment savedAppointment = appointmentRepository.save(newAppointment);

        return mapToAppointmentResponse(savedAppointment);
    }

    @Override
    public List<AppointmentResponse> getMyHistory(UserDetails currentUser) {
        User patient = userRepository.findByNationalId(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        return appointmentRepository.findByPatientIdOrderByAppointmentTimeDesc(patient.getId())
                .stream()
                .map(this::mapToAppointmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelAppointment(UserDetails currentUser, Long appointmentId) {
        User patient = userRepository.findByNationalId(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        Appointment appointmentToCancel = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Randevu bulunamadı."));

        if (!appointmentToCancel.getPatient().getId().equals(patient.getId())) {
            throw new RuntimeException("Bu randevuyu iptal etme yetkiniz yok.");
        }

        // 1. Randevunun durumunu GÜNCELLE
        appointmentToCancel.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointmentToCancel);

        // 2. İlişkili zaman dilimini tekrar MÜSAİT yap
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
                .orElseThrow(() -> new RuntimeException("Randevu bulunamadı."));

        if (!appointment.getPatient().getId().equals(patient.getId())) {
            throw new RuntimeException("Bu randevu detayını görme yetkiniz yok.");
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
                .collect(Collectors.toList());

        return MedicalRecordForPatientResponse.builder()
                .diagnosis(record.getDiagnosis())
                .notes(record.getNotes())
                .prescriptions(prescriptions)
                .build();
    }
}
