package com.eklinik.eklinikapi.repository;

import com.eklinik.eklinikapi.enums.UserRole;
import com.eklinik.eklinikapi.model.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {
    public static Specification<User> findByCriteria(String searchTerm, UserRole role) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Arama terimi için kontrol
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String likePattern = "%" + searchTerm.toLowerCase() + "%";

                Predicate searchInFirstName = criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), likePattern);
                Predicate searchInLastName = criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), likePattern);
                Predicate searchInEmail = criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern);
                // YENİ: T.C. Kimlik No ile arama (sadece rakam içerdiği için lower case'e gerek yok)
                Predicate searchInNationalId = criteriaBuilder.like(root.get("nationalId"), "%" + searchTerm + "%");

                // Bu alanlardan herhangi birinde eşleşme olması yeterli (OR)
                predicates.add(criteriaBuilder.or(searchInFirstName, searchInLastName, searchInEmail, searchInNationalId));
            }

            // Rol filtresi için kontrol
            if (role != null) {
                predicates.add(criteriaBuilder.equal(root.get("role"), role));
            }

            // Koşul yoksa boş bir "where" cümlesi oluştur, varsa koşulları "AND" ile birleştir.
            return query.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                    .getRestriction();
        };
    }
}
