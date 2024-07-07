package com.dreamsol.services.impl;

import com.dreamsol.dtos.requestDtos.VehicleLicenceReqDto;
import com.dreamsol.dtos.responseDtos.ApiResponse;
import com.dreamsol.dtos.responseDtos.ExcelValidateDataResponseDto;
import com.dreamsol.dtos.responseDtos.ValidatedData;
import com.dreamsol.dtos.responseDtos.VehicleLicenceResDto;
import com.dreamsol.entites.Unit;
import com.dreamsol.entites.VehicleLicence;
import com.dreamsol.entites.VehicleLicenceAttachment;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.UnitRepository;
import com.dreamsol.repositories.VehicleLicenceAttachmentRepo;
import com.dreamsol.repositories.VehicleLicenceRepo;
import com.dreamsol.securities.JwtUtil;
import com.dreamsol.utility.DtoUtilities;
import com.dreamsol.utility.ExcelUtility;
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
public class VehicleLicenceService {

    private final VehicleLicenceRepo vehicleLicenceRepo;

    private final DtoUtilities dtoUtilities;

    private final FileService fileService;

    private final VehicleLicenceAttachmentRepo vehicleLicenceAttachmentRepo;

    private final ExcelUtility excelUtility;

    private final JwtUtil jwtUtil;

    private final UnitRepository unitRepository;

    private static final Logger logger = LoggerFactory.getLogger(VehicleLicenceService.class);

    public ResponseEntity<?> addLicence(VehicleLicenceReqDto vehicleLicenceReqDto,
                                        MultipartFile pucFile,
                                        MultipartFile insuranceFile,
                                        MultipartFile registrationFile,
                                        String path) {

        if (vehicleLicenceRepo.existsByVehicleNumber(vehicleLicenceReqDto.getVehicleNumber())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("Vehicle number already exists.", false));
        }
        try {
            VehicleLicence vehicleLicence = dtoUtilities.vehicleLicenceDtoToVehicleLicence(vehicleLicenceReqDto);

            if (pucFile != null && !pucFile.isEmpty()) {
                VehicleLicenceAttachment pucAttachment = uploadFile(pucFile, path);
                vehicleLicence.setPucAttachment(pucAttachment);
            }

            if (insuranceFile != null && !insuranceFile.isEmpty()) {
                VehicleLicenceAttachment insuranceAttachment = uploadFile(insuranceFile, path);
                vehicleLicence.setInsuranceAttachment(insuranceAttachment);
            }

            if (registrationFile != null && !registrationFile.isEmpty()) {
                VehicleLicenceAttachment registrationAttachment = uploadFile(registrationFile, path);
                vehicleLicence.setRegistrationAttachment(registrationAttachment);
            }
            vehicleLicence.setCreatedBy(jwtUtil.getCurrentLoginUser());
            vehicleLicence.setUpdatedBy(jwtUtil.getCurrentLoginUser());
            vehicleLicence.setStatus(true);

            VehicleLicence savedVehicle = vehicleLicenceRepo.save(vehicleLicence);

            return ResponseEntity.status(HttpStatus.CREATED).body(dtoUtilities.vehicleLicenceToVehicleLicenceDto(savedVehicle));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to add Vehicle Licence.", false));
        }
    }


    public ResponseEntity<?> deleteLicence(Long licenceId) {
        VehicleLicence vehicleLicence = vehicleLicenceRepo.findById(licenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle Licence", "Id", licenceId));

        if (!vehicleLicence.isStatus()) {
            throw new ResourceNotFoundException("Vehicle Licence", "Id", licenceId);
        }

        vehicleLicence.setStatus(false);

        vehicleLicenceRepo.save(vehicleLicence);

        return ResponseEntity.ok(new ApiResponse("Licence deleted successfully!", true));
    }

    public ResponseEntity<?> updateLicence(VehicleLicenceReqDto vehicleLicenceReqDto, Long licenceId,
                                           MultipartFile pucFile, MultipartFile insuranceFile, MultipartFile registrationFile,
                                           String path) {
        VehicleLicence vehicleLicence = vehicleLicenceRepo.findById(licenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle Licence", "Id", licenceId));

        BeanUtils.copyProperties(vehicleLicenceReqDto, vehicleLicence, "id", "vehicleNumber");
        vehicleLicence.setInsuranceDate(LocalDate.parse(vehicleLicenceReqDto.getInsuranceDate()));
        vehicleLicence.setPucDate(LocalDate.parse(vehicleLicenceReqDto.getPucDate()));
        vehicleLicence.setRegistrationDate(LocalDate.parse(vehicleLicenceReqDto.getRegistrationDate()));

        try {
            if (pucFile != null && !pucFile.isEmpty()) {
                VehicleLicenceAttachment pucAttachment = vehicleLicence.getPucAttachment();
                if (pucAttachment != null) {
                    uploadFile(pucFile, path, pucAttachment, vehicleLicenceReqDto);
                } else {
                    pucAttachment = uploadFile(pucFile, path);
                    vehicleLicence.setPucAttachment(pucAttachment);
                }
            }

            if (insuranceFile != null && !insuranceFile.isEmpty()) {
                VehicleLicenceAttachment insuranceAttachment = vehicleLicence.getInsuranceAttachment();
                if (insuranceAttachment != null) {
                    uploadFile(insuranceFile, path, insuranceAttachment, vehicleLicenceReqDto);
                } else {
                    insuranceAttachment = uploadFile(insuranceFile, path);
                    vehicleLicence.setInsuranceAttachment(insuranceAttachment);
                }
            }

            if (registrationFile != null && !registrationFile.isEmpty()) {
                VehicleLicenceAttachment registrationAttachment = vehicleLicence.getRegistrationAttachment();
                if (registrationAttachment != null) {
                    uploadFile(registrationFile, path, registrationAttachment, vehicleLicenceReqDto);
                } else {
                    registrationAttachment = uploadFile(registrationFile, path);
                    vehicleLicence.setRegistrationAttachment(registrationAttachment);
                }
            }

            vehicleLicence.setUpdatedBy(jwtUtil.getCurrentLoginUser());

            VehicleLicence updatedVehicle = vehicleLicenceRepo.save(vehicleLicence);

            return ResponseEntity.ok(dtoUtilities.vehicleLicenceToVehicleLicenceDto(updatedVehicle));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to update Licence.", false));
        }
    }

    public ResponseEntity<?> fetchById(Long licenceId) {
        VehicleLicence vehicleLicence = vehicleLicenceRepo.findById(licenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle Licence", "Id", licenceId));
        return ResponseEntity.ok(dtoUtilities.vehicleLicenceToVehicleLicenceDto(vehicleLicence));
    }

    public ResponseEntity<Page<VehicleLicenceResDto>> fetchAllVehicles(
            String status,
            Long unitId,
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Boolean statusBoolean = (status != null) ? Boolean.parseBoolean(status) : null;

        Page<VehicleLicence> vehicleLicences = vehicleLicenceRepo.findByStatusAndUnitId(statusBoolean, unitId, pageable);

        Page<VehicleLicenceResDto> vehicleLicenceResDtoPage = vehicleLicences.map(dtoUtilities::vehicleLicenceToVehicleLicenceDto);

        return ResponseEntity.ok(vehicleLicenceResDtoPage);
    }


    public ResponseEntity<Resource> getFile(String fileName, String uploadDir) {
        try {
            VehicleLicenceAttachment vehicleLicenceAttachment = vehicleLicenceAttachmentRepo.findByGeneratedFileName(fileName)
                    .orElseThrow(ResourceNotFoundException::new);

            Resource resource = new ByteArrayResource(fileService.getFile(uploadDir, vehicleLicenceAttachment.getGeneratedFileName()));

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(vehicleLicenceAttachment.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + vehicleLicenceAttachment.getOriginalFileName() + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<?> downloadVehicleDataAsExcel(
            String status,
            Long unitId) {
        try {
            Boolean statusBoolean = (status != null) ? Boolean.parseBoolean(status) : null;

            List<VehicleLicence> vehicleLicenceList = vehicleLicenceRepo.findByStatusAndUnitId(statusBoolean, unitId);

            if (vehicleLicenceList.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No vehicles available!");

            List<VehicleLicenceResDto> vehicleLicenceResDtoList = vehicleLicenceList.stream()
                    .map(dtoUtilities::vehicleLicenceToVehicleLicenceDto)
                    .collect(Collectors.toList());

            String fileName = "vehicle_excel_data.xlsx";
            String sheetName = fileName.substring(0, fileName.indexOf('.'));
            Resource resource = excelUtility.downloadDataAsExcel(vehicleLicenceResDtoList, sheetName);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error!" + e);
        }
    }

    public ResponseEntity<?> downloadExcelSample() throws IOException {
        String fileName = "vehicle_excel_sample.xlsx";
        String sheetName = fileName.substring(0, fileName.indexOf('.'));
        Resource resource = excelUtility.downloadExcelSample(VehicleLicenceReqDto.class, sheetName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }

    private VehicleLicenceAttachment uploadFile(MultipartFile file, String path) {
        try {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            if (fileName.contains("..")) {
                throw new Exception("Invalid file Name");
            }
            VehicleLicenceAttachment vehicleLicenceAttachment = new VehicleLicenceAttachment();
            vehicleLicenceAttachment.setOriginalFileName(fileName);
            vehicleLicenceAttachment.setGeneratedFileName(fileService.fileSave(file, path));
            vehicleLicenceAttachment.setFileType(file.getContentType());
            return vehicleLicenceAttachment;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new VehicleLicenceAttachment();
    }

    private void uploadFile(MultipartFile file, String path, VehicleLicenceAttachment existingAttachment, VehicleLicenceReqDto vehicleLicenceReqDto) {
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
                existingAttachment = new VehicleLicenceAttachment();
            }

            existingAttachment.setOriginalFileName(fileName);
            existingAttachment.setGeneratedFileName(generatedFileName);
            existingAttachment.setFileType(file.getContentType());
        } catch (Exception exception) {
            exception.printStackTrace();
            new VehicleLicenceAttachment();
        }
    }

    public ResponseEntity<?> saveBulkData(List<VehicleLicenceReqDto> vehicleLicenceReqDtoList) {
        try {
            String currentUser = jwtUtil.getCurrentLoginUser();
            List<VehicleLicence> vehicleLicenceList = vehicleLicenceReqDtoList.stream()
                    .map(dtoUtilities::vehicleLicenceDtoToVehicleLicence)
                    .peek(vehicleLicence -> {
                        vehicleLicence.setCreatedBy(currentUser);
                        vehicleLicence.setUpdatedBy(currentUser);
                        vehicleLicence.setStatus(true);
                    })
                    .collect(Collectors.toList());
            vehicleLicenceRepo.saveAll(vehicleLicenceList);
            logger.info("All data saved successfully!");
            return ResponseEntity.status(HttpStatus.CREATED).body(vehicleLicenceList);
        } catch (Exception e) {
            logger.error("Error occurred while saving bulk data: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while saving bulk data: " + e.getMessage());
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

    public ExcelValidateDataResponseDto validateDataFromDB(ExcelValidateDataResponseDto validateDataResponse) {
        List<?> validList = validateDataResponse.getValidDataList();
        List<ValidatedData> invalidList = validateDataResponse.getInvalidDataList();
        List<VehicleLicenceReqDto> vehicleLicenceReqDtoList = new ArrayList<>();
        for (int i = 0; i < validList.size(); ) {
            ValidatedData validatedData = (ValidatedData) validList.get(i);
            VehicleLicenceReqDto vehicleLicenceReqDto = (VehicleLicenceReqDto) validatedData.getData();
            ValidatedData checkedData = checkValidOrNot(vehicleLicenceReqDto);
            if (!checkedData.isStatus()) {
                invalidList.add(checkedData);
                validList.remove(validatedData);
                continue;
            }
            vehicleLicenceReqDtoList.add(vehicleLicenceReqDto);
            i++;
        }
        validateDataResponse.setValidDataList(vehicleLicenceReqDtoList);
        return validateDataResponse;
    }

    public ValidatedData checkValidOrNot(VehicleLicenceReqDto vehicleLicenceReqDto) {
        StringBuilder message = new StringBuilder();
        boolean status = true;
        ValidatedData checkedData = new ValidatedData();
        Optional<VehicleLicence> DbVehicleLicence = vehicleLicenceRepo.findByVehicleNumber(vehicleLicenceReqDto.getVehicleNumber());
        if (DbVehicleLicence.isPresent()) {
            message.append("Vehicle already exist!, ");
            status = false;
        }
        Optional<Unit> DbUnit = unitRepository.findById(vehicleLicenceReqDto.getUnitId());
        if (DbUnit.isEmpty()) {
            message.append("Unit doesn't exist! ");
            status = false;
        }
        checkedData.setData(vehicleLicenceReqDto);
        checkedData.setMessage(message.toString());
        checkedData.setStatus(status);
        return checkedData;
    }
}
