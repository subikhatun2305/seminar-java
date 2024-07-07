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
public class DrivingLicenceResDto extends CommonAutoIdEntityResponseDto {

    private String driverName;

    private Long driverMobile;

    private String licence;

    private LocalDate expDate;

    private String brief;

    private String FileUrl;
}
