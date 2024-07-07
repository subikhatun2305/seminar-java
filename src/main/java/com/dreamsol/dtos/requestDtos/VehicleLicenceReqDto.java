package com.dreamsol.dtos.requestDtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleLicenceReqDto extends CommonAutoIdEntityRequestDto {

    @NotEmpty(message = "Owner name must be provided.")
    @Size(min = 3, max = 50, message = "Driver name should be between 3 and 50 characters.")
    @Pattern(regexp = "^[A-Za-z]+(?:[\\s'][A-Za-z]+)*$", message = "Driver name should only contain alphabets and spaces.")
    @Schema(description = "Name of the driver", example = "John Doe")
    private String vehicleOwner;

    @NotEmpty(message = "Vehicle number must be provided.")
    @Size(min = 5, max = 20, message = "Vehicle number should be between 5 and 20 characters.")
    @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "Vehicle number should only contain alphabets, numbers, and hyphens.")
    @Schema(description = "Vehicle number", example = "AB-123-CD-4567")
    private String vehicleNumber;

    @NotEmpty(message = "Vehicle type must be provided.")
    @Size(min = 3, max = 30, message = "Vehicle type should be between 3 and 30 characters.")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Vehicle type should only contain alphabets and spaces.")
    @Schema(description = "Type of the vehicle", example = "Sedan")
    private String vehicleType;

    @NotNull(message = "Expiry date is required.")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$", message = "Expiry date must be in the format yyyy-MM-dd.")
    @Schema(description = "Licence expiry date", example = "2023-12-31")
    private String insuranceDate;

    @NotNull(message = "Expiry date is required.")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$", message = "Expiry date must be in the format yyyy-MM-dd.")
    @Schema(description = "Licence expiry date", example = "2023-12-31")
    private String pucDate;

    @NotNull(message = "Expiry date is required.")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$", message = "Expiry date must be in the format yyyy-MM-dd.")
    @Schema(description = "Licence expiry date", example = "2023-12-31")
    private String registrationDate;

    @NotEmpty(message = "Brief must be provided.")
    @Size(min = 10, max = 200, message = "Brief should be between 10 and 200 characters.")
    @Schema(description = "Brief description about the driver", example = "Experienced driver with a clean record.")
    private String brief;

}
