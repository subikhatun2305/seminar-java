package com.dreamsol.entites;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class VisitorPrerequest extends CommonAutoIdEntity {
    @Column(length = 50, nullable = false)
    private String name;
    @Column(nullable = false)
    private Long mobile;
    @Column(length = 100)
    private String email="";
    @Column(length = 50, nullable = false)
    private String organizationName;
    @Column(length = 100)
    private String address="";
    @Column(length = 50)
    private String possessionsAllowed="";
    @OneToOne
    private Purpose meetingPurpose;
    private LocalDateTime meetingSchedule;
    private LocalTime startHours;
    private LocalTime endHours;
    @Column(length = 50)
    private String location;
    @Column(length = 6, nullable = false)
    private String otp;
    @Column(length = 20, nullable = false)
    private String meetingStatus;
}
