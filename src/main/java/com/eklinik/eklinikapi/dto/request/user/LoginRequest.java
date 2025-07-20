package com.eklinik.eklinikapi.dto.request.user;

import lombok.Data;

@Data
public class LoginRequest {
    private String nationalId;
    private String password;
}
