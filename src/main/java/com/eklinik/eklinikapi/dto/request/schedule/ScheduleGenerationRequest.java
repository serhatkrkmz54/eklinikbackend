package com.eklinik.eklinikapi.dto.request.schedule;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleGenerationRequest {
    @NotNull(message = "Doktor ID'si boş olamaz.")
    private Long doctorId;

    @NotNull(message = "Çalışma başlangıç zamanı boş olamaz.")
    @FutureOrPresent(message = "Çalışma başlangıcı geçmiş bir tarih olamaz.")
    private LocalDateTime workdayStart;

    @NotNull(message = "Çalışma bitiş zamanı boş olamaz.")
    @FutureOrPresent(message = "Çalışma bitişi geçmiş bir tarih olamaz.")
    private LocalDateTime workdayEnd;

    // Öğle arası opsiyoneldir, bu yüzden validation yok.
    private LocalDateTime lunchStart;
    private LocalDateTime lunchEnd;

    @NotNull(message = "Randevu aralığı boş olamaz.")
    @Min(value = 5, message = "Randevu aralığı en az 5 dakika olmalıdır.")
    private int slotDurationInMinutes;

}
