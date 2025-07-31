package com.eklinik.eklinikapi.repository;

import com.eklinik.eklinikapi.enums.UserRole;
import com.eklinik.eklinikapi.model.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {
    public static Specification<User> findByCriteria(String searchTerm, UserRole role, String status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String likePattern = "%" + searchTerm.toLowerCase() + "%";

                Predicate searchInFirstName = criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), likePattern);
                Predicate searchInLastName = criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), likePattern);
                Predicate searchInEmail = criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern);
                Predicate searchInNationalId = criteriaBuilder.like(root.get("nationalId"), "%" + searchTerm + "%");

                predicates.add(criteriaBuilder.or(searchInFirstName, searchInLastName, searchInEmail, searchInNationalId));
            }

            if (role != null) {
                predicates.add(criteriaBuilder.equal(root.get("role"), role));
            }

            if (status != null) {
                if (status.equalsIgnoreCase("PASSIVE")) {
                    predicates.add(criteriaBuilder.isTrue(root.get("deleted")));
                } else if (status.equalsIgnoreCase("ACTIVE")) {
                    predicates.add(criteriaBuilder.isFalse(root.get("deleted")));
                }
            } else {
                predicates.add(criteriaBuilder.isFalse(root.get("deleted")));
            }

            return query.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                    .getRestriction();
        };
    }
}
