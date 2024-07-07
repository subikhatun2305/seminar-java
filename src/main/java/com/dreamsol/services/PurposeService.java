package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.PurposeRequestDto;
import com.dreamsol.dtos.responseDtos.DropDownDto;
import com.dreamsol.dtos.responseDtos.ExcelValidateDataResponseDto;
import com.dreamsol.dtos.responseDtos.PurposeResponseDto;
import com.dreamsol.dtos.responseDtos.ValidatedData;
import com.dreamsol.entites.Department;
import com.dreamsol.entites.Purpose;
import com.dreamsol.entites.Unit;
import com.dreamsol.entites.User;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.DepartmentRepository;
import com.dreamsol.repositories.PurposeRepository;
import com.dreamsol.repositories.UnitRepository;
import com.dreamsol.repositories.UserRepository;
import com.dreamsol.securities.JwtUtil;
import com.dreamsol.utility.DtoUtilities;
import com.dreamsol.utility.ExcelUtility;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

@Service
@RequiredArgsConstructor
public class PurposeService {

    private final PurposeRepository purposeRepository;
    private final ExcelUtility excelUtility;
    private final JwtUtil jwtUtil;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final DtoUtilities utility;

    public ResponseEntity<PurposeResponseDto> createPurpose(PurposeRequestDto purposeRequestDto) {
        // Check if the unit exists
        unitRepository.findById(purposeRequestDto.getUnitId())
                .orElseThrow(() -> new RuntimeException(
                        "Unit with this id does not exist : " + purposeRequestDto.getUnitId()));
        User user = null;
        Department department = null;
        LocalTime alTime = null;
        if (purposeRequestDto.isAlert()) {
            user = userRepository.findById(purposeRequestDto.getUserId()).orElseThrow(() -> new RuntimeException(
                    "User with this id does not exist : " + purposeRequestDto.getUserId()));

            department = departmentRepository.findById(purposeRequestDto.getUserId())
                    .orElseThrow(() -> new RuntimeException(
                            "Department with this id does not exist : " + purposeRequestDto.getDepartmentId()));

            try {
                alTime = LocalTime.parse(purposeRequestDto.getAlertTime());
            } catch (Exception e) {
                throw new RuntimeException("Select a Valid Time in this format HH:MM:SS");
            }
        }
        Purpose purpose = DtoUtilities.purposeRequestDtoToPurpose(purposeRequestDto);
        purpose.setCreatedBy(jwtUtil.getCurrentLoginUser());
        purpose.setUpdatedBy(jwtUtil.getCurrentLoginUser());
        purpose.setStatus(true);
        purpose.setUser(user);
        purpose.setDepartment(department);
        purpose.setAlertTime(alTime);
        Purpose savedPurpose = purposeRepository.save(purpose);
        PurposeResponseDto res = DtoUtilities.purposeToPurposeResponseDto(savedPurpose);
        if (savedPurpose.getUser() != null) {
            res.setUser(utility.userToUserResponseDto(savedPurpose.getUser()));
        }
        return ResponseEntity.ok().body(res);
    }

    public ResponseEntity<PurposeResponseDto> updatePurpose(Long id, PurposeRequestDto purposeRequestDto) {
        Purpose existingPurpose = purposeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purpose", "Id", id));
        // Check if the unit exists
        unitRepository.findById(purposeRequestDto.getUnitId())
                .orElseThrow(() -> new RuntimeException(
                        "Unit with this id does not exist : " + purposeRequestDto.getUnitId()));
        User user = null;
        Department department = null;
        LocalTime alTime = null;
        if (purposeRequestDto.isAlert()) {
            user = userRepository.findById(purposeRequestDto.getUserId()).orElseThrow(() -> new RuntimeException(
                    "User with this id does not exist : " + purposeRequestDto.getUserId()));

            department = departmentRepository.findById(purposeRequestDto.getUserId())
                    .orElseThrow(() -> new RuntimeException(
                            "Department with this id does not exist : " + purposeRequestDto.getDepartmentId()));

            try {
                alTime = LocalTime.parse(purposeRequestDto.getAlertTime());
            } catch (Exception e) {
                throw new RuntimeException("Select a Valid Time in this format HH:MM:SS");
            }

        }

        Purpose updatedPurpose = DtoUtilities.purposeRequestDtoToPurpose(existingPurpose, purposeRequestDto);
        updatedPurpose.setUpdatedBy(jwtUtil.getCurrentLoginUser());
        if (purposeRequestDto.isAlert()) {
            updatedPurpose.setUser(user);
            updatedPurpose.setDepartment(department);
            updatedPurpose.setAlertTime(alTime);
        }
        Purpose savedPurpose = purposeRepository.save(updatedPurpose);
        PurposeResponseDto res = DtoUtilities.purposeToPurposeResponseDto(updatedPurpose);
        if (savedPurpose.getUser() != null) {
            res.setUser(utility.userToUserResponseDto(savedPurpose.getUser()));
        }
        return ResponseEntity.ok().body(res);
    }

    public ResponseEntity<PurposeResponseDto> getPurposeById(Long id) {
        Purpose purpose = purposeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purpose", "Id", id));
        return ResponseEntity.ok().body(DtoUtilities.purposeToPurposeResponseDto(purpose));
    }

    public ResponseEntity<?> getPurposes(String purposeFor, int pageSize, int page, String sortBy, String sortDirection,
                                         String status,
                                         Long unitId) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("Asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by(direction, sortBy));
        Boolean statusBoolean = status != null ? Boolean.parseBoolean(status) : null;

        Page<Purpose> purposePage = purposeRepository.findByStatusAndUnitIdAndPurposeNameIgnoreCase(statusBoolean,
                unitId,
                purposeFor, pageRequest);

        Page<PurposeResponseDto> purposeResponseDtos = purposePage.map(DtoUtilities::purposeToPurposeResponseDto);
        return ResponseEntity.ok(purposeResponseDtos);
    }

    public ResponseEntity<?> deletePurpose(Long id) {
        Purpose purpose = purposeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purpose", "Id", id));
        if (purpose.isStatus()) {
            purpose.setStatus(false);
            purpose.setUpdatedAt(LocalDateTime.now());
            purpose.setUpdatedBy(jwtUtil.getCurrentLoginUser());

            purposeRepository.save(purpose);
            return ResponseEntity.ok().body("Purpose has been deleted");
        } else {
            throw new ResourceNotFoundException("Purpose", "Id", id);
        }
    }

    public ResponseEntity<?> downloadPurposeDataAsExcel(String status, Long unitId, String purposeName) {
        try {
            Boolean statusBoolean = status != null ? Boolean.parseBoolean(status) : null;

            List<Purpose> purposeList = purposeRepository.findByStatusAndUnitIdAndPurposeNameIgnoreCase(statusBoolean,
                    unitId, purposeName);
            if (purposeList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No purposes available!");
            }

            List<PurposeResponseDto> purposeResDtoList = purposeList.stream()
                    .map(DtoUtilities::purposeToPurposeResponseDto)
                    .collect(Collectors.toList());

            String fileName = "purpose_excel_data.xlsx";
            String sheetName = fileName.substring(0, fileName.indexOf('.'));
            Resource resource = excelUtility.downloadDataAsExcel(purposeResDtoList, sheetName);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error! " + e);
        }
    }

    public ResponseEntity<?> downloadPurposeExcelSample() throws IOException {
        String fileName = "purpose_excel_sample.xlsx";
        String sheetName = fileName.substring(0, fileName.indexOf('.'));
        Resource resource = excelUtility.downloadExcelSample(PurposeRequestDto.class, sheetName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }
    public ResponseEntity<?> uploadPurposeExcel(MultipartFile file, Class<?> requestDtoClass)
    {
        try{
            if(excelUtility.isExcelFile(file))
            {
                ExcelValidateDataResponseDto validateDataResponse = excelUtility.validateExcelData(file,requestDtoClass);
                validateDataResponse = validateDataFromDB(validateDataResponse);
                validateDataResponse.setTotalValidData(validateDataResponse.getValidDataList().size());
                validateDataResponse.setTotalInvalidData(validateDataResponse.getInvalidDataList().size());
                if(validateDataResponse.getTotalData()==0){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No data available in excel sheet!");
                }
                return ResponseEntity.status(HttpStatus.OK).body(validateDataResponse);
            }else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect uploaded file type! supported [.xlsx or xls] type");
            }
        }catch(Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while validating excel data: "+e.getMessage());
        }
    }
    public ExcelValidateDataResponseDto validateDataFromDB(ExcelValidateDataResponseDto validateDataResponse){

        List<?> validList = validateDataResponse.getValidDataList();
        List<ValidatedData> invalidList = validateDataResponse.getInvalidDataList();
        List<PurposeRequestDto> purposeRequestDtos = new ArrayList<>();
        for(int i=0;i<validList.size();)
        {
            ValidatedData validatedData = (ValidatedData) validList.get(i);
            PurposeRequestDto userRequestDto = (PurposeRequestDto) validatedData.getData();
            ValidatedData checkedData = checkValidOrNot(userRequestDto);
            if(!checkedData.isStatus())
            {
                invalidList.add(checkedData);
                validList.remove(validatedData);
                continue;
            }
            purposeRequestDtos.add(userRequestDto);
            i++;
        }
        validateDataResponse.setValidDataList(purposeRequestDtos);
        return validateDataResponse;
    }
    public ValidatedData checkValidOrNot(PurposeRequestDto purposeRequestDto){
        StringBuilder message = new StringBuilder();
        boolean status = true;
        ValidatedData checkedData = new ValidatedData();
        if(purposeRequestDto.isAlert()){
            Optional<User> user = userRepository.findById(purposeRequestDto.getUserId());
            if(user.isEmpty())
            {
                message.append("user with id: ").append(purposeRequestDto.getUserId()).append(" doesn't exist, ");
                status = false;
            }
            Optional<Department> department = departmentRepository.findById(purposeRequestDto.getDepartmentId());
            if(department.isEmpty()){
                message.append("department with id: ").append(purposeRequestDto.getDepartmentId()).append(" doesn't exist, ");
                status = false;
            }
            Optional<Unit> unit = unitRepository.findById(purposeRequestDto.getUnitId());
            if(unit.isEmpty()){
                message.append("unit with id: ").append(purposeRequestDto.getUnitId()).append(" doesn't exist!, ");
                status = false;
            }
            try {
                LocalTime.parse(purposeRequestDto.getAlertTime());
            }catch (DateTimeParseException e){
                message.append("Please enter a valid time with format HH:MM:SS");
                status = false;
            }
        }
        checkedData.setData(purposeRequestDto);
        checkedData.setMessage(message.toString());
        checkedData.setStatus(status);
        return checkedData;
    }
    public ResponseEntity<?> saveBulkData(List<PurposeRequestDto> purposeRequestDtos) {
        try{
            String username = jwtUtil.getCurrentLoginUser();
            List<Purpose> purposeList = purposeRequestDtos.stream()
                    .map(purposeReqDto -> {
                        Purpose purpose = DtoUtilities.purposeRequestDtoToPurpose(purposeReqDto);
                        Optional<User> user = userRepository.findById(purposeReqDto.getUserId());
                        Optional<Department> department = departmentRepository.findById(purposeReqDto.getDepartmentId());
                        purpose.setCreatedBy(username);
                        purpose.setUpdatedBy(username);
                        purpose.setStatus(purposeReqDto.isStatus());
                        purpose.setUser(user.orElse(null));
                        purpose.setDepartment(department.orElse(null));
                        purpose.setAlertTime(LocalTime.parse(purposeReqDto.getAlertTime()));
                        return purpose;
                    })
                    .collect(Collectors.toList());
            purposeRepository.saveAll(purposeList);
            return ResponseEntity.status(HttpStatus.CREATED).body("All data saved successfully");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while saving bulk data: "+e.getMessage());
        }
    }
    public ResponseEntity<?> getDropDown() {
        List<Purpose> purposes = purposeRepository.findAll();
        return ResponseEntity.ok(purposes.stream().map(this::purposeToDropDownRes).collect(Collectors.toList()));
    }

    private DropDownDto purposeToDropDownRes(Purpose purpose) {
        DropDownDto dto = new DropDownDto();
        dto.setId(purpose.getId());
        dto.setName(purpose.getPurposeFor());
        return dto;
    }
}