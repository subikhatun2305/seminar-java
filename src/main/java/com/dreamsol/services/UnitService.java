package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.DrivingLicenceReqDto;
import com.dreamsol.dtos.requestDtos.UnitRequestDto;
import com.dreamsol.dtos.responseDtos.DropDownDto;
import com.dreamsol.dtos.responseDtos.ExcelValidateDataResponseDto;
import com.dreamsol.dtos.responseDtos.UnitResponseDto;
import com.dreamsol.dtos.responseDtos.ValidatedData;
import com.dreamsol.entites.DrivingLicence;
import com.dreamsol.entites.Unit;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.UnitRepository;
import com.dreamsol.securities.JwtUtil;
import com.dreamsol.services.impl.DrivingLicenceService;
import com.dreamsol.utility.DtoUtilities;
import com.dreamsol.utility.ExcelUtility;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnitService {

    private final UnitRepository unitRepository;
    private final ExcelUtility excelUtility;
    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(UnitService.class);

    public ResponseEntity<UnitResponseDto> createUnit(UnitRequestDto unitRequestDto) {
        Unit unit = DtoUtilities.unitRequestDtoToUnit(unitRequestDto);
        Optional<Unit> dbUnit = unitRepository.findByUnitNameIgnoreCaseOrUnitIp(unit.getUnitName(),
                unit.getUnitIp());
        if (dbUnit.isPresent()) {
            throw new RuntimeException("Unit with this details already exists UnitName: "
                    + unitRequestDto.getUnitName() + " ,UnitIp: " + unitRequestDto.getUnitIp());
        } else {
            unit.setCreatedBy(jwtUtil.getCurrentLoginUser());
            unit.setUpdatedBy(jwtUtil.getCurrentLoginUser());
            unit.setStatus(true);
            Unit savedUnit = unitRepository.save(unit);
            return ResponseEntity.ok().body(DtoUtilities.unitToUnitResponseDto(savedUnit));
        }
    }

    public ResponseEntity<UnitResponseDto> updateUnit(Long id, UnitRequestDto unitRequestDto) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit", "Id", id));
        Unit updatedUnit = DtoUtilities.unitRequestDtoToUnit(unit, unitRequestDto);
        updatedUnit.setUpdatedBy(jwtUtil.getCurrentLoginUser());
        updatedUnit = unitRepository.save(updatedUnit);
        return ResponseEntity.ok().body(DtoUtilities.unitToUnitResponseDto(updatedUnit));
    }

    public ResponseEntity<UnitResponseDto> getUnitById(Long id) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit", "Id", id));
        return ResponseEntity.ok().body(DtoUtilities.unitToUnitResponseDto(unit));
    }

    public ResponseEntity<Page<UnitResponseDto>> getUnits(int pageSize, int page, String sortBy, String sortDirection,
                                                          String status) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by(direction, sortBy));

        Boolean statusBoolean = status != null ? Boolean.parseBoolean(status) : null;

        Page<Unit> unitsPage = unitRepository.findByStatus(statusBoolean, pageRequest);

        Page<UnitResponseDto> unitResponseDtos = unitsPage.map(DtoUtilities::unitToUnitResponseDto);
        return ResponseEntity.ok(unitResponseDtos);
    }

    public ResponseEntity<?> deleteUnit(Long id) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit", "Id", id));
        if (unit.isStatus()) {
            unit.setStatus(false);
            unit.setUpdatedAt(LocalDateTime.now());
            unit.setUpdatedBy(jwtUtil.getCurrentLoginUser());
            unitRepository.save(unit);
            return ResponseEntity.ok("Unit Deleted Successfully");
        } else {
            throw new ResourceNotFoundException("Unit", "Id", id);
        }
    }

    public ResponseEntity<?> downloadDataAsExcel(String status) {
        Boolean statusBoolean = status != null ? Boolean.parseBoolean(status) : null;

        try {

            List<Unit> unitList = unitRepository.findByStatus(statusBoolean);
            if (unitList.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No units available!");
            List<UnitResponseDto> UnitResDtoList = unitList.stream().map(DtoUtilities::unitToUnitResponseDto)
                    .collect(Collectors.toList());
            String fileName = "unit_excel_data.xlsx";
            String sheetName = fileName.substring(0, fileName.indexOf('.'));
            Resource resource = excelUtility.downloadDataAsExcel(UnitResDtoList, sheetName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error!" + e);
        }
    }

    public ResponseEntity<?> downloadExcelSample() throws IOException {
        String fileName = "unit_excel_sample.xlsx";
        String sheetName = fileName.substring(0, fileName.indexOf('.'));
        Resource resource = excelUtility.downloadExcelSample(UnitRequestDto.class, sheetName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }

    public ResponseEntity<?> getDropDown() {
        List<Unit> units = unitRepository.findAll();
        return ResponseEntity.ok(units.stream().map(unit -> this.unitToDropDownRes(unit)).collect(Collectors.toList()));
    }

    private DropDownDto unitToDropDownRes(Unit unit) {
        DropDownDto dto = new DropDownDto();
        dto.setId(unit.getId());
        dto.setName(unit.getUnitName());
        return dto;
    }

    public ResponseEntity<?> uploadExcelFile(MultipartFile file, Class<?> currentClass) {
        try {
            if (excelUtility.isExcelFile(file)) {
                ExcelValidateDataResponseDto validateDataResponse = excelUtility.validateExcelData(file, currentClass);
                validateDataResponse = validateDataFromDB(validateDataResponse);
                validateDataResponse.setTotalValidData(validateDataResponse.getValidDataList().size());
                validateDataResponse.setTotalInvalidData(validateDataResponse.getInvalidDataList().size());
                if (validateDataResponse.getTotalData() == 0) {
                    logger.info("No data available in excel sheet!");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No data available in excel sheet!");
                }
                logger.info("Excel data validated successfully!");
                return ResponseEntity.status(HttpStatus.OK).body(validateDataResponse);
            } else {
                logger.info("Incorrect uploaded file type!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect uploaded file type! supported [.xlsx or xls] type");
            }
        } catch (Exception e) {
            logger.error("Error occurred while validating excel data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while validating excel data: " + e.getMessage());
        }
    }

    public ExcelValidateDataResponseDto validateDataFromDB(ExcelValidateDataResponseDto validateDataResponse) {
        List<?> validList = validateDataResponse.getValidDataList();
        List<ValidatedData> invalidList = validateDataResponse.getInvalidDataList();
        List<UnitRequestDto> unitRequestDtoList = new ArrayList<>();
        for (int i = 0; i < validList.size(); ) {
            ValidatedData validatedData = (ValidatedData) validList.get(i);
            UnitRequestDto unitRequestDto = (UnitRequestDto) validatedData.getData();
            boolean flag = isExistInDB(unitRequestDto);
            if (flag) {
                ValidatedData invalidData = new ValidatedData();
                invalidData.setData(unitRequestDto);
                invalidData.setMessage("Unit already exist!");
                invalidList.add(invalidData);
                validList.remove(validatedData);
                continue;
            }
            unitRequestDtoList.add(unitRequestDto);
            i++;
        }
        validateDataResponse.setValidDataList(unitRequestDtoList);
        return validateDataResponse;
    }

    public boolean isExistInDB(Object keyword) {
        UnitRequestDto unitRequestDto = (UnitRequestDto) keyword;
        Optional<Unit> DbUnit = unitRepository.findByUnitNameIgnoreCaseAndUnitIp(unitRequestDto.getUnitName(),unitRequestDto.getUnitIp());
        return DbUnit.isPresent();
    }

    public ResponseEntity<?> saveBulkData(List<UnitRequestDto> unitRequestDtoList) {
        try {
            String currentUser = jwtUtil.getCurrentLoginUser();
            List<Unit> unitList = unitRequestDtoList.stream()
                    .map(DtoUtilities::unitRequestDtoToUnit)
                    .peek(drivingLicence -> {
                        drivingLicence.setCreatedBy(currentUser);
                        drivingLicence.setUpdatedBy(currentUser);
                        drivingLicence.setStatus(true);
                    })
                    .collect(Collectors.toList());
            unitRepository.saveAll(unitList);
            logger.info("All data saved successfully!");
            return ResponseEntity.status(HttpStatus.CREATED).body(unitList);
        } catch (Exception e) {
            logger.error("Error occurred while saving bulk data: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while saving bulk data: " + e.getMessage());
        }
    }
}
