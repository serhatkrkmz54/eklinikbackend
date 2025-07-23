package com.eklinik.eklinikapi.controller;

import com.eklinik.eklinikapi.dto.response.appointment.AppointmentDetailForPatientResponse;
import com.eklinik.eklinikapi.dto.response.appointment.AppointmentResponse;
import com.eklinik.eklinikapi.dto.response.clinics.ClinicResponse;
import com.eklinik.eklinikapi.dto.response.doctor.DoctorResponse;
import com.eklinik.eklinikapi.dto.response.schedule.ScheduleResponse;
import com.eklinik.eklinikapi.service.DoctorService;
import com.eklinik.eklinikapi.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final DoctorService doctorService;

    @GetMapping("/clinics")
    public ResponseEntity<List<ClinicResponse>> getAllClinics() {
        return ResponseEntity.ok(patientService.getAllClinics());
    }

    @GetMapping("/clinics/{clinicId}/doctors")
    public ResponseEntity<List<DoctorResponse>> getDoctorsByClinic(@PathVariable Integer clinicId) {
        return ResponseEntity.ok(patientService.getDoctorsByClinic(clinicId));
    }

    @GetMapping("/doctors/{doctorId}/slots")
    public ResponseEntity<List<ScheduleResponse>> getAvailableSlots(
            @PathVariable Long doctorId,
            @RequestParam("date") LocalDate date) {
        return ResponseEntity.ok(patientService.getSlotsByDoctorAndDate(doctorId, date));
    }

    @GetMapping("/doctors/{doctorId}/slots-in-range")
    public ResponseEntity<Map<LocalDate, List<ScheduleResponse>>> getSlotsInRange(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(patientService.getSlotsForDateRange(doctorId, startDate, endDate));
    }

    @PostMapping("/appointments/{scheduleId}")
    public ResponseEntity<AppointmentResponse> bookAppointment(
            @AuthenticationPrincipal UserDetails currentUser,
            @PathVariable Long scheduleId) {
        return ResponseEntity.ok(patientService.bookAppointment(currentUser, scheduleId));
    }

    @GetMapping("/appointments/my-history")
    public ResponseEntity<List<AppointmentResponse>> getMyHistory(@AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(patientService.getMyHistory(currentUser));
    }

    @PutMapping("/appointments/{appointmentId}/cancel")
    public ResponseEntity<String> cancelAppointment(
            @AuthenticationPrincipal UserDetails currentUser,
            @PathVariable Long appointmentId) {
        patientService.cancelAppointment(currentUser, appointmentId);
        return ResponseEntity.ok("Randevu başarıyla iptal edildi.");
    }

    @GetMapping("/appointments/{appointmentId}/details")
    public ResponseEntity<AppointmentDetailForPatientResponse> getMyAppointmentDetails(
            @AuthenticationPrincipal UserDetails currentUser,
            @PathVariable Long appointmentId) {

        AppointmentDetailForPatientResponse response = patientService.getMyAppointmentDetails(currentUser, appointmentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/doctors/{doctorId}")
    public ResponseEntity<DoctorResponse> getDoctorProfileById(@PathVariable Long doctorId) {
        return ResponseEntity.ok(doctorService.getDoctorById(doctorId));
    }
}
