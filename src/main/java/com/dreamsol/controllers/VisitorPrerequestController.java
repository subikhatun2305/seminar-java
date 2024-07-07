package com.dreamsol.controllers;

import com.dreamsol.dtos.requestDtos.VisitorPrerequestDto;
import com.dreamsol.services.impl.VisitorPrerequestService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/visitors-prerequest")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class VisitorPrerequestController
{
    private final VisitorPrerequestService visitorPrerequestService;

    @PostMapping("/create")
    public ResponseEntity<?> createVisitorPrerequest(@RequestBody @Valid VisitorPrerequestDto visitor)
    {
        return visitorPrerequestService.create(visitor);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateVisitorPrerequest(@RequestBody @Valid VisitorPrerequestDto visitor, @PathVariable Long id)
    {
        return visitorPrerequestService.update(visitor,id);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteVisitorPrerequest(Long id)
    {
        return visitorPrerequestService.delete(id);
    }
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getVisitorPrerequestById(Long id)
    {
        return visitorPrerequestService.get(id);
    }

    @GetMapping("/get-by-otp/{otp}")
    public ResponseEntity<?> getVisitorPrerequestByOTP(@PathVariable String otp){
        return visitorPrerequestService.getVisitorByOTP(otp);
    }

    @GetMapping("/get-status-count")
    public ResponseEntity<?> getStatusCount(
            @RequestParam(value = "meetingStatus", required = false) String meetingStatus,
            @RequestParam(value = "meetingPurpose", required = false) Long meetingPurposeId,
            @RequestParam(value = "fromDate", required = false)String fromDate,
            @RequestParam(value = "toDate", required = false)String toDate
    ){
        return visitorPrerequestService.getStatusCount(meetingStatus,meetingPurposeId,fromDate,toDate);
    }
    @GetMapping("/get-all")
    public ResponseEntity<?> getVisitors(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir,
            @RequestParam(value = "unitId", defaultValue = "1", required = false) Long unitId,
            @RequestParam(value = "status", required = false) Boolean status,
            @RequestParam(value = "meetingPurposeId", required = false) Long meetingPurposeId,
            @RequestParam(value = "meetingStatus",required = false) String meetingStatus,
            @RequestParam(value = "fromDate",required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate
    ){
        return visitorPrerequestService.getAll(pageNumber,pageSize,sortBy,sortDir,unitId,status,meetingPurposeId,meetingStatus,fromDate,toDate);
    }

    @GetMapping(value = "/download-excel-data", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadVisitorsDataAsExcel(
            @RequestParam(value = "unitId", defaultValue = "1", required = false) Long unitId,
            @RequestParam(value = "status", required = false) Boolean status,
            @RequestParam(value = "meetingPurpose", required = false) Long meetingPurposeId,
            @RequestParam(value = "meetingStatus",required = false) String meetingStatus,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate
    ){
        return visitorPrerequestService.downloadDataAsExcel(unitId,status,meetingPurposeId,meetingStatus,fromDate,toDate);
    }
}