package com.eklinik.eklinikapi.controller;

import com.eklinik.eklinikapi.dto.request.user.EmergencyCallRequest;
import com.eklinik.eklinikapi.dto.response.user.EmergencyCallResponse;
import com.eklinik.eklinikapi.model.EmergencyCall;
import com.eklinik.eklinikapi.model.User;
import com.eklinik.eklinikapi.repository.EmergencyCallRepository;
import com.eklinik.eklinikapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/emergency")
@RequiredArgsConstructor
public class EmergencyController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final EmergencyCallRepository emergencyCallRepository;


    @PostMapping("/call")
    public ResponseEntity<Void> handleEmergencyCall(
            @RequestBody EmergencyCallRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {

        User patient = null;
        String callerName = "Acil Durum Çağrısı (Giriş Yapılmamış)";

        if (currentUser != null) {
            patient = userRepository.findByNationalId(currentUser.getUsername()).orElse(null);
            if (patient != null) {
                callerName = patient.getFirstName() + " " + patient.getLastName();
            }
        }

        EmergencyCall callLog = EmergencyCall.builder()
                .patient(patient)
                .callerName(callerName)
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .accuracy(request.getAccuracy())
                .build();
        emergencyCallRepository.save(callLog);

        EmergencyCallResponse response = EmergencyCallResponse.builder()
                .callId(callLog.getId())
                .patientFullName(callerName)
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .accuracy(request.getAccuracy())
                .callTime(callLog.getCallTime())
                .build();
        messagingTemplate.convertAndSend("/topic/emergency-alerts", response);

        return ResponseEntity.ok().build();
    }
}
