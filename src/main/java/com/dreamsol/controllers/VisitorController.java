package com.dreamsol.controllers;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dreamsol.dtos.requestDtos.VisitorRequestDto;
import com.dreamsol.dtos.responseDtos.VisitorResponseDto;
import com.dreamsol.services.VisitorService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/visitors")
@RequiredArgsConstructor
public class VisitorController {

    private final VisitorService visitorService;

    @PostMapping("/create-visitor")
    public ResponseEntity<VisitorResponseDto> createVisitor(
            @Valid @RequestBody VisitorRequestDto visitorRequestDto) {
        return visitorService.createVisitor(visitorRequestDto);
    }

    @PutMapping("/update-visitor/{id}")
    public ResponseEntity<VisitorResponseDto> updateVisitor(@PathVariable Long id,
                                                            @Valid @RequestBody VisitorRequestDto visitorRequestDto) {
        return visitorService.updateVisitor(id, visitorRequestDto);
    }

    @GetMapping("/get-visitor/{id}")
    public ResponseEntity<VisitorResponseDto> getVisitorById(@PathVariable Long id) {
        return visitorService.getVisitorById(id);
    }

    @GetMapping("/get-all-visitors")
    public ResponseEntity<?> getAllVisitors(
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long purposeId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {
        return visitorService.getVisitors(pageSize, page, sortBy, sortDirection, status, unitId, employeeId, purposeId,
                departmentId, fromDate, toDate);
    }

    @GetMapping("/get-all-visitor-count")
    public ResponseEntity<?> getAllVisitorsCount(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long purposeId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {
        return visitorService.getVisitorsCount(status, unitId, employeeId, purposeId,
                departmentId, fromDate, toDate);
    }

    @DeleteMapping("/delete-visitor/{id}")
    public ResponseEntity<?> deleteVisitor(@PathVariable Long id) {
        return visitorService.deleteVisitor(id);
    }

    @GetMapping(value = "/download-excel-data", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelData(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long purposeId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) throws java.io.IOException {
        return visitorService.downloadVisitorDataAsExcel(status, unitId, employeeId, purposeId, departmentId, fromDate,
                toDate);
    }

    @PostMapping(value = "/search-by-phone")
    public ResponseEntity<?> searchByPhoneNumber(Long phoneNumber) {
        return visitorService.searchVisitor(phoneNumber);
    }

}
