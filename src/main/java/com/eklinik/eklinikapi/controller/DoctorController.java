package com.eklinik.eklinikapi.controller;

import com.eklinik.eklinikapi.dto.request.appointment.CompleteAppointmentRequest;
import com.eklinik.eklinikapi.dto.response.doctor.AppointmentDetailForDoctorResponse;
import com.eklinik.eklinikapi.dto.response.doctor.AppointmentForDoctorResponse;
import com.eklinik.eklinikapi.dto.response.medicalrecord.MedicalRecordResponse;
import com.eklinik.eklinikapi.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_DOCTOR')")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentForDoctorResponse>> getMyAppointments(
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestParam("date") LocalDate date) {

        List<AppointmentForDoctorResponse> appointments = doctorService.getMyAppointments(currentUser, date);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/appointments/{appointmentId}")
    public ResponseEntity<AppointmentDetailForDoctorResponse> getAppointmentDetails(
            @AuthenticationPrincipal UserDetails currentUser,
            @PathVariable Long appointmentId) {

        AppointmentDetailForDoctorResponse response = doctorService.getAppointmentDetails(currentUser, appointmentId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/appointments/{appointmentId}/complete")
    public ResponseEntity<MedicalRecordResponse> completeAppointment(
            @AuthenticationPrincipal UserDetails currentUser,
            @PathVariable Long appointmentId,
            @Valid @RequestBody CompleteAppointmentRequest request) {

        MedicalRecordResponse response = doctorService.completeAppointment(currentUser, appointmentId, request);
        return ResponseEntity.ok(response);
    }

}
