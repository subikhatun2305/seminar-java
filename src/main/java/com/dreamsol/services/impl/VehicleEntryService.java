package com.dreamsol.services.impl;

import com.dreamsol.dtos.requestDtos.VehicleEntryReqDto;
import com.dreamsol.dtos.responseDtos.ApiResponse;
import com.dreamsol.dtos.responseDtos.VehicleEntryCountDto;
import com.dreamsol.dtos.responseDtos.VehicleEntryResDto;
import com.dreamsol.entites.*;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.*;
import com.dreamsol.securities.JwtUtil;
import com.dreamsol.utility.DtoUtilities;
import com.dreamsol.utility.ExcelUtility;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleEntryService {

    private final VehicleEntryRepository vehicleEntryRepository;

    private final DrivingLicenceRepo drivingLicenceRepo;

    private final VehicleLicenceRepo vehicleLicenceRepo;

    private final PlantRepository plantRepository;

    private final PurposeRepository purposeRepository;

    private final DtoUtilities dtoUtilities;

    private final ExcelUtility excelUtility;

    private final JwtUtil jwtUtil;

    public ResponseEntity<?> addEntry(VehicleEntryReqDto vehicleEntryReqDto) {
        try {
            Optional<DrivingLicence> optionalDrivingLicence = drivingLicenceRepo.findByDriverMobile(vehicleEntryReqDto.getDriverMobile());
            DrivingLicence drivingLicence = optionalDrivingLicence.orElseThrow(() -> new NotFoundException("Driver not found with this Mobile Number,Please add the driver first"));

            Optional<VehicleLicence> optionalVehicleLicence = vehicleLicenceRepo.findByVehicleNumber(vehicleEntryReqDto.getVehicleNumber());
            VehicleLicence vehicleLicence = optionalVehicleLicence.orElseThrow(() -> new NotFoundException("Vehicle not found with this vehicle number,Please add the vehicle First"));

            Optional<Plant> optionalPlant = plantRepository.findById(vehicleEntryReqDto.getPlantId());
            Plant plant = optionalPlant.orElseThrow(() -> new NotFoundException("Plant not found with this name,Please add the Plant first"));

            Optional<Purpose> optionalPurpose = purposeRepository.findById(vehicleEntryReqDto.getPurposeId());
            Purpose purpose = optionalPurpose.orElseThrow(() -> new NotFoundException("Purpose not found with this name,Please add the purpose first"));

            VehicleEntry vehicleEntry = dtoUtilities.vehicleEntryDtoToVehicleEntry(vehicleEntryReqDto, drivingLicence, vehicleLicence, plant, purpose);

            vehicleEntry.setCreatedBy(jwtUtil.getCurrentLoginUser());
            vehicleEntry.setUpdatedBy(jwtUtil.getCurrentLoginUser());
            vehicleEntry.setStatus(true);

            VehicleEntry savedVehicleEntry = vehicleEntryRepository.save(vehicleEntry);

            VehicleEntryResDto vehicleEntryResDto = dtoUtilities.vehicleEntryToDto(savedVehicleEntry);

            return ResponseEntity.status(HttpStatus.CREATED).body(vehicleEntryResDto);
        } catch (NotFoundException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to add Vehicle Entry.", false));
        }
    }

    public ResponseEntity<?> deleteEntry(Long entryId) {
        VehicleEntry vehicleEntry = vehicleEntryRepository.findById(entryId).orElseThrow(() -> new ResourceNotFoundException("Vehicle Entry", "Id", entryId));

        if (!vehicleEntry.isStatus()) {
            throw new ResourceNotFoundException("Vehicle Entry", "Id", entryId);
        }

        vehicleEntry.setStatus(false);

        vehicleEntryRepository.save(vehicleEntry);

        return ResponseEntity.ok(new ApiResponse("Entry deleted successfully!", true));
    }

    public ResponseEntity<?> updateEntry(VehicleEntryReqDto vehicleEntryReqDto, Long entryId) {

        VehicleEntry vehicleEntry = vehicleEntryRepository.findById(entryId).orElseThrow(() -> new ResourceNotFoundException("Vehicle Entry", "Id", entryId));
        try {
            Optional<DrivingLicence> optionalDrivingLicence = drivingLicenceRepo.findByDriverMobile(vehicleEntryReqDto.getDriverMobile());
            DrivingLicence drivingLicence = optionalDrivingLicence.orElseThrow(() -> new NotFoundException("Driver not found with this Mobile Number"));

            Optional<VehicleLicence> optionalVehicleLicence = vehicleLicenceRepo.findByVehicleNumber(vehicleEntryReqDto.getVehicleNumber());
            VehicleLicence vehicleLicence = optionalVehicleLicence.orElseThrow(() -> new NotFoundException("Vehicle not found with this vehicle number"));

            Optional<Plant> optionalPlant = plantRepository.findById(vehicleEntryReqDto.getPlantId());
            Plant plant = optionalPlant.orElseThrow(() -> new NotFoundException("Plant not found with this name"));

            Optional<Purpose> optionalPurpose = purposeRepository.findById(vehicleEntryReqDto.getPurposeId());
            Purpose purpose = optionalPurpose.orElseThrow(() -> new NotFoundException("Purpose not found with this name"));

            BeanUtils.copyProperties(vehicleEntryReqDto, vehicleEntry, "id");

            vehicleEntry.setDrivingLicence(drivingLicence);
            vehicleEntry.setVehicleLicence(vehicleLicence);
            vehicleEntry.setPlant(plant);
            vehicleEntry.setPurpose(purpose);
            vehicleEntry.setUpdatedBy(jwtUtil.getCurrentLoginUser());

            VehicleEntry updatedVehicleEntry = vehicleEntryRepository.save(vehicleEntry);


            VehicleEntryResDto vehicleEntryResDto = dtoUtilities.vehicleEntryToDto(updatedVehicleEntry);

            return ResponseEntity.ok(vehicleEntryResDto);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to update Entry.", false));
        }
    }

    public ResponseEntity<?> fetchById(Long entryId) {
        VehicleEntry vehicleEntry = vehicleEntryRepository.findById(entryId).orElseThrow(() -> new ResourceNotFoundException("Vehicle Entry", "Id", entryId));
        return ResponseEntity.ok(dtoUtilities.vehicleEntryToDto(vehicleEntry));
    }

    public ResponseEntity<Page<VehicleEntryResDto>> fetchAllEntries(String status, Long unitId, Long plantId, Long purposeId, int page, int size, String sortBy, String sortDirection, String fromDate, String toDate) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
        LocalDateTime date1 = null;
        LocalDateTime date2 = null;

        try {
            date1 = (fromDate != null && !fromDate.isEmpty()) ? LocalDate.parse(fromDate, formatter).atStartOfDay() : null;
        } catch (DateTimeParseException ignored) {
        }

        try {
            date2 = (toDate != null && !toDate.isEmpty()) ? LocalDate.parse(toDate, formatter).atTime(LocalTime.MAX) : null;
        } catch (DateTimeParseException ignored) {
        }

        Boolean statusBoolean = (status != null && !status.isEmpty()) ? Boolean.parseBoolean(status) : null;

        Page<VehicleEntry> vehicleEntries = vehicleEntryRepository.findByParameters(statusBoolean, unitId, plantId, purposeId, date1, date2, pageable);

        Page<VehicleEntryResDto> vehicleEntryResDtoPage = vehicleEntries.map(dtoUtilities::vehicleEntryToDto);

        return ResponseEntity.ok(vehicleEntryResDtoPage);
    }

    public ResponseEntity<?> downloadEntryDataAsExcel(String status, Long unitId, Long plantId, Long purposeId, String fromDate, String toDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
            LocalDateTime date1 = null;
            LocalDateTime date2 = null;

            try {
                date1 = (fromDate != null && !fromDate.isEmpty()) ? LocalDate.parse(fromDate, formatter).atStartOfDay() : null;
            } catch (DateTimeParseException ignored) {
            }

            try {
                date2 = (toDate != null && !toDate.isEmpty()) ? LocalDate.parse(toDate, formatter).atTime(LocalTime.MAX) : null;
            } catch (DateTimeParseException ignored) {
            }

            Boolean statusBoolean = (status != null) ? Boolean.parseBoolean(status) : null;

            List<VehicleEntry> vehicleEntryList = vehicleEntryRepository.findByParameters(statusBoolean, unitId, plantId, purposeId, date1, date2);

            if (vehicleEntryList.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No entries available!");

            List<VehicleEntryResDto> vehicleEntryResDtoList = vehicleEntryList.stream().map(dtoUtilities::vehicleEntryToDto).collect(Collectors.toList());

            String fileName = "entry_excel_data.xlsx";
            String sheetName = fileName.substring(0, fileName.indexOf('.'));
            Resource resource = excelUtility.downloadDataAsExcel(vehicleEntryResDtoList, sheetName);

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName).contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(resource);
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format! Please provide dates in yyyy-mm-dd format.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error!" + e);
        }
    }

    public ResponseEntity<?> downloadExcelSample() throws IOException {
        String fileName = "entry_excel_sample.xlsx";
        String sheetName = fileName.substring(0, fileName.indexOf('.'));
        Resource resource = excelUtility.downloadExcelSample(VehicleEntryReqDto.class, sheetName);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName).contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(resource);
    }

    public ResponseEntity<VehicleEntryCountDto> getEntryCounts() {
        Long totalEntries = vehicleEntryRepository.countTotalEntries();
        Long inEntries = vehicleEntryRepository.countInEntries();
        Long outEntries = vehicleEntryRepository.countOutEntries();

        VehicleEntryCountDto entryCountDto = new VehicleEntryCountDto(totalEntries, inEntries, outEntries);

        return ResponseEntity.ok(entryCountDto);
    }

    public ResponseEntity<?> exitEntry(Long entryId) {
        VehicleEntry vehicleEntry = vehicleEntryRepository.findById(entryId).orElseThrow(() -> new ResourceNotFoundException("Vehicle Entry", "Id", entryId));

        if (vehicleEntry.isStatus()) {
            vehicleEntry.setStatus(false);
            vehicleEntryRepository.save(vehicleEntry);
            return ResponseEntity.ok(new ApiResponse("Vehicle exit successful!", true));
        } else {
            throw new ResourceNotFoundException("Vehicle Entry", "Id", entryId);
        }
    }
}
