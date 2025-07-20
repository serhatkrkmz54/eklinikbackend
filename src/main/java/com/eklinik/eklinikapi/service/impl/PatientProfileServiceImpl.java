package com.eklinik.eklinikapi.service.impl;

import com.eklinik.eklinikapi.dto.request.user.PatientProfileRequest;
import com.eklinik.eklinikapi.dto.response.user.PatientProfileResponse;
import com.eklinik.eklinikapi.model.PatientProfile;
import com.eklinik.eklinikapi.model.User;
import com.eklinik.eklinikapi.repository.PatientProfileRepository;
import com.eklinik.eklinikapi.repository.UserRepository;
import com.eklinik.eklinikapi.service.PatientProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientProfileServiceImpl implements PatientProfileService {
    private final UserRepository userRepository;
    private final PatientProfileRepository patientProfileRepository;

    @Override
    public PatientProfileResponse getPatientProfile(UserDetails userDetails) {
        User user = findUserByUserDetails(userDetails);
        PatientProfile profile = findProfileByUserId(user.getId());
        return mapEntityToResponse(profile);
    }

    @Override
    public PatientProfileResponse updatePatientProfile(UserDetails userDetails, PatientProfileRequest request) {
        User user = findUserByUserDetails(userDetails);
        PatientProfile profile = findProfileByUserId(user.getId());
        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setWeight(request.getWeight());
        profile.setHeight(request.getHeight());
        profile.setHasChronicIllness(request.getHasChronicIllness());
        profile.setIsMedicationDependent(request.getIsMedicationDependent());
        profile.setBirthPlaceCity(request.getBirthPlaceCity());
        profile.setBirthPlaceDistrict(request.getBirthPlaceDistrict());
        profile.setAddress(request.getAddress());

        PatientProfile updatedProfile = patientProfileRepository.save(profile);
        return mapEntityToResponse(updatedProfile);
    }

    private User findUserByUserDetails(UserDetails userDetails) {
        return userRepository.findByNationalId(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));
    }

    private PatientProfile findProfileByUserId(Long userId) {
        return patientProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Hasta profili bulunamadı."));
    }

    private PatientProfileResponse mapEntityToResponse(PatientProfile entity) {
        return PatientProfileResponse.builder()
                .dateOfBirth(entity.getDateOfBirth())
                .weight(entity.getWeight())
                .height(entity.getHeight())
                .hasChronicIllness(entity.getHasChronicIllness())
                .isMedicationDependent(entity.getIsMedicationDependent())
                .birthPlaceCity(entity.getBirthPlaceCity())
                .birthPlaceDistrict(entity.getBirthPlaceDistrict())
                .address(entity.getAddress())
                .build();
    }

}