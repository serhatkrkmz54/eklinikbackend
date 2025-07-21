package com.eklinik.eklinikapi.service;

import com.eklinik.eklinikapi.dto.response.user.UserResponse;
import com.eklinik.eklinikapi.dto.request.user.LoginRequest;
import com.eklinik.eklinikapi.dto.request.user.RegisterPatientCombinatedRequest;
import com.eklinik.eklinikapi.dto.request.user.UpdateMyUserRequest;
import com.eklinik.eklinikapi.dto.response.user.LoginResponse;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {
    LoginResponse registerPatient(RegisterPatientCombinatedRequest registerRequest);
    LoginResponse loginUser(LoginRequest loginRequest);
    UserResponse updateMyInfo(UserDetails currentUser, UpdateMyUserRequest request);
    UserResponse getMyProfile(UserDetails currentUser);
}
