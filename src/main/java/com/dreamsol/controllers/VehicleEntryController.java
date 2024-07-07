package com.dreamsol.controllers;

import com.dreamsol.dtos.requestDtos.VehicleEntryReqDto;
import com.dreamsol.dtos.responseDtos.VehicleEntryCountDto;
import com.dreamsol.dtos.responseDtos.VehicleEntryResDto;
import com.dreamsol.services.impl.VehicleEntryService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vehicle-entry")
public class VehicleEntryController {

    private final VehicleEntryService vehicleEntryService;

    @PostMapping("/add")
    public ResponseEntity<?> createEntry(@Valid @RequestBody VehicleEntryReqDto vehicleEntryReqDto) {
        return vehicleEntryService.addEntry(vehicleEntryReqDto);
    }

    @DeleteMapping("/delete/{entryId}")
    public ResponseEntity<?> deleteEntry(@PathVariable Long entryId) {
        return vehicleEntryService.deleteEntry(entryId);
    }

    @PutMapping("/update/{entryId}")
    public ResponseEntity<?> updateEntry(@Valid @RequestBody VehicleEntryReqDto vehicleEntryReqDto, @PathVariable Long entryId) {
        return vehicleEntryService.updateEntry(vehicleEntryReqDto, entryId);
    }

    @GetMapping("/get/{entryId}")
    public ResponseEntity<?> getEntryById(@PathVariable Long entryId) {
        return vehicleEntryService.fetchById(entryId);
    }

    @GetMapping("/get-all")
    public ResponseEntity<Page<VehicleEntryResDto>> fetchAll(@RequestParam(required = false) String status, @RequestParam(required = false) Long unitId, @RequestParam(required = false) Long plantId, @RequestParam(required = false) Long purposeId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDirection, @Parameter(description = "yyyy-mm-dd") @RequestParam(required = false) String fromDate, @Parameter(description = "yyyy-mm-dd") @RequestParam(required = false) String toDate) {
        return vehicleEntryService.fetchAllEntries(status, unitId, plantId, purposeId, page, size, sortBy, sortDirection, fromDate, toDate);
    }

    @GetMapping("/download-entry-data")
    public ResponseEntity<?> downloadEntryDataAsExcel(@RequestParam(value = "status", required = false) String status, @RequestParam(value = "unitId", required = false) Long unitId, @RequestParam(value = "plantId", required = false) Long plantId, @RequestParam(value = "purposeId", required = false) Long purposeId, @Parameter(description = "yyyy-mm-dd") @RequestParam(value = "fromDate", required = false) String fromDate, @Parameter(description = "yyyy-mm-dd") @RequestParam(value = "toDate", required = false) String toDate) {
        return vehicleEntryService.downloadEntryDataAsExcel(status, unitId, plantId, purposeId, fromDate, toDate);
    }

    @GetMapping(value = "/download-excel-sample", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelSample() throws IOException {
        return vehicleEntryService.downloadExcelSample();
    }

    @GetMapping("/count")
    public ResponseEntity<VehicleEntryCountDto> getEntryCounts() {
        return vehicleEntryService.getEntryCounts();
    }
    
    @PostMapping("/entry/exit")
    public ResponseEntity<?> exitEntry(@RequestParam Long entryId) {
        return vehicleEntryService.exitEntry(entryId);
    }
}
