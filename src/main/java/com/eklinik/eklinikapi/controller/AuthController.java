package com.eklinik.eklinikapi.controller;

import com.eklinik.eklinikapi.dto.request.admin.UserResponse;
import com.eklinik.eklinikapi.dto.request.user.LoginRequest;
import com.eklinik.eklinikapi.dto.request.user.RegisterPatientCombinatedRequest;
import com.eklinik.eklinikapi.dto.request.user.UpdateMyUserRequest;
import com.eklinik.eklinikapi.dto.response.user.LoginResponse;
import com.eklinik.eklinikapi.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> registerPatient(@RequestBody @Valid RegisterPatientCombinatedRequest registerRequest) {
        LoginResponse loginResponse = authService.registerPatient(registerRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.loginUser(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @PutMapping("/update-profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> updateMyInfo(
            @AuthenticationPrincipal UserDetails currentUser,
            @Valid @RequestBody UpdateMyUserRequest request) {
        UserResponse updatedUser = authService.updateMyInfo(currentUser, request);
        return ResponseEntity.ok(updatedUser);
    }
}
