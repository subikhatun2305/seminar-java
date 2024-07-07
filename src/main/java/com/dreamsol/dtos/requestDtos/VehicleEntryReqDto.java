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
public class VehicleEntryReqDto extends CommonAutoIdEntityRequestDto {

    @NotNull(message = "Mobile number is required.")
    @Min(value = 6000000000L, message = "Mobile number must be a 10-digit number starting with 6, 7, 8, or 9.")
    @Max(value = 9999999999L, message = "Mobile number must be a 10-digit number.")
    @Schema(description = "Mobile number of the user", example = "9876543210")
    private Long driverMobile;

    @NotEmpty(message = "Vehicle number must be provided.")
    @Size(min = 5, max = 20, message = "Vehicle number should be between 5 and 20 characters.")
    @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "Vehicle number should only contain alphabets, numbers, and hyphens.")
    @Schema(description = "Vehicle number", example = "AB-123-CD-4567")
    private String vehicleNumber;

    @NotNull(message = "Plant ID is required.")
    private Long plantId;

    @NotNull(message = "Purpose ID is required.")
    private Long purposeId;

    @NotBlank(message = "Location from cannot be blank")
    @Size(max = 200, message = "Location from must be less than or equal to 200 characters")
    private String locationFrom;

    // Fields for "With Material" type entries
    @Size(max = 30, message = "Trip ID must be less than or equal to 30 characters")
    private String tripId;

    @Size(max = 100, message = "Invoice number must be less than or equal to 100 characters")
    private String invoiceNo;

    @Size(max = 200, message = "Material description must be less than or equal to 200 characters")
    private String materialDescription;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Long quantity;

    @Min(value = 1, message = "Number of bills must be at least 1")
    private Long numberOfBill;

    @Size(max = 200, message = "Destination to must be less than or equal to 200 characters")
    private String destinationTo;
}
