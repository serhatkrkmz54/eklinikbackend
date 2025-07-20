package com.eklinik.eklinikapi.repository;

import com.eklinik.eklinikapi.enums.UserRole;
import com.eklinik.eklinikapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByNationalId(String nationalId);

    Boolean existsByNationalId(String nationalId);

    Boolean existsByEmail(String email);

    Boolean existsByRole(UserRole role);

}
