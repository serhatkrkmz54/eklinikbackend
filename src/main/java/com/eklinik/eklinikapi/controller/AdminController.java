package com.eklinik.eklinikapi.controller;

import com.eklinik.eklinikapi.dto.request.doctor.DoctorRequest;
import com.eklinik.eklinikapi.dto.request.doctor.UpdateDoctorRequest;
import com.eklinik.eklinikapi.dto.request.schedule.ScheduleGenerationRequest;
import com.eklinik.eklinikapi.dto.request.user.PatientProfileRequest;
import com.eklinik.eklinikapi.dto.request.admin.CreateUserRequest;
import com.eklinik.eklinikapi.dto.request.admin.UpdateUserRequest;
import com.eklinik.eklinikapi.dto.response.user.UserResponse;
import com.eklinik.eklinikapi.dto.request.clinics.ClinicRequest;
import com.eklinik.eklinikapi.dto.response.doctor.DoctorResponse;
import com.eklinik.eklinikapi.dto.response.schedule.ScheduleResponse;
import com.eklinik.eklinikapi.dto.response.user.PatientProfileResponse;
import com.eklinik.eklinikapi.dto.response.clinics.ClinicResponse;
import com.eklinik.eklinikapi.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final ClinicService clinicService;
    private final DoctorService doctorService;
    private final ScheduleService scheduleService;
    private final PatientService patientService;

    @PostMapping("/create-user")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse createdUser = adminService.createUser(request);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/patient-count")
    public ResponseEntity<Map<String, Long>> getPatientCount() {
        long count = patientService.getTotalPatientCount();
        Map<String, Long> response = Collections.singletonMap("count", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all-user")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = adminService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/delete/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok("Kullanıcı başarıyla silindi, ID: " + id);
    }

    @PutMapping("/update/users/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {

        UserResponse updatedUser = adminService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/update-patient-profile/users/{userId}")
    public ResponseEntity<PatientProfileResponse> updatePatientProfile(
            @PathVariable Long userId,
            @RequestBody PatientProfileRequest request) {

        PatientProfileResponse updatedProfile = adminService.updatePatientProfile(userId, request);
        return ResponseEntity.ok(updatedProfile);
    }

    @PostMapping("/create-clinics")
    public ResponseEntity<ClinicResponse> createClinic(@Valid @RequestBody ClinicRequest request) {
        ClinicResponse createdClinic = clinicService.createClinic(request);
        return new ResponseEntity<>(createdClinic, HttpStatus.CREATED);
    }

    @GetMapping("/clinic-count")
    public ResponseEntity<Map<String, Long>> getClinicCount() {
        long count = clinicService.getTotalClinicCount();
        Map<String, Long> response = Collections.singletonMap("count", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all-clinics")
    public ResponseEntity<List<ClinicResponse>> getAllClinics() {
        return ResponseEntity.ok(clinicService.getAllClinics());
    }

    @GetMapping("/clinics/{id}")
    public ResponseEntity<ClinicResponse> getClinicById(@PathVariable Integer id) {
        return ResponseEntity.ok(clinicService.getClinicById(id));
    }

    @PutMapping("/update-clinics/clinics/{id}")
    public ResponseEntity<ClinicResponse> updateClinic(@PathVariable Integer id, @Valid @RequestBody ClinicRequest request) {
        return ResponseEntity.ok(clinicService.updateClinic(id, request));
    }

    @DeleteMapping("/clinics/{id}")
    public ResponseEntity<String> deleteClinic(@PathVariable Integer id) {
        clinicService.deleteClinic(id);
        return ResponseEntity.ok("Klinik başarıyla silindi, ID: " + id);
    }

    @GetMapping("/doctor-count")
    public ResponseEntity<Map<String, Long>> getDoctorCount() {
        long count = doctorService.getTotalDoctorCount();
        Map<String, Long> response = Collections.singletonMap("count", count);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-doctors")
    public ResponseEntity<DoctorResponse> createDoctor(@Valid @RequestBody DoctorRequest request) {
        DoctorResponse createdDoctor = doctorService.createDoctor(request);
        return new ResponseEntity<>(createdDoctor, HttpStatus.CREATED);
    }

    @GetMapping("/get-doctors")
    public ResponseEntity<List<DoctorResponse>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/get-doctors/{id}")
    public ResponseEntity<DoctorResponse> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @PutMapping("/update-doctors/doctors/{id}")
    public ResponseEntity<DoctorResponse> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDoctorRequest request) {
        DoctorResponse updatedDoctor = doctorService.updateDoctor(id, request);
        return ResponseEntity.ok(updatedDoctor);
    }

    @DeleteMapping("/delete/doctors/{id}")
    public ResponseEntity<String> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.ok("Doktor profili başarıyla silindi ve kullanıcının rolü HASTA olarak güncellendi. ID: " + id);
    }

    @PostMapping("/schedules/generate")
    public ResponseEntity<String> generateSchedules(@Valid @RequestBody ScheduleGenerationRequest request) {
        scheduleService.generateSchedules(request);
        return ResponseEntity.ok("Doktor için takvim başarıyla oluşturuldu.");
    }

    @GetMapping("/doctors/{doctorId}/schedules")
    public ResponseEntity<List<ScheduleResponse>> getSchedulesForDoctor(
            @PathVariable Long doctorId,
            @RequestParam(value = "date", required = false) LocalDate date) {
        List<ScheduleResponse> schedules = scheduleService.getSchedulesForDoctorByDate(doctorId, date);
        return ResponseEntity.ok(schedules);
    }

    @DeleteMapping("/schedules/{scheduleId}")
    public ResponseEntity<String> deleteScheduleSlot(@PathVariable Long scheduleId) {
        scheduleService.deleteScheduleSlot(scheduleId);
        return ResponseEntity.ok("Randevu zaman dilimi başarıyla silindi.");
    }

}
