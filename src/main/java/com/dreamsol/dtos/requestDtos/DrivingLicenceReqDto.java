package com.dreamsol.dtos.requestDtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DrivingLicenceReqDto extends CommonAutoIdEntityRequestDto
{
    @NotEmpty(message = "Driver name must be provided.")
    @Size(min = 3, max = 50, message = "Driver name should be between 3 and 50 characters.")
    @Pattern(regexp = "^[A-Za-z]+(?:[\\s'][A-Za-z]+)*$", message = "Driver name should only contain alphabets and spaces.")
    @Schema(description = "Name of the driver", example = "John Doe")
    private String driverName;

    @NotNull(message = "Mobile number is required.")
    @Min(value = 6000000000L, message = "Mobile number must be a 10-digit number starting with 6, 7, 8, or 9.")
    @Max(value = 9999999999L, message = "Mobile number must be a 10-digit number.")
    @Schema(description = "Mobile number of the user", example = "9876543210")
    private Long driverMobile;

    @NotEmpty(message = "Licence must be provided.")
    @Size(min = 5, max = 20, message = "Licence should be between 5 and 20 characters.")
    @Schema(description = "Driver's licence number", example = "DL1234567890")
    private String licence;

    @NotNull(message = "Expiry date is required.")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$", message = "Expiry date must be in the format yyyy-MM-dd.")
    @Schema(description = "Licence expiry date", example = "2023-12-31")
    private String expDate;

    @NotEmpty(message = "Brief must be provided.")
    @Size(min = 10, max = 200, message = "Brief should be between 10 and 200 characters.")
    @Schema(description = "Brief description about the driver", example = "Experienced driver with a clean record.")
    private String brief;

//    public DrivingLicenceReqDto(Long unitId, String driverName, Long driverMobile, String licence, String expDate, String brief) {
//        super(unitId);
//        this.driverName = driverName;
//        this.driverMobile = driverMobile;
//        this.licence = licence;
//        this.expDate = expDate;
//        this.brief = brief;
//    }
}
