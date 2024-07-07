package com.dreamsol.dtos.responseDtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleLicenceResDto extends CommonAutoIdEntityResponseDto{

    private String vehicleOwner;

    private String vehicleNumber;

    private String vehicleType;

    private LocalDate insuranceDate;

    private LocalDate pucDate;

    private LocalDate registrationDate;

    private String brief;

    private String pucUrl;

    private String insuranceUrl;

    private String registrationUrl;
}
