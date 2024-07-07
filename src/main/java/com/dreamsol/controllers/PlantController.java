package com.dreamsol.controllers;

import com.dreamsol.dtos.requestDtos.PlantRequestDto;
import com.dreamsol.dtos.requestDtos.PurposeRequestDto;
import com.dreamsol.dtos.responseDtos.PlantResponseDto;
import com.dreamsol.services.PlantService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/plants")
@RequiredArgsConstructor
public class PlantController {

    private final PlantService plantService;

    @PostMapping("/create-plant")
    public ResponseEntity<PlantResponseDto> createPlant(@Valid @RequestBody PlantRequestDto plantRequestDto) {
        return plantService.createPlant(plantRequestDto);
    }

    @PutMapping("update-plant/{id}")
    public ResponseEntity<PlantResponseDto> updatePlant(@PathVariable Long id,
            @Valid @RequestBody PlantRequestDto plantRequestDto) {
        return plantService.updatePlant(id, plantRequestDto);
    }

    @GetMapping("get-plant/{id}")
    public ResponseEntity<PlantResponseDto> getPlantById(@PathVariable Long id) {
        return plantService.getPlantById(id);
    }

    @GetMapping("get-all-plants")
    public ResponseEntity<?> getAllDepartments(
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) String plantName) {
        return plantService.getPlants(plantName, pageSize, page, sortBy, sortDirection, status, unitId);
    }

    @DeleteMapping("delete-plant/{id}")
    public ResponseEntity<?> deletePlant(@PathVariable Long id) {
        return plantService.deletePlant(id);

    }

    @GetMapping(value = "/download-excel-data", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelData(@RequestParam(required = false) String status,
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) String plantName) {
        return plantService.downloadPlantDataAsExcel(status, unitId, plantName);
    }

    @GetMapping(value = "/download-excel-sample", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelSample() throws IOException {
        return plantService.downloadPlantExcelSample();
    }
    @PostMapping(value = "/upload-excel-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadExcelFile(@RequestParam("file") MultipartFile file)
    {
        return plantService.uploadPurposeExcel(file, PlantRequestDto.class);
    }
    @PostMapping("/save-bulk-data")
    public ResponseEntity<?> saveBulkData(@RequestBody @Valid List<PlantRequestDto> plantRequestDtos)
    {
        return plantService.saveBulkData(plantRequestDtos);
    }
    @GetMapping("/drop-down")
    public ResponseEntity<?> getPlantDropDown() {
        return plantService.getDropDown();
    }
}
