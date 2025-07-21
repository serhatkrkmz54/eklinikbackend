package com.eklinik.eklinikapi.service;

import com.eklinik.eklinikapi.dto.request.appointment.CompleteAppointmentRequest;
import com.eklinik.eklinikapi.dto.request.doctor.DoctorRequest;
import com.eklinik.eklinikapi.dto.request.doctor.UpdateDoctorRequest;
import com.eklinik.eklinikapi.dto.response.doctor.AppointmentDetailForDoctorResponse;
import com.eklinik.eklinikapi.dto.response.doctor.AppointmentForDoctorResponse;
import com.eklinik.eklinikapi.dto.response.doctor.DoctorResponse;
import com.eklinik.eklinikapi.dto.response.medicalrecord.MedicalRecordResponse;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.List;

public interface DoctorService {
    DoctorResponse createDoctor(DoctorRequest request);
    List<DoctorResponse> getAllDoctors();
    DoctorResponse getDoctorById(Long id);
    DoctorResponse updateDoctor(Long id, UpdateDoctorRequest request);
    void deleteDoctor(Long id);
    List<AppointmentForDoctorResponse> getMyAppointments(UserDetails currentUser, LocalDate date);
    AppointmentDetailForDoctorResponse getAppointmentDetails(UserDetails currentUser, Long appointmentId);
    MedicalRecordResponse completeAppointment(UserDetails currentUser, Long appointmentId, CompleteAppointmentRequest request);


}
