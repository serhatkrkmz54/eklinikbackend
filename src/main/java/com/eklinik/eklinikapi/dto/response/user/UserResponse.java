package com.eklinik.eklinikapi.dto.response.user;

import com.eklinik.eklinikapi.enums.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String nationalId;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private UserRole role;
    private LocalDateTime createdAt;
    private PatientProfileResponse patientProfile;
}
