package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.DepartmentRequestDto;
import com.dreamsol.dtos.responseDtos.DepartmentResponseDto;
import com.dreamsol.dtos.responseDtos.DropDownDto;
import com.dreamsol.dtos.responseDtos.ExcelValidateDataResponseDto;
import com.dreamsol.dtos.responseDtos.ValidatedData;
import com.dreamsol.entites.Department;
import com.dreamsol.entites.Unit;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.exceptions.ValidationException;
import com.dreamsol.repositories.DepartmentRepository;
import com.dreamsol.repositories.UnitRepository;
import com.dreamsol.securities.JwtUtil;
import com.dreamsol.utility.DtoUtilities;
import com.dreamsol.utility.ExcelUtility;
import com.dreamsol.utility.ValidatorUtilities;

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
import java.util.Set;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UnitRepository unitRepository;
    private final ExcelUtility excelUtility;
    private final JwtUtil jwtUtil;
    private final ValidatorUtilities validatioUtility;

    private static final Logger logger = LoggerFactory.getLogger(DepartmentService.class);

    public ResponseEntity<DepartmentResponseDto> createDepartment(DepartmentRequestDto departmentRequestDto) {
        // Check if department already exists
        Optional<Department> dbDepartment = departmentRepository
                .findByDepartmentCodeIgnoreCase(departmentRequestDto.getDepartmentCode());
        if (dbDepartment.isPresent()) {
            throw new RuntimeException(
                    "Department Already exists with  Code : " + departmentRequestDto.getDepartmentCode());
        }
        // Check if the unit exists
        unitRepository.findById(departmentRequestDto.getUnitId())
                .orElseThrow(() -> new RuntimeException(
                        "Unit with this id does not exist : " + departmentRequestDto.getUnitId()));
        Department department = DtoUtilities.departmentRequestDtoToDepartment(departmentRequestDto);
        department.setCreatedBy(jwtUtil.getCurrentLoginUser());
        department.setUpdatedBy(jwtUtil.getCurrentLoginUser());
        department.setStatus(true);
        Department savedDepartment = departmentRepository
                .save(department);
        return ResponseEntity.ok().body(DtoUtilities.departmentToDepartmentResponseDto(savedDepartment));

    }

    public ResponseEntity<List<DepartmentResponseDto>> createDepartments(
            List<DepartmentRequestDto> departmentRequestDtoList) {
        // Removing duplicates
        Set<DepartmentRequestDto> list = new HashSet<DepartmentRequestDto>(departmentRequestDtoList);
        // validationMessages
        Set<Set<String>> msg = new HashSet<Set<String>>();
        // Performing Validations on DepartmentRequestDtoList
        @SuppressWarnings("unused")
        Set<String> set = list.stream().map((departmentDto) -> {
            if (!validatioUtility.validateDto(departmentDto)) {
                msg.add(validatioUtility.validateDtoMessages(departmentDto));
            }
            return "Valid";
        }).collect(Collectors.toSet());

        if (!msg.isEmpty()) {
            throw new ValidationException(msg);
        }
        // Check if department already exists
        List<Department> validDepartments = list.stream().map((departmentRequestDto) -> {
            Optional<Department> department = departmentRepository
                    .findByDepartmentCodeIgnoreCase(departmentRequestDto.getDepartmentCode());
            if (department.isPresent()) {
                throw new RuntimeException(
                        "Department with this Code Already Exist : " + departmentRequestDto.getDepartmentCode());
            }
            // Checking if Unit Exist
            unitRepository.findById(departmentRequestDto.getUnitId()).orElseThrow(
                    () -> new RuntimeException("Unit with this Id not Found : " + departmentRequestDto.getUnitId()));
            // Converting and setting Required Values
            Department departmentUtil = DtoUtilities.departmentRequestDtoToDepartment(departmentRequestDto);
            departmentUtil.setCreatedBy(jwtUtil.getCurrentLoginUser());
            departmentUtil.setUpdatedBy(jwtUtil.getCurrentLoginUser());
            departmentUtil.setStatus(true);
            return departmentUtil;

        }).collect(Collectors.toList());
        List<Department> dbDepartments = departmentRepository.saveAll(validDepartments);
        return ResponseEntity.ok(dbDepartments.stream().map(DtoUtilities::departmentToDepartmentResponseDto).collect(Collectors.toList()));
    }

    public ResponseEntity<DepartmentResponseDto> updateDepartment(Long id, DepartmentRequestDto departmentRequestDto) {
        // Find the existing department
        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "Id", id));

        Optional<Department> departmentWithSameCode = departmentRepository
                .findByDepartmentCodeIgnoreCase(departmentRequestDto.getDepartmentCode());
        if (departmentWithSameCode.isPresent() && !departmentWithSameCode.get().getId().equals(id)) {
            throw new RuntimeException("Department code already exists");
        }

        // Check if the unit exists
        unitRepository.findById(departmentRequestDto.getUnitId())
                .orElseThrow(() -> new RuntimeException(
                        "Unit with this id does not exist : " + departmentRequestDto.getUnitId()));
        // Update department fields
        Department updatedDepartment = DtoUtilities.departmentRequestDtoToDepartment(existingDepartment,
                departmentRequestDto);
        updatedDepartment.setUpdatedBy(jwtUtil.getCurrentLoginUser());
        // Save the updated department
        updatedDepartment = departmentRepository.save(updatedDepartment);
        return ResponseEntity.ok().body(DtoUtilities.departmentToDepartmentResponseDto(updatedDepartment));
    }

    public ResponseEntity<DepartmentResponseDto> getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "Id", id));
        return ResponseEntity.ok().body(DtoUtilities.departmentToDepartmentResponseDto(department));
    }

    public ResponseEntity<?> deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "Id", id));

        if (department.isStatus()) {
            department.setStatus(false);
            department.setUpdatedAt(LocalDateTime.now());
            department.setUpdatedBy(jwtUtil.getCurrentLoginUser());

            departmentRepository.save(department);
            return ResponseEntity.ok().body("Department has been deleted");
        } else {
            throw new ResourceNotFoundException("Department", "Id", id);

        }
    }

    public ResponseEntity<?> getDepartments(int pageSize, int page, String sortBy, String sortDirection, String status,
            Long unitId) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("Asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by(direction, sortBy));
        Boolean statusBoolean = status != null ? Boolean.parseBoolean(status) : null;

        Page<Department> departmentsPage = departmentRepository.findByStatusAndUnitId(statusBoolean, unitId,
                pageRequest);

        Page<DepartmentResponseDto> departmentResponseDtos = departmentsPage
                .map(DtoUtilities::departmentToDepartmentResponseDto);
        return ResponseEntity.ok(departmentResponseDtos);
    }

    public ResponseEntity<?> downloadDepartmentDataAsExcel(String status, Long unitId) {
        try {
            Boolean statusBoolean = status != null ? Boolean.parseBoolean(status) : null;

            List<Department> departmentList = departmentRepository.findByStatusAndUnitId(statusBoolean, unitId);
            if (departmentList.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No departments available!");

            List<DepartmentResponseDto> departmentResDtoList = departmentList.stream()
                    .map(DtoUtilities::departmentToDepartmentResponseDto)
                    .collect(Collectors.toList());

            String fileName = "department_excel_data.xlsx";
            String sheetName = fileName.substring(0, fileName.indexOf('.'));
            Resource resource = excelUtility.downloadDataAsExcel(departmentResDtoList, sheetName);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error! " + e);
        }
    }

    public ResponseEntity<?> downloadDepartmentExcelSample() throws IOException {
        String fileName = "department_excel_sample.xlsx";
        String sheetName = fileName.substring(0, fileName.indexOf('.'));
        Resource resource = excelUtility.downloadExcelSample(DepartmentRequestDto.class, sheetName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }

    public ResponseEntity<?> getDropDown() {
        List<Department> departments = departmentRepository.findAll();
        return ResponseEntity.ok(departments.stream().map(this::departmentToDropDownRes).collect(Collectors.toList()));
    }

    private DropDownDto departmentToDropDownRes(Department department) {
        DropDownDto dto = new DropDownDto();
        dto.setId(department.getId());
        dto.setName(department.getDepartmentName());
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
        List<DepartmentRequestDto> departmentRequestDtoList = new ArrayList<>();
        for (int i = 0; i < validList.size(); ) {
            ValidatedData validatedData = (ValidatedData) validList.get(i);
            DepartmentRequestDto departmentRequestDto = (DepartmentRequestDto) validatedData.getData();
            ValidatedData checkedData = checkValidOrNot(departmentRequestDto);
            if(!checkedData.isStatus()){
                invalidList.add(checkedData);
                validList.remove(validatedData);
                continue;
            }
            departmentRequestDtoList.add(departmentRequestDto);
            i++;
        }
        validateDataResponse.setValidDataList(departmentRequestDtoList);
        return validateDataResponse;
    }

    public ValidatedData checkValidOrNot(DepartmentRequestDto departmentRequestDto){
        StringBuilder message = new StringBuilder();
        boolean status = true;
        ValidatedData checkedData = new ValidatedData();
        Optional<Department> DbDepartment = departmentRepository.findByDepartmentCodeIgnoreCase(departmentRequestDto.getDepartmentCode());
        if(DbDepartment.isPresent()){
            message.append("Department already exist!, ");
            status = false;
        }
        Optional<Unit> DbUnit=unitRepository.findById(departmentRequestDto.getUnitId());
        if(DbUnit.isEmpty())
        {
            message.append("Unit doesn't exist! ");
            status = false;
        }
        checkedData.setData(departmentRequestDto);
        checkedData.setMessage(message.toString());
        checkedData.setStatus(status);
        return checkedData;
    }

    public ResponseEntity<?> saveBulkData(List<DepartmentRequestDto> departmentRequestDtoList) {
        try {
            String currentUser = jwtUtil.getCurrentLoginUser();
            List<Department> departmentList = departmentRequestDtoList.stream()
                    .map(DtoUtilities::departmentRequestDtoToDepartment)
                    .peek(drivingLicence -> {
                        drivingLicence.setCreatedBy(currentUser);
                        drivingLicence.setUpdatedBy(currentUser);
                        drivingLicence.setStatus(true);
                    })
                    .collect(Collectors.toList());
            departmentRepository.saveAll(departmentList);
            logger.info("All data saved successfully!");
            return ResponseEntity.status(HttpStatus.CREATED).body(departmentList);
        } catch (Exception e) {
            logger.error("Error occurred while saving bulk data: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while saving bulk data: " + e.getMessage());
        }
    }
}
