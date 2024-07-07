package com.dreamsol.utility;

import com.dreamsol.dtos.requestDtos.*;
import com.dreamsol.dtos.responseDtos.*;
import com.dreamsol.entites.*;
import com.dreamsol.securities.JwtUtil;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.BiFunction;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class DtoUtilities {

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    public BiFunction<Long, String, DropDownDto> createDropDown = DropDownDto::new;

    public User userRequstDtoToUser(UserRequestDto userRequestDto) {
        User user = new User();
        user.setCreatedBy(jwtUtil.getCurrentLoginUser());
        user.setUpdatedBy(jwtUtil.getCurrentLoginUser());
        BeanUtils.copyProperties(userRequestDto, user);
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        return user;
    }

    public UserResponseDto userToUserResponseDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        BeanUtils.copyProperties(user, userResponseDto);
        userResponseDto.setUsertype(userTypeToUserTypeResponseDto(user.getUserType()));
        return userResponseDto;
    }

    public UserType userTypeRequestDtoToUserType(UserTypeRequestDto userTypeRequestDto) {
        UserType userType = new UserType();
        BeanUtils.copyProperties(userTypeRequestDto, userType);
        userType.setCreatedBy(jwtUtil.getCurrentLoginUser());
        userType.setUpdatedBy(jwtUtil.getCurrentLoginUser());
        return userType;
    }

    public UserTypeResponseDto userTypeToUserTypeResponseDto(UserType userType) {
        UserTypeResponseDto userTypeResponseDto = new UserTypeResponseDto();
        BeanUtils.copyProperties(userType, userTypeResponseDto);
        return userTypeResponseDto;
    }

    public VisitorPrerequest visitorPrerequestDtoToVisitorPrerequest(VisitorPrerequestDto visitorPrerequestDto) {
        VisitorPrerequest visitorPrerequest = new VisitorPrerequest();
        BeanUtils.copyProperties(visitorPrerequestDto, visitorPrerequest);
        visitorPrerequest.setCreatedBy(jwtUtil.getCurrentLoginUser());
        visitorPrerequest.setUpdatedBy(jwtUtil.getCurrentLoginUser());
        return visitorPrerequest;
    }

    public VisitorPrerequestResponseDto visitorPrerequestToVisitorPrerequestResponseDto(
            VisitorPrerequest visitorPrerequest) {
        VisitorPrerequestResponseDto visitorPrerequestResponseDto = new VisitorPrerequestResponseDto();
        BeanUtils.copyProperties(visitorPrerequest, visitorPrerequestResponseDto);
        visitorPrerequestResponseDto.setMeetingPurpose(visitorPrerequest.getMeetingPurpose().getPurposeFor());
        return visitorPrerequestResponseDto;
    }

    public DrivingLicence licenceDtoToLicence(DrivingLicenceReqDto drivingLicenceReqDto) {
        DrivingLicence drivingLicence = new DrivingLicence();
        BeanUtils.copyProperties(drivingLicenceReqDto, drivingLicence);
        drivingLicence.setExpDate(LocalDate.parse(drivingLicenceReqDto.getExpDate()));
        return drivingLicence;
    }

    public DrivingLicenceResDto licenceToLicenceDto(DrivingLicence drivingLicence) {
        DrivingLicenceResDto drivingLicenceResDto = new DrivingLicenceResDto();
        BeanUtils.copyProperties(drivingLicence, drivingLicenceResDto);
        if (drivingLicence.getFile() != null) {
            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/driving-licence/download/")
                    .path(drivingLicence.getFile().getGeneratedFileName())
                    .toUriString();
            drivingLicenceResDto.setFileUrl(fileUrl);
        }
        return drivingLicenceResDto;
    }

    public VehicleLicence vehicleLicenceDtoToVehicleLicence(VehicleLicenceReqDto vehicleLicenceReqDto) {
        VehicleLicence vehicleLicence=new VehicleLicence();
        BeanUtils.copyProperties(vehicleLicenceReqDto, vehicleLicence);
        vehicleLicence.setInsuranceDate(LocalDate.parse(vehicleLicenceReqDto.getInsuranceDate()));
        vehicleLicence.setPucDate(LocalDate.parse(vehicleLicenceReqDto.getPucDate()));
        vehicleLicence.setRegistrationDate(LocalDate.parse(vehicleLicenceReqDto.getRegistrationDate()));
        return vehicleLicence;
    }

    public VehicleLicenceResDto vehicleLicenceToVehicleLicenceDto(VehicleLicence vehicleLicence) {
        VehicleLicenceResDto vehicleLicenceDto = new VehicleLicenceResDto();
        BeanUtils.copyProperties(vehicleLicence, vehicleLicenceDto);

        if (vehicleLicence.getPucAttachment() != null) {
            String pucAttachmentUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/vehicle-licence/download/")
                    .path(vehicleLicence.getPucAttachment().getGeneratedFileName())
                    .toUriString();
            vehicleLicenceDto.setPucUrl(pucAttachmentUrl);
        }

        if (vehicleLicence.getInsuranceAttachment() != null) {
            String insuranceAttachmentUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/vehicle-licence/download/")
                    .path(vehicleLicence.getInsuranceAttachment().getGeneratedFileName())
                    .toUriString();
            vehicleLicenceDto.setInsuranceUrl(insuranceAttachmentUrl);
        }

        if (vehicleLicence.getRegistrationAttachment() != null) {
            String registrationAttachmentUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/vehicle-licence/download/")
                    .path(vehicleLicence.getRegistrationAttachment().getGeneratedFileName())
                    .toUriString();
            vehicleLicenceDto.setRegistrationUrl(registrationAttachmentUrl);
        }

        return vehicleLicenceDto;
    }

    public static Plant plantRequestDtoToPlant(PlantRequestDto plantRequestDto) {
        Plant plant = new Plant();
        BeanUtils.copyProperties(plantRequestDto, plant);
        plant.setCreatedAt(LocalDateTime.now());
        plant.setUpdatedAt(LocalDateTime.now());

        return plant;
    }

    public static Plant plantRequestDtoToPlant(Plant plant, PlantRequestDto plantRequestDto) {
        BeanUtils.copyProperties(plantRequestDto, plant);
        plant.setUpdatedAt(LocalDateTime.now());

        return plant;
    }

    public static PlantResponseDto plantToPlantResponseDto(Plant plant) {
        PlantResponseDto plantResponseDto = new PlantResponseDto();
        BeanUtils.copyProperties(plant, plantResponseDto);

        return plantResponseDto;
    }

    public static Unit unitRequestDtoToUnit(UnitRequestDto unitRequestDto) {
        Unit unit = new Unit();

        BeanUtils.copyProperties(unitRequestDto, unit);
        unit.setCreatedAt(LocalDateTime.now());
        unit.setUpdatedAt(LocalDateTime.now());
        return unit;
    }

    public static Unit unitRequestDtoToUnit(Unit unit, UnitRequestDto unitRequestDto) {
        BeanUtils.copyProperties(unitRequestDto, unit);
        unit.setUpdatedAt(LocalDateTime.now());

        return unit;
    }

    public static UnitResponseDto unitToUnitResponseDto(Unit unit) {
        UnitResponseDto unitResponseDto = new UnitResponseDto();
        BeanUtils.copyProperties(unit, unitResponseDto);
        return unitResponseDto;
    }

    public static Department departmentRequestDtoToDepartment(DepartmentRequestDto departmentRequestDto) {
        Department department = new Department();

        BeanUtils.copyProperties(departmentRequestDto, department);
        department.setCreatedAt(LocalDateTime.now());
        department.setUpdatedAt(LocalDateTime.now());
        return department;
    }

    public static Department departmentRequestDtoToDepartment(Department department,
            DepartmentRequestDto departmentRequestDto) {
        BeanUtils.copyProperties(departmentRequestDto, department);
        department.setUpdatedAt(LocalDateTime.now());
        return department;
    }

    public static DepartmentResponseDto departmentToDepartmentResponseDto(Department department) {
        DepartmentResponseDto departmentResponseDto = new DepartmentResponseDto();
        BeanUtils.copyProperties(department, departmentResponseDto);
        return departmentResponseDto;
    }

    public static Purpose purposeRequestDtoToPurpose(PurposeRequestDto purposeRequestDto) {
        Purpose purpose = new Purpose();
        purpose.setCreatedAt(LocalDateTime.now());
        purpose.setUpdatedAt(LocalDateTime.now());
        BeanUtils.copyProperties(purposeRequestDto, purpose);
        return purpose;
    }

    public static Purpose purposeRequestDtoToPurpose(Purpose purpose, PurposeRequestDto purposeRequestDto) {
        BeanUtils.copyProperties(purposeRequestDto, purpose);
        purpose.setUpdatedAt(LocalDateTime.now());
        return purpose;
    }

    public static PurposeResponseDto purposeToPurposeResponseDto(Purpose purpose) {
        PurposeResponseDto purposeResponseDto = new PurposeResponseDto();
        BeanUtils.copyProperties(purpose, purposeResponseDto);
        if (purpose.getDepartment() != null) {
            purposeResponseDto.setDepartment(DtoUtilities.departmentToDepartmentResponseDto(purpose.getDepartment()));
        }
        return purposeResponseDto;
    }

    public static Series seriesRequestDtoToSeries(SeriesRequestDto seriesRequestDto) {
        Series series = new Series();
        BeanUtils.copyProperties(seriesRequestDto, series);
        series.setCreatedAt(LocalDateTime.now());
        series.setUpdatedAt(LocalDateTime.now());
        return series;
    }

    public static Series seriesRequestDtoToSeries(Series series, SeriesRequestDto seriesRequestDto) {
        BeanUtils.copyProperties(seriesRequestDto, series);
        series.setUpdatedAt(LocalDateTime.now());
        return series;
    }

    public static SeriesResponseDto seriesToSeriesResponseDto(Series series) {
        SeriesResponseDto seriesResponseDto = new SeriesResponseDto();
        BeanUtils.copyProperties(series, seriesResponseDto);
        return seriesResponseDto;
    }

    public VehicleEntry vehicleEntryDtoToVehicleEntry(VehicleEntryReqDto vehicleEntryReqDto,
            DrivingLicence drivingLicence, VehicleLicence vehicleLicence, Plant plant, Purpose purpose) {
        VehicleEntry vehicleEntry = new VehicleEntry();
        BeanUtils.copyProperties(vehicleEntryReqDto, vehicleEntry);
        vehicleEntry.setDrivingLicence(drivingLicence);
        vehicleEntry.setVehicleLicence(vehicleLicence);
        vehicleEntry.setPlant(plant);
        vehicleEntry.setPurpose(purpose);
        return vehicleEntry;
    }

    public VehicleEntryResDto vehicleEntryToDto(VehicleEntry savedVehicleEntry) {
        VehicleEntryResDto vehicleEntryResDto = new VehicleEntryResDto();
        BeanUtils.copyProperties(savedVehicleEntry, vehicleEntryResDto);

        // Set DrivingLicenceResDto using the conversion method
        DrivingLicenceResDto drivingLicenceResDto = licenceToLicenceDto(savedVehicleEntry.getDrivingLicence());
        vehicleEntryResDto.setDrivingLicenceResDto(drivingLicenceResDto);

        // Set VehicleLicenceResDto using the conversion method
        VehicleLicenceResDto vehicleLicenceResDto = vehicleLicenceToVehicleLicenceDto(
                savedVehicleEntry.getVehicleLicence());
        vehicleEntryResDto.setVehicleLicenceResDto(vehicleLicenceResDto);

        // Set PlantResponseDto
        PlantResponseDto plantResponseDto = new PlantResponseDto();
        BeanUtils.copyProperties(savedVehicleEntry.getPlant(), plantResponseDto);
        vehicleEntryResDto.setPlantResponseDto(plantResponseDto);

        // Set PurposeResponseDto
        PurposeResponseDto purposeResponseDto = new PurposeResponseDto();
        BeanUtils.copyProperties(savedVehicleEntry.getPurpose(), purposeResponseDto);
        vehicleEntryResDto.setPurposeResponseDto(purposeResponseDto);

        return vehicleEntryResDto;
    }

    public static Visitor visitorRequestDtoToVisitor(VisitorRequestDto visitorRequestDto) {
        Visitor visitor = new Visitor();
        BeanUtils.copyProperties(visitorRequestDto, visitor);
        visitor.setCreatedAt(LocalDateTime.now());
        visitor.setUpdatedAt(LocalDateTime.now());
        return visitor;
    }

    public static Visitor visitorRequestDtoToVisitor(Visitor visitor, VisitorRequestDto visitorRequestDto) {
        BeanUtils.copyProperties(visitorRequestDto, visitor);
        visitor.setUpdatedAt(LocalDateTime.now());
        return visitor;
    }

    public static VisitorResponseDto visitorToVisitorResponseDto(Visitor visitor) {
        VisitorResponseDto visitorResponseDto = new VisitorResponseDto();
        BeanUtils.copyProperties(visitor, visitorResponseDto);
        if (visitor.getDepartment() != null) {
            visitorResponseDto.setDepartment(DtoUtilities.departmentToDepartmentResponseDto(visitor.getDepartment()));
        }
        if (visitor.getPurpose() != null) {
            visitorResponseDto.setPurpose(DtoUtilities.purposeToPurposeResponseDto(visitor.getPurpose()));
        }
        return visitorResponseDto;
    }
}
