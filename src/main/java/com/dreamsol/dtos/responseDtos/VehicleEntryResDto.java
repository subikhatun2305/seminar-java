package com.dreamsol.dtos.responseDtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleEntryResDto extends CommonAutoIdEntityResponseDto{

//    private String vehicleNumber;
//    private String driverName;
//    private Long driverMobileNumber;
//    private String vehicleType;
//    private String vehicleOwner;
//    private String locationFrom;
//    private String plantTo;
//    private String tripId;
//    private String invoiceNo;
//    private String materialDescription;
//    private Long quantity;
//    private Long numberOfBill;
//    private String visitPurpose;
//    private String destinationTo;

    private DrivingLicenceResDto drivingLicenceResDto;

    private VehicleLicenceResDto vehicleLicenceResDto;

    private PlantResponseDto plantResponseDto;

    private  PurposeResponseDto purposeResponseDto;

    private String locationFrom;

    private String tripId;

    private String invoiceNo;

    private String materialDescription;

    private Long quantity;

    private Long numberOfBill;

    private String destinationTo;
}
