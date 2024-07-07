package com.dreamsol.entites;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Visitor extends CommonAutoIdEntity {
    @Column(length = 50, nullable = false)
    private String visitorName;

    @Column(length = 50, nullable = true)
    private String visitorCompany;

    @Column(length = 100, nullable = true)
    private String visitorAddress;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "purposeId")
    Purpose purpose;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "departmentId")
    Department department;

    @Column(length = 100, nullable = true)
    private String possessionAllowed;

    @Column(length = 100, nullable = true)
    private String visitorCardNumber;

    @Column(length = 100, nullable = true)
    private String vehicleNumber;

    @Column(length = 100, nullable = true)
    private String equipments;

    private boolean approvalRequired;

    @Column(length = 100, nullable = true)
    private LocalDateTime validFrom;

    @Column(length = 100, nullable = true)
    private LocalDateTime validTill;

    @Column(nullable = false)
    private Long phoneNumber;

}
