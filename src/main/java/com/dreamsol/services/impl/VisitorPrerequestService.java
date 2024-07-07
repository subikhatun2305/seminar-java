package com.dreamsol.services.impl;

import com.dreamsol.dtos.requestDtos.VisitorPrerequestDto;
import com.dreamsol.dtos.responseDtos.VisitorPrerequestResponseDto;
import com.dreamsol.entites.Purpose;
import com.dreamsol.entites.VisitorPrerequest;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.PurposeRepository;
import com.dreamsol.repositories.VisitorPrerequestRepository;
import com.dreamsol.utility.DtoUtilities;
import com.dreamsol.utility.ExcelUtility;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitorPrerequestService
{
    private final Logger logger = LoggerFactory.getLogger(VisitorPrerequestService.class);
    private final VisitorPrerequestRepository visitorRepository;
    private final PurposeRepository purposeRepository;
    private final DtoUtilities dtoUtilities;
    private final ExcelUtility excelUtility;
    private static final Pattern VALID_DATE_PATTERN = Pattern.compile("^(\\d{4})-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$");
    private static final Pattern VALID_OTP_PATTERN = Pattern.compile("^\\d{6}$");
    public ResponseEntity<?> create(VisitorPrerequestDto visitorPrerequestDto) {
        try {
            VisitorPrerequest visitorPrerequest = dtoUtilities.visitorPrerequestDtoToVisitorPrerequest(visitorPrerequestDto);
            Optional<Purpose> purposeOptional = purposeRepository.findById(visitorPrerequestDto.getMeetingPurposeId());
            purposeOptional.ifPresent(visitorPrerequest::setMeetingPurpose);
            visitorPrerequest.setOtp(generateOTP());
            try {
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                //DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                // Parsing Start Hours
                String startHoursString = visitorPrerequestDto.getStartHours();
                LocalTime startHours = (startHoursString == null || startHoursString.isEmpty()) ? LocalTime.MIN : LocalTime.parse(startHoursString, timeFormatter);
                visitorPrerequest.setStartHours(startHours);

                // Parsing End Hours
                String endHoursString = visitorPrerequestDto.getEndHours();
                LocalTime endHours = (endHoursString == null || endHoursString.isEmpty()) ? LocalTime.MAX : LocalTime.parse(endHoursString, timeFormatter);
                visitorPrerequest.setEndHours(endHours);

                // Parsing Meeting Schedule
                String meetingScheduleString = visitorPrerequestDto.getMeetingSchedule();
                if (meetingScheduleString == null || meetingScheduleString.isEmpty()) {
                    assert meetingScheduleString != null;
                    throw new DateTimeParseException("Meeting schedule date is missing", meetingScheduleString, 0);
                }
                //LocalDate meetingScheduleDate = LocalDate.parse(meetingScheduleString, dateFormatter);
                visitorPrerequest.setMeetingSchedule(LocalDateTime.parse(meetingScheduleString));

            } catch (DateTimeParseException e) {
                String errorMessage = "Please enter the correct format for schedule date [YYYY-MM-DD] or time [HH:MM:SS]: ";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            }
            String meetingStatus = visitorPrerequestDto.getMeetingStatus();
            visitorPrerequest.setMeetingStatus(meetingStatus==null || meetingStatus.isEmpty()? "pending": meetingStatus);
            visitorRepository.save(visitorPrerequest);
            logger.info("Pre-requested new visitor created successfully!");
            return ResponseEntity.status(HttpStatus.CREATED).body("Pre-requested new visitor created successfully!");
        }
        catch (Exception e){
            logger.error("Error occurred while creating new visitor, ",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while creating new visitor!");
        }
    }
    public ResponseEntity<?> update(VisitorPrerequestDto visitorPrerequestDto, Long id)
    {
        try{
            VisitorPrerequest visitorPrerequest = visitorRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("pre-requested visitor","id",id));
            BeanUtils.copyProperties(visitorPrerequestDto,visitorPrerequest);
            Purpose purpose = purposeRepository.findById(visitorPrerequestDto.getMeetingPurposeId()).orElseThrow(()->new ResourceNotFoundException("purpose","purposeFor",visitorPrerequestDto.getMeetingPurposeId()));
            visitorPrerequest.setMeetingPurpose(purpose);
            try {
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss"); // Using 24-hour format
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Ensuring the date format

                // Parsing Start Hours
                String startHoursString = visitorPrerequestDto.getStartHours();
                LocalTime startHours = (startHoursString == null || startHoursString.isEmpty()) ? LocalTime.MIN : LocalTime.parse(startHoursString, timeFormatter);
                visitorPrerequest.setStartHours(startHours);

                // Parsing End Hours
                String endHoursString = visitorPrerequestDto.getEndHours();
                LocalTime endHours = (endHoursString == null || endHoursString.isEmpty()) ? LocalTime.MAX : LocalTime.parse(endHoursString, timeFormatter);
                visitorPrerequest.setEndHours(endHours);

                // Parsing Meeting Schedule
                String meetingScheduleString = visitorPrerequestDto.getMeetingSchedule();
                if (meetingScheduleString == null || meetingScheduleString.isEmpty()) {
                    assert meetingScheduleString != null;
                    throw new DateTimeParseException("Meeting schedule date is missing", meetingScheduleString, 0);
                }
                LocalDate meetingScheduleDate = LocalDate.parse(meetingScheduleString, dateFormatter);
                visitorPrerequest.setMeetingSchedule(meetingScheduleDate.atTime(startHours));

            } catch (DateTimeParseException e) {
                String errorMessage = "Please enter the correct format for schedule date [YYYY-MM-DD] or time [HH:MM:SS]: ";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            }
            visitorPrerequest.setMeetingStatus(visitorPrerequestDto.getMeetingStatus());
            visitorRepository.save(visitorPrerequest);
            logger.info("Pre-requested visitor with id: "+id+" updated successfully!");
            return ResponseEntity.status(HttpStatus.OK).body("Pre-requested visitor with id: "+id+" updated successfully!");
        }catch(Exception e){
            logger.error("Error occurred while updating pre-requested visitor details, ",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while updating pre-requested visitor details,"+e.getMessage());
        }
    }

    public ResponseEntity<?> delete(Long id)
    {
        try {
            VisitorPrerequest visitorPrerequest = visitorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pre-requested Visitor", "id", id));
            visitorPrerequest.setStatus(false);
            visitorRepository.save(visitorPrerequest);
            logger.info("Visitor prerequest with id: "+id+" deleted successfully!");
            return ResponseEntity.status(HttpStatus.OK).body("Visitor pre-request with id: "+id+" deleted successfully!");
        }catch (Exception e){
            logger.error("Error occurred while deleting visitor pre-request: "+e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while deleting visitor pre-request!");
        }
    }

    public ResponseEntity<?> get(Long id)
    {
        try {
            VisitorPrerequest visitorPrerequest = visitorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pre-requested Visitor", "id", id));
            logger.info("Pre-requested visitor found with id: " + id);
            return ResponseEntity.status(HttpStatus.FOUND).body(visitorPrerequest);
        }catch (Exception e){
            logger.error("Error occurred while fetching pre-requested visitor with id: "+id,e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while fetching pre-requested visitor with id: "+id);
        }
    }

    public ResponseEntity<?> getStatusCount(String meetingStatus,Long meetingPurposeId,String fromDate,String toDate){
        try {
            LocalDateTime from = null;
            LocalDateTime to = null;
            if(fromDate != null && toDate != null)
            {
                if(isValidDate(fromDate) && isValidDate(toDate))
                {
                    from = LocalDate.parse(fromDate).atStartOfDay();
                    to = LocalDate.parse(toDate).atTime(LocalTime.MAX);
                    if(isInvalidDateRange(from, to)) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date range!");
                    }
                }else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("from-date and to-date must be valid date in YYYY-MM-DD format");
                }
            }
            List<VisitorPrerequest> prerequestList = visitorRepository.findByFilters(meetingStatus, meetingPurposeId, from, to);
            Map<String, Long> meetingStatusCount = new LinkedHashMap<>();
            long countPending = 0L;
            long countDone = 0L;
            long countRescheduled = 0L;
            long countCancelled = 0L;
            for (VisitorPrerequest visitorPrerequest : prerequestList) {
                switch (visitorPrerequest.getMeetingStatus().toLowerCase()) {
                    case "pending":
                        countPending++;
                        break;
                    case "done":
                        countDone++;
                        break;
                    case "reschedule":
                        countRescheduled++;
                        break;
                    case "cancel":
                        countCancelled++;
                }
            }
            meetingStatusCount.put("Pending", countPending);
            meetingStatusCount.put("Done", countDone);
            meetingStatusCount.put("Rescheduled", countRescheduled);
            meetingStatusCount.put("Cancelled", countCancelled);
            return ResponseEntity.status(HttpStatus.OK).body(meetingStatusCount);
        }catch (Exception e){
            logger.error("Error occurred while fetching count of meeting status",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while fetching count of meeting status: ");
        }
    }
    public ResponseEntity<?> getAll(Integer pageNumber, Integer pageSize, String sortBy, String sortDir, Long unitId, Boolean status, Long meetingPurposeId, String meetingStatus, String fromDate, String toDate) {
        try {
            LocalDateTime from = null;
            LocalDateTime to = null;
            if(fromDate != null && toDate != null)
            {
                if(isValidDate(fromDate) && isValidDate(toDate))
                {
                    from = LocalDate.parse(fromDate).atStartOfDay();
                    to = LocalDate.parse(toDate).atTime(LocalTime.MAX);
                    if(isInvalidDateRange(from, to)) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date range!");
                    }
                }else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("from-date and to-date must be valid date in YYYY-MM-DD format");
                }
            }
            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
            List<VisitorPrerequest> prerequestList = visitorRepository.findByFilters(unitId, status, meetingPurposeId, meetingStatus, from, to, pageable);
            logger.info("All visitors data fetched successfully!");
            return ResponseEntity.status(HttpStatus.OK).body(prerequestList);
        }
        catch (Exception e){
            logger.error("Error occurred while fetching all visitors: ",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while fetching all visitors: "+e.getMessage());
        }
    }

    public ResponseEntity<?> downloadDataAsExcel(Long unitId, Boolean status, Long meetingPurposeId, String meetingStatus, String fromDate, String toDate)
    {
        try{
            LocalDateTime from = null;
            LocalDateTime to = null;
            if(fromDate != null && toDate != null)
            {
                if(isValidDate(fromDate) && isValidDate(toDate))
                {
                    from = LocalDate.parse(fromDate).atStartOfDay();
                    to = LocalDate.parse(toDate).atTime(LocalTime.MAX);
                    if(isInvalidDateRange(from, to)) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date range!");
                    }
                }else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("from-date and to-date must be valid date in YYYY-MM-DD format");
                }
            }
            List<VisitorPrerequest> prerequestList = visitorRepository.findByFilters(unitId,status,meetingPurposeId,meetingStatus,from,to);
            if (prerequestList.isEmpty()) {
                logger.info("No visitors available!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No visitors available!");
            }
            List<VisitorPrerequestResponseDto> responseDtos = prerequestList.stream()
                    .map(dtoUtilities::visitorPrerequestToVisitorPrerequestResponseDto)
                    .collect(Collectors.toList());
            String fileName = "visitor_prerequest_excel_data.xlsx";
            String sheetName = fileName.substring(0,fileName.indexOf('.'));
            Resource resource = excelUtility.downloadDataAsExcel(responseDtos, sheetName);
            logger.info("pre-requested visitors data as excel file downloaded successfully!");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        }
        catch (Exception e){
            logger.error("Error occurred while downloading data as excel file: ",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while downloading data as excel file: "+e.getMessage());
        }
    }

    private String generateOTP(){
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
    private boolean isValidDate(String date){
        return VALID_DATE_PATTERN.matcher(date).matches();
    }
    private boolean isInvalidDateRange(LocalDateTime fromDate, LocalDateTime toDate){
        return toDate.isBefore(fromDate) || toDate.isAfter(LocalDateTime.now());
    }

    public ResponseEntity<?> getVisitorByOTP(String otp) {
        try{
            if(VALID_OTP_PATTERN.matcher(otp).matches()){
                VisitorPrerequest visitorPrerequest = visitorRepository.findByOtp(otp).orElseThrow(()->new ResourceNotFoundException("Visitor Prerequest","otp",0L));
                logger.info("Visitor found with otp "+otp);
                return ResponseEntity.status(HttpStatus.FOUND).body(visitorPrerequest);
            }
            logger.info("Invalid OTP!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP!");
        }catch(Exception e){
            logger.error("Error occurred while fetching visitors by otp",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
        }
    }
}
