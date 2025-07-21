package com.eklinik.eklinikapi.dto.request.schedule;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Data
public class ScheduleGenerationRequest {
    @NotNull(message = "Doktor ID'si boş olamaz.")
    private Long doctorId;

    @NotNull(message = "Başlangıç tarihi boş olamaz.")
    @FutureOrPresent(message = "Başlangıç tarihi geçmiş bir tarih olamaz.")
    private LocalDate startDate;

    @NotNull(message = "Bitiş tarihi boş olamaz.")
    @FutureOrPresent(message = "Bitiş tarihi geçmiş bir tarih olamaz.")
    private LocalDate endDate;

    @NotNull(message = "Çalışma başlangıç saati boş olamaz.")
    private LocalTime workStartTime;

    @NotNull(message = "Çalışma bitiş saati boş olamaz.")
    private LocalTime workEndTime;

    @NotEmpty(message = "En az bir çalışma günü seçilmelidir.")
    private Set<DayOfWeek> workDays;

    private LocalTime lunchStartTime;
    private LocalTime lunchEndTime;

    @NotNull(message = "Randevu aralığı boş olamaz.")
    @Min(value = 5, message = "Randevu aralığı en az 5 dakika olmalıdır.")
    private int slotDurationInMinutes;

}
