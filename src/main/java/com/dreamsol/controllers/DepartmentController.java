package com.dreamsol.controllers;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import com.dreamsol.dtos.requestDtos.UnitRequestDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dreamsol.dtos.requestDtos.DepartmentRequestDto;
import com.dreamsol.dtos.responseDtos.DepartmentResponseDto;
import com.dreamsol.services.DepartmentService;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping("/create-department")
    public ResponseEntity<DepartmentResponseDto> createDepartment(
            @Valid @RequestBody DepartmentRequestDto departmentRequestDto) {
        return departmentService.createDepartment(departmentRequestDto);
    }
    @PostMapping("/create-departments")
    public ResponseEntity<?> createDepartments(
            @Valid @RequestBody List<DepartmentRequestDto> departmentRequestDtoList) {
        return departmentService.createDepartments(departmentRequestDtoList);
    }
    @PutMapping("/update-department/{id}")
    public ResponseEntity<DepartmentResponseDto> updateDepartment(@PathVariable Long id,
            @Valid @RequestBody DepartmentRequestDto departmentRequestDto) {
        return departmentService.updateDepartment(id, departmentRequestDto);
    }

    @GetMapping("/get-department/{id}")
    public ResponseEntity<DepartmentResponseDto> getDepartmentById(@PathVariable Long id) {
        return departmentService.getDepartmentById(id);
    }

    @GetMapping("/get-all-departments")
    public ResponseEntity<?> getAllDepartments(
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long unitId) {
        return departmentService.getDepartments(pageSize, page, sortBy, sortDirection, status, unitId);
    }

    @DeleteMapping("/delete-department/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {
        return departmentService.deleteDepartment(id);
    }

    @GetMapping(value = "/download-excel-data", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelData(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long unitId) {
        return departmentService.downloadDepartmentDataAsExcel(status, unitId);
    }

    @GetMapping(value = "/download-excel-sample", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelSample() throws IOException {
        return departmentService.downloadDepartmentExcelSample();
    }

    @GetMapping("/drop-down")
    public ResponseEntity<?> getDepartmentsDropDown() {
        return departmentService.getDropDown();
    }

    @PostMapping(value = "/upload-excel-data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadExcelData(@RequestParam("file") MultipartFile file)
    {
        return departmentService.uploadExcelFile(file,DepartmentRequestDto.class);
    }

    @PostMapping("/save-bulk-data")
    public ResponseEntity<?> saveBulkData(@RequestBody @Valid List<DepartmentRequestDto> departmentRequestDtoList)
    {
        return departmentService.saveBulkData(departmentRequestDtoList);
    }
}
