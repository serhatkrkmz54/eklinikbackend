package com.eklinik.eklinikapi.service.impl;

import com.eklinik.eklinikapi.dto.request.clinics.ClinicRequest;
import com.eklinik.eklinikapi.dto.response.clinics.ClinicResponse;
import com.eklinik.eklinikapi.exception.ResourceAlreadyExistsException;
import com.eklinik.eklinikapi.model.Clinic;
import com.eklinik.eklinikapi.repository.ClinicRepository;
import com.eklinik.eklinikapi.service.ClinicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClinicServiceImpl implements ClinicService {

    private final ClinicRepository clinicRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public ClinicResponse createClinic(ClinicRequest request) {
        if (clinicRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new ResourceAlreadyExistsException("Bu isme sahip bir klinik zaten mevcut!");
        }
        Clinic clinic = Clinic.builder()
                .name(request.getName().trim())
                .build();
        Clinic savedClinic = clinicRepository.save(clinic);
        ClinicResponse response = mapToClinicResponse(savedClinic);
        messagingTemplate.convertAndSend("/topic/clinics", response);
        return response;
    }

    @Override
    public ClinicResponse updateClinic(Integer id, ClinicRequest request) {
        Clinic clinicToUpdate = findClinicEntityById(id);
        clinicToUpdate.setName(request.getName());
        Clinic updatedClinic = clinicRepository.save(clinicToUpdate);
        return mapToClinicResponse(updatedClinic);
    }

    @Override
    public void deleteClinic(Integer id) {
        Clinic clinicToDelete = findClinicEntityById(id);
        clinicRepository.delete(clinicToDelete);
    }

    @Override
    public ClinicResponse getClinicById(Integer id) {
        return mapToClinicResponse(findClinicEntityById(id));
    }

    @Override
    public List<ClinicResponse> getAllClinics() {
        return clinicRepository.findAll().stream()
                .map(this::mapToClinicResponse)
                .collect(Collectors.toList());
    }

    @Override
    public long getTotalClinicCount() {
//        log.info("Toplam klinik sayısı sorgulanıyor.");
        return clinicRepository.count();
    }

    private Clinic findClinicEntityById(Integer id) {
        return clinicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Klinik bulunamadı, ID: " + id));
    }

    private ClinicResponse mapToClinicResponse(Clinic clinic) {
        return ClinicResponse.builder()
                .id(clinic.getId())
                .name(clinic.getName())
                .build();
    }
}
