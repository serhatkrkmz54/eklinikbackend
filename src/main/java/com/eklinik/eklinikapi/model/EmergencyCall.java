package com.eklinik.eklinikapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "emergency_calls")
@Builder
@Entity
public class EmergencyCall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User patient;

    @Column(nullable = false)
    private String callerName;

    @Column(columnDefinition = "TEXT")
    private String address;

    private double latitude;
    private double longitude;
    private int accuracy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime callTime;

}
