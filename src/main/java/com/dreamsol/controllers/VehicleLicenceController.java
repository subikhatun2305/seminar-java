package com.dreamsol.controllers;

import com.dreamsol.dtos.requestDtos.VehicleLicenceReqDto;
import com.dreamsol.dtos.responseDtos.VehicleLicenceResDto;
import com.dreamsol.services.impl.VehicleLicenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vehicle-licence")
public class VehicleLicenceController {

    private final VehicleLicenceService vehicleLicenceService;

    @Value("${project.FileUpload}")
    private String uploadDir;

    @PostMapping(path = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createLicence(@Valid @RequestPart VehicleLicenceReqDto vehicleLicenceReqDto,
                                           @RequestParam("pucFile") MultipartFile pucFile,
                                           @RequestParam("insuranceFile") MultipartFile insuranceFile,
                                           @RequestParam("registrationFile") MultipartFile registrationFile) {
        return vehicleLicenceService.addLicence(vehicleLicenceReqDto,pucFile,insuranceFile,registrationFile, uploadDir);
    }

    @DeleteMapping("/delete/{licenceId}")
    public ResponseEntity<?> deleteLicence(@PathVariable Long licenceId) {
        return vehicleLicenceService.deleteLicence(licenceId);
    }

    @PutMapping(path="/update/{licenceId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
    public ResponseEntity<?> updateLicence(@Valid @RequestPart VehicleLicenceReqDto vehicleLicenceReqDto,
                                           @PathVariable Long licenceId,
                                           @RequestParam("pucFile") MultipartFile pucFile,
                                           @RequestParam("insuranceFile") MultipartFile insuranceFile,
                                           @RequestParam("registrationFile") MultipartFile registrationFile) {
        return vehicleLicenceService.updateLicence(vehicleLicenceReqDto, licenceId, pucFile,insuranceFile,registrationFile, uploadDir);
    }

    @GetMapping("/get/{licenceId}")
    public ResponseEntity<?> getLicenceById(@PathVariable Long licenceId) {
        return vehicleLicenceService.fetchById(licenceId);
    }

    @GetMapping("/get-all")
    public ResponseEntity<Page<VehicleLicenceResDto>> fetchAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long unitId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        return vehicleLicenceService.fetchAllVehicles(status,unitId, page, size, sortBy, sortDirection);
    }

    @GetMapping(path = "/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws IOException {
        return vehicleLicenceService.getFile(fileName, uploadDir);
    }

    @GetMapping("/download-vehicle-data")
    public ResponseEntity<?> downloadVehicleDataAsExcel(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "unitId", required = false) Long unitId) {
        return vehicleLicenceService.downloadVehicleDataAsExcel(status, unitId);
    }

    @GetMapping(value = "/download-excel-sample",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelSample() throws IOException {
        return vehicleLicenceService.downloadExcelSample();
    }

    @PostMapping(value = "/upload-excel-data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadExcelData(@RequestParam("file") MultipartFile file)
    {
        return vehicleLicenceService.uploadExcelFile(file, VehicleLicenceReqDto.class);
    }

    @PostMapping("/save-bulk-data")
    public ResponseEntity<?> saveBulkData(@RequestBody @Valid List<VehicleLicenceReqDto> vehicleLicenceReqDtoList)
    {
        return vehicleLicenceService.saveBulkData(vehicleLicenceReqDtoList);
    }

}
