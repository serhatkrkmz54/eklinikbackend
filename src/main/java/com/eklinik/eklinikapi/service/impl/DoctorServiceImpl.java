package com.eklinik.eklinikapi.service.impl;

import com.eklinik.eklinikapi.dto.request.admin.UserResponse;
import com.eklinik.eklinikapi.dto.request.appointment.CompleteAppointmentRequest;
import com.eklinik.eklinikapi.dto.request.doctor.DoctorRequest;
import com.eklinik.eklinikapi.dto.response.clinics.ClinicResponse;
import com.eklinik.eklinikapi.dto.response.doctor.*;
import com.eklinik.eklinikapi.dto.response.medicalrecord.MedicalRecordResponse;
import com.eklinik.eklinikapi.enums.AppointmentStatus;
import com.eklinik.eklinikapi.enums.UserRole;
import com.eklinik.eklinikapi.model.*;
import com.eklinik.eklinikapi.repository.*;
import com.eklinik.eklinikapi.service.DoctorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
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
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final ClinicRepository clinicRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    @Override
    @Transactional
    public DoctorResponse createDoctor(DoctorRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı, ID: " + request.getUserId()));

        Clinic clinic = clinicRepository.findById(request.getClinicId())
                .orElseThrow(() -> new RuntimeException("Klinik bulunamadı, ID: " + request.getClinicId()));

        user.setRole(UserRole.ROLE_DOCTOR);
        userRepository.save(user);

        Doctor doctor = Doctor.builder()
                .user(user)
                .clinic(clinic)
                .title(request.getTitle())
                .build();
        Doctor savedDoctor = doctorRepository.save(doctor);

        return mapToDoctorResponse(savedDoctor);
    }

    @Override
    public List<DoctorResponse> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(this::mapToDoctorResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DoctorResponse getDoctorById(Long id) {
        Doctor doctor = findDoctorEntityById(id);
        return mapToDoctorResponse(doctor);
    }

    @Override
    @Transactional
    public DoctorResponse updateDoctor(Long id, DoctorRequest request) {
        Doctor doctorToUpdate = findDoctorEntityById(id);
        User newUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı, ID: " + request.getUserId()));
        Clinic newClinic = clinicRepository.findById(request.getClinicId())
                .orElseThrow(() -> new RuntimeException("Klinik bulunamadı, ID: " + request.getClinicId()));
        newUser.setRole(UserRole.ROLE_DOCTOR);
        userRepository.save(newUser);

        doctorToUpdate.setUser(newUser);
        doctorToUpdate.setClinic(newClinic);
        doctorToUpdate.setTitle(request.getTitle());

        Doctor updatedDoctor = doctorRepository.save(doctorToUpdate);
        return mapToDoctorResponse(updatedDoctor);
    }

    @Override
    @Transactional
    public void deleteDoctor(Long id) {
        Doctor doctorToDelete = findDoctorEntityById(id);
        User user = doctorToDelete.getUser();

        user.setRole(UserRole.ROLE_PATIENT);
        userRepository.save(user);
        doctorRepository.delete(doctorToDelete);
    }

    @Override
    public List<AppointmentForDoctorResponse> getMyAppointments(UserDetails currentUser, LocalDate date) {
        User user = userRepository.findByNationalId(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        Doctor doctor = doctorRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Bu kullanıcıya ait doktor profili bulunamadı."));

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<Appointment> appointments = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetweenOrderByAppointmentTimeAsc(doctor.getId(), startOfDay, endOfDay);

        return appointments.stream()
                .map(this::mapToAppointmentForDoctorResponse)
                .collect(Collectors.toList());    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentDetailForDoctorResponse getAppointmentDetails(UserDetails currentUser, Long appointmentId) {
        Doctor doctor = findDoctorByUser(currentUser);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Randevu bulunamadı."));

        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new RuntimeException("Bu randevu detayını görme yetkiniz yok.");
        }

        User patientUser = appointment.getPatient();
        PatientProfile patientProfile = patientUser.getPatientProfile();

        List<PatientHistoryItemResponse> history = appointmentRepository
                .findByPatientIdAndDoctorIdAndStatusOrderByAppointmentTimeDesc(
                        patientUser.getId(), doctor.getId(), AppointmentStatus.COMPLETED)
                .stream()
                .map(this::mapToHistoryItemResponse)
                .collect(Collectors.toList());

        PatientDetailForDoctorResponse patientDetails = PatientDetailForDoctorResponse.builder()
                .firstName(patientUser.getFirstName())
                .lastName(patientUser.getLastName())
                .email(patientUser.getEmail())
                .phoneNumber(patientUser.getPhoneNumber())
                .dateOfBirth(patientProfile.getDateOfBirth())
                .weight(patientProfile.getWeight())
                .height(patientProfile.getHeight())
                .hasChronicIllness(patientProfile.getHasChronicIllness())
                .isMedicationDependent(patientProfile.getIsMedicationDependent())
                .address(patientProfile.getAddress())
                .history(history)
                .build();

        return AppointmentDetailForDoctorResponse.builder()
                .appointmentId(appointment.getId())
                .appointmentTime(appointment.getAppointmentTime())
                .status(appointment.getStatus())
                .patientDetails(patientDetails)
                .build();
    }

    @Override
    @Transactional
    public MedicalRecordResponse completeAppointment(UserDetails currentUser, Long appointmentId, CompleteAppointmentRequest request) {
        Doctor doctor = findDoctorByUser(currentUser);
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Randevu bulunamadı."));

        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new RuntimeException("Bu randevuyu tamamlama yetkiniz yok.");
        }
        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new RuntimeException("Sadece 'Zamanlanmış' durumdaki bir randevu tamamlanabilir.");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        MedicalRecord medicalRecord = MedicalRecord.builder()
                .appointment(appointment)
                .diagnosis(request.getDiagnosis())
                .notes(request.getNotes())
                .prescriptions(new ArrayList<>())
                .build();

        if (request.getPrescriptions() != null && !request.getPrescriptions().isEmpty()) {
            request.getPrescriptions().forEach(pr -> {
                Prescription prescription = Prescription.builder()
                        .medicalRecord(medicalRecord)
                        .medicationName(pr.getMedicationName())
                        .dosage(pr.getDosage())
                        .duration(pr.getDuration())
                        .build();
                medicalRecord.getPrescriptions().add(prescription);
            });
        }

        MedicalRecord savedRecord = medicalRecordRepository.save(medicalRecord);

        return mapToMedicalRecordResponse(savedRecord);
    }

    private Doctor findDoctorEntityById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı, ID: " + id));
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

    private AppointmentForDoctorResponse mapToAppointmentForDoctorResponse(Appointment appointment) {
        User patient = appointment.getPatient();
        UserResponse patientInfo = UserResponse.builder()
                .id(patient.getId())
                .nationalId(patient.getNationalId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .email(patient.getEmail())
                .phoneNumber(patient.getPhoneNumber())
                .build();

        return AppointmentForDoctorResponse.builder()
                .appointmentId(appointment.getId())
                .appointmentTime(appointment.getAppointmentTime())
                .status(appointment.getStatus())
                .patientInfo(patientInfo)
                .build();
    }

    private Doctor findDoctorByUser(UserDetails userDetails) {
        User user = userRepository.findByNationalId(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));
        return doctorRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Bu kullanıcıya ait doktor profili bulunamadı."));
    }

    private PatientHistoryItemResponse mapToHistoryItemResponse(Appointment pastAppointment) {
        String diagnosis = medicalRecordRepository.findByAppointmentId(pastAppointment.getId())
                .map(MedicalRecord::getDiagnosis)
                .orElse("Teşhis girilmemiş");

        return PatientHistoryItemResponse.builder()
                .appointmentId(pastAppointment.getId())
                .appointmentTime(pastAppointment.getAppointmentTime())
                .diagnosis(diagnosis)
                .build();
    }

    private MedicalRecordResponse mapToMedicalRecordResponse(MedicalRecord record) {
        List<String> prescriptionSummaries = record.getPrescriptions().stream()
                .map(p -> p.getMedicationName() + " - " + p.getDosage())
                .collect(Collectors.toList());

        return MedicalRecordResponse.builder()
                .recordId(record.getId())
                .appointmentId(record.getAppointment().getId())
                .diagnosis(record.getDiagnosis())
                .notes(record.getNotes())
                .prescriptions(prescriptionSummaries)
                .build();
    }
}
