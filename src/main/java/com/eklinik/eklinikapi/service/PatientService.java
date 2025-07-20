package com.eklinik.eklinikapi.service;

import com.eklinik.eklinikapi.dto.response.appointment.AppointmentDetailForPatientResponse;
import com.eklinik.eklinikapi.dto.response.appointment.AppointmentResponse;
import com.eklinik.eklinikapi.dto.response.clinics.ClinicResponse;
import com.eklinik.eklinikapi.dto.response.doctor.DoctorResponse;
import com.eklinik.eklinikapi.dto.response.schedule.ScheduleResponse;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.List;

public interface PatientService {
    List<ClinicResponse> getAllClinics();
    List<DoctorResponse> getDoctorsByClinic(Integer clinicId);
    List<ScheduleResponse> getAvailableSlots(Long doctorId, LocalDate date);

    AppointmentResponse bookAppointment(UserDetails currentUser, Long scheduleId);
    List<AppointmentResponse> getMyHistory(UserDetails currentUser);
    void cancelAppointment(UserDetails currentUser, Long appointmentId);
    AppointmentDetailForPatientResponse getMyAppointmentDetails(UserDetails currentUser, Long appointmentId);

}
