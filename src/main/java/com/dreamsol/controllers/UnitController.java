package com.dreamsol.controllers;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import com.dreamsol.dtos.requestDtos.DrivingLicenceReqDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dreamsol.dtos.requestDtos.UnitRequestDto;
import com.dreamsol.dtos.responseDtos.UnitResponseDto;
import com.dreamsol.services.UnitService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/units")
public class UnitController {

    @Autowired
    private UnitService unitService;

    @PostMapping("/create-unit")
    public ResponseEntity<UnitResponseDto> createUnit(
            @Valid @RequestBody UnitRequestDto unitRequestDto) {
        return unitService.createUnit(unitRequestDto);

    }

    @PutMapping("/update-unit/{id}")
    public ResponseEntity<UnitResponseDto> updateUnit(@PathVariable Long id,
                                                      @Valid @RequestBody UnitRequestDto unitRequestDto) {
        return unitService.updateUnit(id, unitRequestDto);

    }

    @GetMapping("/get-unit/{id}")
    public ResponseEntity<UnitResponseDto> getUnitById(@PathVariable Long id) {
        return unitService.getUnitById(id);

    }

    @GetMapping("/get-all-units")
    public ResponseEntity<?> getAllUnits(
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) String status) {
        return unitService.getUnits(pageSize, page, sortBy, sortDirection, status);
    }

    @DeleteMapping("/delete-unit/{id}")
    public ResponseEntity<?> deleteUnit(@PathVariable Long id) {
        return unitService.deleteUnit(id);

    }

    @GetMapping(value = "/download-excel-data", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelData(@RequestParam(required = false) String status) {
        return unitService.downloadDataAsExcel(status);
    }

    @GetMapping(value = "/download-excel-sample", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelSample() throws IOException {
        return unitService.downloadExcelSample();
    }

    @GetMapping("/drop-down")
    public ResponseEntity<?> getUnitsDropDown() {
        return unitService.getDropDown();
    }

    @PostMapping(value = "/upload-excel-data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadExcelData(@RequestParam("file") MultipartFile file)
    {
        return unitService.uploadExcelFile(file,UnitRequestDto.class);
    }

    @PostMapping("/save-bulk-data")
    public ResponseEntity<?> saveBulkData(@RequestBody @Valid List<UnitRequestDto> unitRequestDtoList)
    {
        return unitService.saveBulkData(unitRequestDtoList);
    }
}
