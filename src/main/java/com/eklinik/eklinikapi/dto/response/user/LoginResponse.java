package com.eklinik.eklinikapi.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private String role;

    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
    }

}
