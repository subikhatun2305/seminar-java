package com.dreamsol.controllers;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import com.dreamsol.dtos.requestDtos.UserRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dreamsol.dtos.requestDtos.PurposeRequestDto;
import com.dreamsol.dtos.responseDtos.PurposeResponseDto;
import com.dreamsol.services.PurposeService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/purposes")
@RequiredArgsConstructor
public class PurposeController {

    @Autowired
    private final PurposeService purposeService;

    @PostMapping("/create-purpose")
    public ResponseEntity<PurposeResponseDto> createPurpose(
            @Valid @RequestBody PurposeRequestDto purposeRequestDto) {
        return purposeService.createPurpose(purposeRequestDto);
    }

    @PutMapping("/update-purpose/{id}")
    public ResponseEntity<PurposeResponseDto> updatePurpose(@PathVariable Long id,
            @Valid @RequestBody PurposeRequestDto purposeRequestDto) {
        return purposeService.updatePurpose(id, purposeRequestDto);
    }

    @GetMapping("/get-purpose/{id}")
    public ResponseEntity<PurposeResponseDto> getPurposeById(@PathVariable Long id) {
        return purposeService.getPurposeById(id);
    }

    @GetMapping("/get-all-purposes")
    public ResponseEntity<?> getAllPurposes(
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) String purposeFor) {
        return purposeService.getPurposes(purposeFor, pageSize, page, sortBy, sortDirection, status, unitId);
    }

    @DeleteMapping("/delete-purpose/{id}")
    public ResponseEntity<?> deletePurpose(@PathVariable Long id) {
        return purposeService.deletePurpose(id);
    }

    @GetMapping(value = "/download-excel-data", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelData(@RequestParam(required = false) String status,
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) String purposeFor) {
        return purposeService.downloadPurposeDataAsExcel(status, unitId, purposeFor);
    }

    @GetMapping(value = "/download-excel-sample", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelSample() throws IOException {
        return purposeService.downloadPurposeExcelSample();
    }
    @PostMapping(value = "/upload-excel-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadExcelFile(@RequestParam("file") MultipartFile file)
    {
        return purposeService.uploadPurposeExcel(file, PurposeRequestDto.class);
    }
    @PostMapping("/save-bulk-data")
    public ResponseEntity<?> saveBulkData(@RequestBody @Valid List<PurposeRequestDto> purposeRequestDtos)
    {
        return purposeService.saveBulkData(purposeRequestDtos);
    }
    @GetMapping("/drop-down")
    public ResponseEntity<?> getPurposeDropDown() {
        return purposeService.getDropDown();
    }
}
