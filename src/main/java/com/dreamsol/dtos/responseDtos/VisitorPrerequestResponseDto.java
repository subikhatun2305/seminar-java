package com.dreamsol.dtos.responseDtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class VisitorPrerequestResponseDto extends CommonAutoIdEntityResponseDto {

    private String name;

    private Long mobile;

    private String email;

    private String organizationName;

    private String address;

    private String possessionsAllowed;

    private String meetingPurpose;

    private LocalDateTime meetingSchedule;

    private LocalTime startHours;

    private LocalTime endHours;

    private String location;

    private String meetingStatus;

}
