package com.dreamsol.dtos.responseDtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VisitorResponseDto extends CommonAutoIdEntityResponseDto {
    private String visitorName;
    private String visitorCompany;
    private String visitorAddress;
    private UserResponseDto user;
    private PurposeResponseDto purpose;
    private DepartmentResponseDto department;
    private String possessionAllowed;
    private String visitorCardNumber;
    private String vehicleNumber;
    private String equipments;
    private boolean approvalRequired;
    private LocalDateTime validFrom;
    private LocalDateTime validTill;
    private Long phoneNumber;
}
