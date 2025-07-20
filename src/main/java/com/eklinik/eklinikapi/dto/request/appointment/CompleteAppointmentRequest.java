package com.eklinik.eklinikapi.dto.request.appointment;

import com.eklinik.eklinikapi.dto.request.presciption.PrescriptionRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CompleteAppointmentRequest {
    @NotBlank(message = "Teşhis boş olamaz.")
    private String diagnosis;

    private String notes;

    @Valid
    private List<PrescriptionRequest> prescriptions;
}
