package com.dreamsol.services;

import org.springframework.stereotype.Service;

import com.dreamsol.dtos.requestDtos.VisitorRequestDto;
import com.dreamsol.dtos.responseDtos.VisitorCountDto;
import com.dreamsol.dtos.responseDtos.VisitorResponseDto;
import com.dreamsol.entites.Department;
import com.dreamsol.entites.Purpose;
import com.dreamsol.entites.User;
import com.dreamsol.entites.Visitor;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.VisitorRepository;
import com.dreamsol.repositories.DepartmentRepository;
import com.dreamsol.repositories.PurposeRepository;
import com.dreamsol.repositories.UnitRepository;
import com.dreamsol.repositories.UserRepository;
import com.dreamsol.securities.JwtUtil;
import com.dreamsol.utility.DtoUtilities;
import com.dreamsol.utility.ExcelUtility;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitorService {

    private final VisitorRepository visitorRepository;
    private final DepartmentRepository departmentRepository;
    private final PurposeRepository purposeRepository;
    private final UserRepository userRepository;
    private final UnitRepository unitRepository;
    private final ExcelUtility excelUtility;
    private final JwtUtil jwtUtil;
    private final DtoUtilities utilities;

    public ResponseEntity<VisitorResponseDto> createVisitor(VisitorRequestDto visitorRequestDto) {
        Visitor visitor = DtoUtilities.visitorRequestDtoToVisitor(visitorRequestDto);
        User user = userRepository.findById(visitorRequestDto.getUserId())
                .orElseThrow(() -> new RuntimeException(
                        "User not Found with id : " + visitorRequestDto.getUserId()));
        Department department = departmentRepository.findById(visitorRequestDto.getDepartmentId()).orElseThrow(
                () -> new RuntimeException("Department not Found with id : "
                        + visitorRequestDto.getDepartmentId()));
        Purpose purpose = purposeRepository.findById(visitorRequestDto.getPurposeId()).orElseThrow(
                () -> new RuntimeException(
                        "Purpose not Found with id : " + visitorRequestDto.getPurposeId()));
        // Check if the unit exists
        unitRepository.findById(visitorRequestDto.getUnitId())
                .orElseThrow(() -> new RuntimeException(
                        "Unit not found with id : " + visitorRequestDto.getUnitId()));

        try {
            LocalDateTime validfrom = LocalDateTime.parse(visitorRequestDto.getValidFrom());
            LocalDateTime validTill = LocalDateTime.parse(visitorRequestDto.getValidTill());
            visitor.setValidFrom(validfrom);
            visitor.setValidTill(validTill);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Please Select a Valid Date and Time, format is YYYY-MM-DDTHH-MM-SS");
        }
        visitor.setDepartment(department);
        visitor.setUser(user);
        visitor.setPurpose(purpose);
        visitor.setCreatedBy(jwtUtil.getCurrentLoginUser());
        visitor.setUpdatedBy(jwtUtil.getCurrentLoginUser());
        visitor.setStatus(true);
        // saving Visitor Entry into DB
        Visitor savedVisitor = visitorRepository.save(visitor);
        VisitorResponseDto res = DtoUtilities.visitorToVisitorResponseDto(savedVisitor);
        res.setUser(utilities.userToUserResponseDto(user));
        return ResponseEntity.ok().body(res);
    }

    public ResponseEntity<VisitorResponseDto> updateVisitor(Long id, VisitorRequestDto visitorRequestDto) {
        Visitor existingVisitor = visitorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visitor", "Id", id));
        User user = userRepository.findById(visitorRequestDto.getUserId())
                .orElseThrow(() -> new RuntimeException(
                        "User not Found with id : " + visitorRequestDto.getUserId()));
        Department department = departmentRepository.findById(visitorRequestDto.getDepartmentId()).orElseThrow(
                () -> new RuntimeException("Department not Found with id : "
                        + visitorRequestDto.getDepartmentId()));
        Purpose purpose = purposeRepository.findById(visitorRequestDto.getPurposeId()).orElseThrow(
                () -> new RuntimeException(
                        "Purpose not Found with id : " + visitorRequestDto.getPurposeId()));
        existingVisitor.setDepartment(department);
        existingVisitor.setUser(user);
        existingVisitor.setPurpose(purpose);
        Visitor updatedVisitor = DtoUtilities.visitorRequestDtoToVisitor(existingVisitor, visitorRequestDto);
        updatedVisitor.setUpdatedBy(jwtUtil.getCurrentLoginUser());
        // Saving Updated Visitor into DB
        updatedVisitor = visitorRepository.save(updatedVisitor);
        VisitorResponseDto res = DtoUtilities.visitorToVisitorResponseDto(updatedVisitor);
        res.setUser(utilities.userToUserResponseDto(user));
        return ResponseEntity.ok().body(res);
    }

    public ResponseEntity<VisitorResponseDto> getVisitorById(Long id) {
        Visitor visitor = visitorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visitor", "Id", id));
        VisitorResponseDto res = DtoUtilities.visitorToVisitorResponseDto(visitor);
        res.setUser(utilities.userToUserResponseDto(visitor.getUser()));
        return ResponseEntity.ok().body(res);
    }

    public ResponseEntity<?> deleteVisitor(Long id) {
        Visitor visitor = visitorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visitor", "Id", id));

        if (visitor.isStatus()) {
            visitor.setStatus(false);
            visitor.setUpdatedAt(LocalDateTime.now());
            visitor.setUpdatedBy(jwtUtil.getCurrentLoginUser());

            visitorRepository.save(visitor);
            return ResponseEntity.ok().body("Visitor has been deleted");
        } else {
            throw new ResourceNotFoundException("Visitor", "Id", id);
        }
    }

    public ResponseEntity<?> getVisitors(int pageSize, int page, String sortBy, String sortDirection, String status,
                                         Long unitId, Long employeeId, Long purposeId, Long departmentId, String fromDate,
                                         String toDate) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("Asc") ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by(direction, sortBy));

        Boolean statusBoolean = status != null ? Boolean.parseBoolean(status) : null;
        LocalDate dateFormatFromDate = null;
        LocalDate dateFormatToDate = null;
        try {
            dateFormatFromDate = fromDate != null ? LocalDate.parse(fromDate) : null;
            dateFormatToDate = toDate != null ? LocalDate.parse(toDate) : null;
        } catch (Exception e) {
            throw new RuntimeException("Please Select a Valid Date Format YYYY-MM-DD");
        }
        LocalDateTime startDate = dateFormatFromDate != null ? dateFormatFromDate.atStartOfDay() : null;
        LocalDateTime endDate = dateFormatToDate != null ? dateFormatToDate.atTime(java.time.LocalTime.MAX)
                : null;
        Page<Visitor> visitorsPage = visitorRepository
                .findByEmployeeIdAndPurposeIdAndDepartmentIdAndUnitIdAndStatus(
                        employeeId, purposeId, departmentId, unitId, statusBoolean, startDate,
                        endDate,
                        pageRequest);

        Page<VisitorResponseDto> visitorResponseDtos = visitorsPage
                .map((visitor) -> {
                    VisitorResponseDto dto = DtoUtilities.visitorToVisitorResponseDto(visitor);
                    dto.setUser(utilities.userToUserResponseDto(visitor.getUser()));
                    return dto;
                });
        return ResponseEntity.ok(visitorResponseDtos);
    }

    public ResponseEntity<?> downloadVisitorDataAsExcel(String status, Long unitId, Long employeeId, Long purposeId,
                                                        Long departmentId, String fromDate, String toDate) throws java.io.IOException {
        LocalDate dateFormatFromDate = null;
        LocalDate dateFormatToDate = null;
        try {
            dateFormatFromDate = fromDate != null ? LocalDate.parse(fromDate) : null;
            dateFormatToDate = toDate != null ? LocalDate.parse(toDate) : null;
        } catch (Exception e) {
            throw new RuntimeException("Please Select a Valid Date Format YYYY-MM-DD");
        }
        try {
            LocalDateTime startDate = dateFormatFromDate != null ? dateFormatFromDate.atStartOfDay() : null;
            LocalDateTime endDate = dateFormatToDate != null
                    ? dateFormatToDate.atTime(java.time.LocalTime.MAX)
                    : null;
            Boolean statusBoolean = status != null ? Boolean.parseBoolean(status) : null;

            List<Visitor> visitorList = visitorRepository
                    .findByEmployeeIdAndPurposeIdAndDepartmentIdAndUnitIdAndStatus(employeeId,
                            purposeId, departmentId, unitId, statusBoolean, startDate,
                            endDate);
            if (visitorList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No purposes available!");
            }

            List<VisitorResponseDto> visitorResDtoList = visitorList.stream()
                    .map((visitor) -> {
                        VisitorResponseDto dto = DtoUtilities
                                .visitorToVisitorResponseDto(visitor);
                        dto.setUser(utilities.userToUserResponseDto(visitor.getUser()));
                        return dto;
                    }).collect(Collectors.toList());

            String fileName = "visitor_excel_data.xlsx";
            String sheetName = fileName.substring(0, fileName.indexOf('.'));
            Resource resource = excelUtility.downloadDataAsExcel(visitorResDtoList, sheetName);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal Server Error! " + e);
        }
    }

    public ResponseEntity<?> getVisitorsCount(String status, Long unitId, Long employeeId, Long purposeId,
                                              Long departmentId, String fromDate,
                                              String toDate) {

        Boolean statusBoolean = status != null ? Boolean.parseBoolean(status) : null;
        LocalDate dateFormatFromDate = null;
        LocalDate dateFormatToDate = null;
        try {
            dateFormatFromDate = fromDate != null ? LocalDate.parse(fromDate) : null;
            dateFormatToDate = toDate != null ? LocalDate.parse(toDate) : null;
        } catch (Exception e) {
            throw new RuntimeException("Please Select a Valid Date Format YYYY-MM-DD");
        }
        LocalDateTime fromDateNew = dateFormatFromDate != null ? dateFormatFromDate.atStartOfDay()
                : null;
        LocalDateTime toDateNew = dateFormatToDate != null ? dateFormatToDate.atTime(java.time.LocalTime.MAX)
                : null;

        List<Visitor> list = visitorRepository.findByEmployeeIdAndPurposeIdAndDepartmentIdAndUnitIdAndStatus(
                employeeId, purposeId, departmentId, unitId, statusBoolean, fromDateNew, toDateNew);

        Long totalVisitor = 0L;
        Long totalVisitorApproval = 0L;
        Long totalVisitorIn = 0L;
        Long totalVisitorOut = 0L;
        Long totalVisitorNotApproval = 0L;

        for (Visitor visitor : list) {
            totalVisitor++;
            if (!visitor.isApprovalRequired()) {
                totalVisitorNotApproval++;
                if (visitor.isStatus()) {
                    totalVisitorIn++;
                } else {
                    totalVisitorOut++;
                }
            } else {
                totalVisitorApproval++;
            }
        }
        VisitorCountDto count = new VisitorCountDto();
        count.setTotalVisitor(totalVisitor);
        count.setVisitorIn(totalVisitorIn);
        count.setVisitorOut(totalVisitorOut);
        count.setVistorApprovalNotRequired(totalVisitorNotApproval);
        count.setVisitorApprovalRequired(totalVisitorApproval);
        return ResponseEntity.ok(count);
    }

    public ResponseEntity<?> searchVisitor(Long phoneNumber) {
        List<Visitor> visitors = visitorRepository.findByPhoneNumber(phoneNumber);
        return visitors.isEmpty() ? ResponseEntity.status(HttpStatus.NOT_FOUND).build()
                : ResponseEntity.ok(visitors.get(visitors.size() - 1));
    }
}