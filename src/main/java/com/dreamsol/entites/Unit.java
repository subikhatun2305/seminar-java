package com.dreamsol.entites;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Unit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(length = 50, updatable = false)
    private String createdBy;

    @Column(length = 50)
    private String updatedBy;

    private boolean status;

    @Column(nullable = false, length = 50, unique = true)
    private String unitName;

    @Column(nullable = false, length = 50, unique = true)
    private String unitIp;

    @Column(nullable = false, length = 50)
    private String unitCity;

    @Column(nullable = false, length = 50)
    private String passAddress;

    @Column(nullable = false, length = 50)
    private String passDisclaimer;

}
