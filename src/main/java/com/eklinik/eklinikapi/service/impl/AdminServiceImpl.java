package com.eklinik.eklinikapi.service.impl;

import com.eklinik.eklinikapi.dto.request.user.PatientProfileRequest;
import com.eklinik.eklinikapi.dto.request.admin.CreateUserRequest;
import com.eklinik.eklinikapi.dto.request.admin.UpdateUserRequest;
import com.eklinik.eklinikapi.dto.response.user.UserResponse;
import com.eklinik.eklinikapi.dto.response.user.PatientProfileResponse;
import com.eklinik.eklinikapi.enums.UserRole;
import com.eklinik.eklinikapi.exception.ResourceAlreadyExistsException;
import com.eklinik.eklinikapi.model.PatientProfile;
import com.eklinik.eklinikapi.model.User;
import com.eklinik.eklinikapi.repository.PatientProfileRepository;
import com.eklinik.eklinikapi.repository.UserRepository;
import com.eklinik.eklinikapi.repository.UserSpecification;
import com.eklinik.eklinikapi.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PatientProfileRepository patientProfileRepository;

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByNationalId(request.getNationalId())) {
            throw new ResourceAlreadyExistsException("Bu TC Kimlik Numarası zaten kayıtlı!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Bu e-posta adresi zaten kayıtlı!");
        }

        User user = User.builder()
                .nationalId(request.getNationalId())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .build();

        if (user.getRole() == UserRole.ROLE_PATIENT) {
            PatientProfile profile = PatientProfile.builder().build();
            user.setPatientProfile(profile);
            profile.setUser(user);
        }

        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    @Override
    public Page<UserResponse> getAllUsers(String searchTerm, UserRole role, Pageable pageable, String status) {
        Specification<User> spec = UserSpecification.findByCriteria(searchTerm, role, status);
        Page<User> userPage = userRepository.findAll(spec, pageable);
        return userPage.map(this::mapToUserResponse);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı, ID: " + id));
        return mapToUserResponse(user);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Silinecek kullanıcı bulunamadı, ID: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı, ID: " + id));

        if (request.getEmail() != null && !userToUpdate.getEmail().equals(request.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Bu e-posta adresi zaten başka bir kullanıcı tarafından kullanılıyor!");
            }
            userToUpdate.setEmail(request.getEmail());
        }

        if (request.getFirstName() != null) {
            userToUpdate.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            userToUpdate.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            userToUpdate.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getRole() != null) {
            userToUpdate.setRole(request.getRole());
        }

        User updatedUser = userRepository.save(userToUpdate);
        return mapToUserResponse(updatedUser);
    }

    @Override
    public PatientProfileResponse updatePatientProfile(Long userId, PatientProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı, ID: " + userId));

        if (user.getRole() != UserRole.ROLE_PATIENT) {
            throw new RuntimeException("Sadece hastaların profili güncellenebilir. Kullanıcı ID: " + userId);
        }

        PatientProfile profileToUpdate = patientProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Hasta profili bulunamadı, Kullanıcı ID: " + userId));

        profileToUpdate.setDateOfBirth(request.getDateOfBirth());
        profileToUpdate.setWeight(request.getWeight());
        profileToUpdate.setHeight(request.getHeight());
        profileToUpdate.setHasChronicIllness(request.getHasChronicIllness());
        profileToUpdate.setIsMedicationDependent(request.getIsMedicationDependent());
        profileToUpdate.setBirthPlaceCity(request.getBirthPlaceCity());
        profileToUpdate.setBirthPlaceDistrict(request.getBirthPlaceDistrict());
        profileToUpdate.setAddress(request.getAddress());
        profileToUpdate.setCountry(request.getCountry());

        PatientProfile updatedProfile = patientProfileRepository.save(profileToUpdate);

        return mapToPatientProfileResponse(updatedProfile);
    }

    @Override
    public UserResponse reactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceAlreadyExistsException("Kullanıcı bulunamadı, ID: " + id));
        if (!user.isDeleted()) {
            throw new ResourceAlreadyExistsException("Kullanıcı zaten aktif durumda.");
        }
        user.setDeleted(false);
        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse.UserResponseBuilder responseBuilder = UserResponse.builder()
                .id(user.getId())
                .nationalId(user.getNationalId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .deleted(user.isDeleted())
                .role(user.getRole())
                .createdAt(user.getCreatedAt());
        if (user.getPatientProfile() != null) {
            responseBuilder.patientProfile(mapToPatientProfileResponse(user.getPatientProfile()));
        }

        return responseBuilder.build();
    }
    private PatientProfileResponse mapToPatientProfileResponse(PatientProfile entity) {
        return PatientProfileResponse.builder()
                .dateOfBirth(entity.getDateOfBirth())
                .weight(entity.getWeight())
                .height(entity.getHeight())
                .hasChronicIllness(entity.getHasChronicIllness())
                .isMedicationDependent(entity.getIsMedicationDependent())
                .birthPlaceCity(entity.getBirthPlaceCity())
                .birthPlaceDistrict(entity.getBirthPlaceDistrict())
                .address(entity.getAddress())
                .country(entity.getCountry())
                .build();
    }
}
