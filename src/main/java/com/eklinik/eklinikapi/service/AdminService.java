package com.eklinik.eklinikapi.service;

import com.eklinik.eklinikapi.dto.request.user.PatientProfileRequest;
import com.eklinik.eklinikapi.dto.request.admin.CreateUserRequest;
import com.eklinik.eklinikapi.dto.request.admin.UpdateUserRequest;
import com.eklinik.eklinikapi.dto.response.user.UserResponse;
import com.eklinik.eklinikapi.dto.response.user.PatientProfileResponse;
import com.eklinik.eklinikapi.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {
    UserResponse createUser(CreateUserRequest request);
    Page<UserResponse> getAllUsers(String searchTerm, UserRole role, Pageable pageable);
    UserResponse getUserById(Long id);
    void deleteUser(Long id);
    UserResponse updateUser(Long id, UpdateUserRequest request);
    PatientProfileResponse updatePatientProfile(Long userId, PatientProfileRequest request);
}
