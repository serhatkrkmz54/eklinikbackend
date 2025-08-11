package com.eklinik.eklinikapi.dto.response.clinics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClinicAppointmentCountDataResponse {

    private String clinicName;
    private long appointmentCount;

}
