package com.eklinik.eklinikapi.startup;

import com.eklinik.eklinikapi.enums.UserRole;
import com.eklinik.eklinikapi.model.User;
import com.eklinik.eklinikapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByRole(UserRole.ROLE_ADMIN)) {

            log.info("Veritabanında admin bulunamadı, varsayılan admin oluşturuluyor...");

            User adminUser = User.builder()
                    .nationalId("11111111111")
                    .email("admin@eklinik.com")
                    .password(passwordEncoder.encode("123456"))
                    .firstName("Ana")
                    .lastName("Admin")
                    .phoneNumber("+905000000000")
                    .role(UserRole.ROLE_ADMIN)
                    .build();

            userRepository.save(adminUser);

            log.info("Varsayılan admin kullanıcısı başarıyla oluşturuldu. E-posta: admin@eklinik.com");
        } else {
            log.info("Veritabanında zaten bir admin mevcut, yeni admin oluşturulmadı.");
        }
    }
}
