package com.dreamsol.services.impl;

import com.dreamsol.dtos.requestDtos.DrivingLicenceReqDto;
import com.dreamsol.dtos.responseDtos.ApiResponse;
import com.dreamsol.dtos.responseDtos.DrivingLicenceResDto;
import com.dreamsol.dtos.responseDtos.ExcelValidateDataResponseDto;
import com.dreamsol.dtos.responseDtos.ValidatedData;
import com.dreamsol.entites.DrivingLicence;
import com.dreamsol.entites.DrivingLicenceAttachment;
import com.dreamsol.entites.Unit;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.DrivingLicenceRepo;
import com.dreamsol.repositories.DrivingLicenceAttachmentRepo;
import com.dreamsol.repositories.UnitRepository;
import com.dreamsol.securities.JwtUtil;
import com.dreamsol.utility.DtoUtilities;
import com.dreamsol.utility.ExcelUtility;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ByteArrayResource;
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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DrivingLicenceService {

    private final DrivingLicenceRepo drivingLicenceRepo;

    private final DtoUtilities dtoUtilities;

    private final FileService fileService;

    private final DrivingLicenceAttachmentRepo licenceAttachmentRepo;

    private final ExcelUtility excelUtility;

    private final JwtUtil jwtUtil;

    private final UnitRepository unitRepository;

    private static final Logger logger = LoggerFactory.getLogger(DrivingLicenceService.class);

    public ResponseEntity<?> addLicence(DrivingLicenceReqDto drivingLicenceReqDto, MultipartFile file, String path) {
        try {
            boolean licenceExists = drivingLicenceRepo.findByLicence(drivingLicenceReqDto.getLicence()).isPresent();
            boolean mobileExists = drivingLicenceRepo.findByDriverMobile(drivingLicenceReqDto.getDriverMobile()).isPresent();

            if (licenceExists && mobileExists) {
                return ResponseEntity.badRequest().body(new ApiResponse("Licence and Mobile number already exist!", false));
            } else if (licenceExists) {
                return ResponseEntity.badRequest().body(new ApiResponse("Licence already exists!", false));
            } else if (mobileExists) {
                return ResponseEntity.badRequest().body(new ApiResponse("Mobile number already exists!", false));
            }

            unitRepository.findById(drivingLicenceReqDto.getUnitId()).orElseThrow(() -> new NotFoundException("Unit not found with this unit id"));

            DrivingLicence drivingLicence = dtoUtilities.licenceDtoToLicence(drivingLicenceReqDto);
            String currentUser = jwtUtil.getCurrentLoginUser();
            drivingLicence.setCreatedBy(currentUser);
            drivingLicence.setUpdatedBy(currentUser);
            drivingLicence.setStatus(true);
            drivingLicence.setFile(uploadFile(file, path));

            return ResponseEntity.status(HttpStatus.CREATED).body(dtoUtilities.licenceToLicenceDto(drivingLicenceRepo.save(drivingLicence)));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to add Driving Licence.", false));
        }
    }


    public ResponseEntity<?> deleteLicence(Long licenceId) {
        DrivingLicence drivingLicence = drivingLicenceRepo.findById(licenceId).orElseThrow(() -> new ResourceNotFoundException("Driving Licence", "Id", licenceId));

        if (!drivingLicence.isStatus()) {
            throw new ResourceNotFoundException("Driving Licence", "Id", licenceId);
        }

        drivingLicence.setStatus(false);

        drivingLicenceRepo.save(drivingLicence);

        return ResponseEntity.ok(new ApiResponse("Licence deleted successfully!", true));
    }

    public ResponseEntity<?> updateLicence(DrivingLicenceReqDto drivingLicenceReqDto, Long licenceId, MultipartFile file, String path) {
        try {
            DrivingLicence drivingLicence = drivingLicenceRepo.findById(licenceId).orElseThrow(() -> new ResourceNotFoundException("Driving Licence", "Id", licenceId));

            unitRepository.findById(drivingLicenceReqDto.getUnitId()).orElseThrow(() -> new NotFoundException("Unit not found with this unit id"));

            BeanUtils.copyProperties(drivingLicenceReqDto, drivingLicence, "id", "licence");
            drivingLicence.setExpDate(LocalDate.parse(drivingLicenceReqDto.getExpDate()));
            DrivingLicenceAttachment existingAttachment = drivingLicence.getFile();

            if (existingAttachment != null) {
                uploadFile(file, path, existingAttachment, drivingLicenceReqDto);
            } else {
                DrivingLicenceAttachment newAttachment = uploadFile(file, path);
                drivingLicence.setFile(newAttachment);
            }
            drivingLicence.setUpdatedBy(jwtUtil.getCurrentLoginUser());
            DrivingLicence updatedDriving = drivingLicenceRepo.save(drivingLicence);

            return ResponseEntity.ok(dtoUtilities.licenceToLicenceDto(updatedDriving));
        } catch (NotFoundException | ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to update Driving Licence.", false));
        }
    }


    public ResponseEntity<?> fetchById(Long licenceId) {
        DrivingLicence drivingLicence = drivingLicenceRepo.findById(licenceId).orElseThrow(() -> new ResourceNotFoundException("Driving Licence", "Id", licenceId));
        return ResponseEntity.ok(dtoUtilities.licenceToLicenceDto(drivingLicence));
    }

    public ResponseEntity<Page<DrivingLicenceResDto>> fetchAllDrivers(String status, Long unitId, int page, int size, String sortBy, String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Boolean statusBoolean = (status != null) ? Boolean.parseBoolean(status) : null;

        Page<DrivingLicence> drivingLicences = drivingLicenceRepo.findByStatusAndUnitId(statusBoolean, unitId, pageable);

        Page<DrivingLicenceResDto> drivingLicenceResDtoPage = drivingLicences.map(dtoUtilities::licenceToLicenceDto);

        return ResponseEntity.ok(drivingLicenceResDtoPage);
    }

    public ResponseEntity<Resource> getFile(String fileName, String uploadDir) throws IOException {
        DrivingLicenceAttachment licenceAttachment = licenceAttachmentRepo.findByGeneratedFileName(fileName).orElseThrow(() -> {
            throw new ResourceNotFoundException();
        });
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(licenceAttachment.getFileType())).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + licenceAttachment.getOriginalFileName() + "\"").body(new ByteArrayResource(fileService.getFile(uploadDir, licenceAttachment.getGeneratedFileName())));
    }

    public ResponseEntity<?> downloadDriverDataAsExcel(String status, Long unitId) {
        try {
            Boolean statusBoolean = (status != null) ? Boolean.parseBoolean(status) : null;

            List<DrivingLicence> drivingLicenceList = drivingLicenceRepo.findByStatusAndUnitId(statusBoolean, unitId);

            if (drivingLicenceList.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No drivers available!");

            List<DrivingLicenceResDto> drivingLicenceResDtoList = drivingLicenceList.stream().map(dtoUtilities::licenceToLicenceDto).collect(Collectors.toList());

            String fileName = "driver_excel_data.xlsx";
            String sheetName = fileName.substring(0, fileName.indexOf('.'));
            Resource resource = excelUtility.downloadDataAsExcel(drivingLicenceResDtoList, sheetName);

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName).contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error!" + e);
        }
    }

    public ResponseEntity<?> downloadExcelSample() throws IOException {
        String fileName = "driver_excel_sample.xlsx";
        String sheetName = fileName.substring(0, fileName.indexOf('.'));
        Resource resource = excelUtility.downloadExcelSample(DrivingLicenceReqDto.class, sheetName);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName).contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(resource);
    }

    private DrivingLicenceAttachment uploadFile(MultipartFile file, String path) {
        try {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            if (fileName.contains("..")) {
                throw new Exception("Invalid file Name");
            }
            DrivingLicenceAttachment drivingLicenceAttachment = new DrivingLicenceAttachment();
            drivingLicenceAttachment.setOriginalFileName(fileName);
            drivingLicenceAttachment.setGeneratedFileName(fileService.fileSave(file, path));
            drivingLicenceAttachment.setFileType(file.getContentType());
            return drivingLicenceAttachment;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new DrivingLicenceAttachment();
    }

    private void uploadFile(MultipartFile file, String path, DrivingLicenceAttachment existingAttachment, DrivingLicenceReqDto drivingLicenceReqDto) {
        try {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            if (fileName.contains("..")) {
                throw new Exception("Invalid file Name");
            }

            String generatedFileName = fileService.fileSave(file, path);
            if (generatedFileName == null) {
                throw new Exception("Failed to save file");
            }

            if (existingAttachment == null) {
                existingAttachment = new DrivingLicenceAttachment();
            }

            existingAttachment.setOriginalFileName(fileName);
            existingAttachment.setGeneratedFileName(generatedFileName);
            existingAttachment.setFileType(file.getContentType());
        } catch (Exception exception) {
            exception.printStackTrace();
            new DrivingLicenceAttachment();
        }
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

    public ResponseEntity<?> saveBulkData(List<DrivingLicenceReqDto> drivingLicenceReqDtoList) {
        try {
            String currentUser = jwtUtil.getCurrentLoginUser();
            List<DrivingLicence> drivingLicenceList = drivingLicenceReqDtoList.stream().map(dtoUtilities::licenceDtoToLicence).peek(drivingLicence -> {
                drivingLicence.setCreatedBy(currentUser);
                drivingLicence.setUpdatedBy(currentUser);
                drivingLicence.setStatus(true);
            }).collect(Collectors.toList());
            drivingLicenceRepo.saveAll(drivingLicenceList);
            logger.info("All data saved successfully!");
            return ResponseEntity.status(HttpStatus.CREATED).body(drivingLicenceList);
        } catch (Exception e) {
            logger.error("Error occurred while saving bulk data: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while saving bulk data: " + e.getMessage());
        }
    }

    public ResponseEntity<?> findByDriverMobile(Long driverMobile) {
        try {
            DrivingLicence drivingLicence = drivingLicenceRepo.findByDriverMobile(driverMobile).orElseThrow(() -> new RuntimeException("Driver not found with mobile number: " + driverMobile));
            DrivingLicenceResDto response = dtoUtilities.licenceToLicenceDto(drivingLicence);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(ex.getMessage(), false));
        }
    }

    public ExcelValidateDataResponseDto validateDataFromDB(ExcelValidateDataResponseDto validateDataResponse) {
        List<?> validList = validateDataResponse.getValidDataList();
        List<ValidatedData> invalidList = validateDataResponse.getInvalidDataList();
        List<DrivingLicenceReqDto> drivingLicenceReqDtoList = new ArrayList<>();
        for (int i = 0; i < validList.size(); ) {
            ValidatedData validatedData = (ValidatedData) validList.get(i);
            DrivingLicenceReqDto drivingLicenceReqDto = (DrivingLicenceReqDto) validatedData.getData();
            ValidatedData checkedData = checkValidOrNot(drivingLicenceReqDto);
            if (!checkedData.isStatus()) {
                invalidList.add(checkedData);
                validList.remove(validatedData);
                continue;
            }
            drivingLicenceReqDtoList.add(drivingLicenceReqDto);
            i++;
        }
        validateDataResponse.setValidDataList(drivingLicenceReqDtoList);
        return validateDataResponse;
    }

    public ValidatedData checkValidOrNot(DrivingLicenceReqDto drivingLicenceReqDto) {
        StringBuilder message = new StringBuilder();
        boolean status = true;
        ValidatedData checkedData = new ValidatedData();
        Optional<DrivingLicence> DbDrivingLicence = drivingLicenceRepo.findByDriverMobile(drivingLicenceReqDto.getDriverMobile());
        if (DbDrivingLicence.isPresent()) {
            message.append("Driving Licence already exist!, ");
            status = false;
        }
        Optional<Unit> DbUnit = unitRepository.findById(drivingLicenceReqDto.getUnitId());
        if (DbUnit.isEmpty()) {
            message.append("Unit doesn't exist! ");
            status = false;
        }
        checkedData.setData(drivingLicenceReqDto);
        checkedData.setMessage(message.toString());
        checkedData.setStatus(status);
        return checkedData;
    }
}
