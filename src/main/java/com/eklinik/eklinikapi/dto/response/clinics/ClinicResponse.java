package com.eklinik.eklinikapi.dto.response.clinics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClinicResponse {
    private Integer id;
    private String name;
}
