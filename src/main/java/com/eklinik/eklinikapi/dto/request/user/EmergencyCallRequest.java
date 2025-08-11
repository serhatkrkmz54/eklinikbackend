package com.eklinik.eklinikapi.dto.request.user;

import lombok.Data;

@Data
public class EmergencyCallRequest {
    private double latitude;
    private double longitude;
    private String address;
    private int accuracy;
}
