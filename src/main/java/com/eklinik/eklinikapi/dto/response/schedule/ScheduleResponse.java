package com.eklinik.eklinikapi.dto.response.schedule;

import com.eklinik.eklinikapi.enums.ScheduleStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ScheduleResponse {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ScheduleStatus status;
}
