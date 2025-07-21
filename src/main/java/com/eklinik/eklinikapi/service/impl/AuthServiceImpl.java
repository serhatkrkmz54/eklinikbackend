package com.eklinik.eklinikapi.service.impl;

import com.eklinik.eklinikapi.dto.response.user.PatientProfileResponse;
import com.eklinik.eklinikapi.dto.response.user.UserResponse;
import com.eklinik.eklinikapi.dto.request.user.LoginRequest;
import com.eklinik.eklinikapi.dto.request.user.RegisterPatientCombinatedRequest;
import com.eklinik.eklinikapi.dto.request.user.UpdateMyUserRequest;
import com.eklinik.eklinikapi.dto.response.user.LoginResponse;
import com.eklinik.eklinikapi.enums.UserRole;
import com.eklinik.eklinikapi.exception.RegistrationException;
import com.eklinik.eklinikapi.model.PatientProfile;
import com.eklinik.eklinikapi.model.User;
import com.eklinik.eklinikapi.repository.UserRepository;
import com.eklinik.eklinikapi.security.JwtTokenProvider;
import com.eklinik.eklinikapi.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    public LoginResponse registerPatient(RegisterPatientCombinatedRequest registerRequest) {
        if (userRepository.existsByNationalId(registerRequest.getUserRequest().getNationalId())) {
            throw new RegistrationException("Bu TC Kimlik Numarası sistemimizde kayıtlıdır!");
        }
        if (userRepository.existsByEmail(registerRequest.getUserRequest().getEmail())) {
            throw new RegistrationException("Bu e-posta adresi sistemimizde kayıtlıdır!");
        }
        if (userRepository.existsByPhoneNumber(registerRequest.getUserRequest().getPhoneNumber())) {
            throw new RegistrationException("Bu telefon numarası sistemimizde kayıtlıdır!");
        }

        User user = User.builder()
                .nationalId(registerRequest.getUserRequest().getNationalId())
                .email(registerRequest.getUserRequest().getEmail())
                .password(passwordEncoder.encode(registerRequest.getUserRequest().getPassword()))
                .firstName(registerRequest.getUserRequest().getFirstName())
                .lastName(registerRequest.getUserRequest().getLastName())
                .phoneNumber(registerRequest.getUserRequest().getPhoneNumber())
                .role(UserRole.ROLE_PATIENT)
                .build();

        if (user.getRole() == UserRole.ROLE_PATIENT) {

            PatientProfile profile = PatientProfile.builder().build();

            if (registerRequest.getProfileRequest() != null) {
                profile.setDateOfBirth(registerRequest.getProfileRequest().getDateOfBirth());
                profile.setWeight(registerRequest.getProfileRequest().getWeight());
                profile.setHeight(registerRequest.getProfileRequest().getHeight());
                profile.setHasChronicIllness(registerRequest.getProfileRequest().getHasChronicIllness());
                profile.setIsMedicationDependent(registerRequest.getProfileRequest().getIsMedicationDependent());
                profile.setBirthPlaceCity(registerRequest.getProfileRequest().getBirthPlaceCity());
                profile.setBirthPlaceDistrict(registerRequest.getProfileRequest().getBirthPlaceDistrict());
                profile.setAddress(registerRequest.getProfileRequest().getAddress());
                profile.setCountry(registerRequest.getProfileRequest().getCountry());
            }
            user.setPatientProfile(profile);
            profile.setUser(user);
        }

        userRepository.save(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getNationalId(), null, Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())));

        return createLoginResponse(authentication);
    }

    @Override
    public LoginResponse loginUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getNationalId(),
                        loginRequest.getPassword()
                )
        );
        return createLoginResponse(authentication);
    }

    @Override
    public UserResponse getMyProfile(UserDetails currentUser) {
        User user = userRepository.findByNationalId(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı. Token geçersiz olabilir."));

        return mapToUserResponse(user);
    }

    private LoginResponse createLoginResponse(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);
        return new LoginResponse(jwt);
    }

    @Override
    @Transactional // Bu operasyonun bir bütün olarak çalışmasını sağlar.
    public UserResponse updateMyInfo(UserDetails currentUser, UpdateMyUserRequest request) {
        User userToUpdate = userRepository.findByNationalId(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı. Token geçersiz olabilir."));

        if (request.getFirstName() != null) userToUpdate.setFirstName(request.getFirstName());
        if (request.getLastName() != null) userToUpdate.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null) userToUpdate.setPhoneNumber(request.getPhoneNumber());
        if (request.getEmail() != null && !userToUpdate.getEmail().equals(request.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RegistrationException("Bu e-posta adresi zaten başka bir kullanıcı tarafından kullanılıyor!");
            }
            userToUpdate.setEmail(request.getEmail());
        }
        PatientProfile profile = userToUpdate.getPatientProfile();
        if (profile == null) {
            profile = new PatientProfile();
            userToUpdate.setPatientProfile(profile);
        }

        if (request.getDateOfBirth() != null) profile.setDateOfBirth(request.getDateOfBirth());
        if (request.getWeight() != null) profile.setWeight(request.getWeight());
        if (request.getHeight() != null) profile.setHeight(request.getHeight());
        if (request.getHasChronicIllness() != null) profile.setHasChronicIllness(request.getHasChronicIllness());
        if (request.getIsMedicationDependent() != null) profile.setIsMedicationDependent(request.getIsMedicationDependent());
        if (request.getBirthPlaceCity() != null) profile.setBirthPlaceCity(request.getBirthPlaceCity());
        if (request.getBirthPlaceDistrict() != null) profile.setBirthPlaceDistrict(request.getBirthPlaceDistrict());
        if (request.getAddress() != null) profile.setAddress(request.getAddress());
        if (request.getCountry() != null) profile.setCountry(request.getCountry());

        User updatedUser = userRepository.save(userToUpdate);
        return mapToUserResponse(updatedUser);
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse.UserResponseBuilder builder = UserResponse.builder()
                .id(user.getId())
                .nationalId(user.getNationalId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .createdAt(user.getCreatedAt());

        if (user.getPatientProfile() != null) {
            PatientProfile profile = user.getPatientProfile();
            PatientProfileResponse profileResponse = PatientProfileResponse.builder()
                    .dateOfBirth(profile.getDateOfBirth())
                    .weight(profile.getWeight())
                    .height(profile.getHeight())
                    .hasChronicIllness(profile.getHasChronicIllness())
                    .isMedicationDependent(profile.getIsMedicationDependent())
                    .birthPlaceCity(profile.getBirthPlaceCity())
                    .birthPlaceDistrict(profile.getBirthPlaceDistrict())
                    .address(profile.getAddress())
                    .country(profile.getCountry())
                    .build();

            builder.patientProfile(profileResponse);
        }

        return builder.build();
    }
}
