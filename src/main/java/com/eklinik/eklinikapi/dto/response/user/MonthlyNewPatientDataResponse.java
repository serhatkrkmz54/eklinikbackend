package com.eklinik.eklinikapi.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyNewPatientDataResponse {
    private String month;
    private long count;
}
