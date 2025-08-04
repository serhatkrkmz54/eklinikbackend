package com.eklinik.eklinikapi.repository;

import com.eklinik.eklinikapi.enums.UserRole;
import com.eklinik.eklinikapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByNationalId(String nationalId);

    Boolean existsByNationalId(String nationalId);

    Boolean existsByEmail(String email);

    Boolean existsByRole(UserRole role);

    Boolean existsByPhoneNumber(String phoneNumber);

    long countByRole(UserRole role);

    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdEvenIfDeleted(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            value = "UPDATE users SET deleted = false WHERE id = :id",
            nativeQuery = true
    )
    void reactivateUserById(@Param("id") Long id);
}
