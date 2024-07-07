package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.SeriesRequestDto;
import com.dreamsol.dtos.responseDtos.DropDownDto;
import com.dreamsol.dtos.responseDtos.ExcelValidateDataResponseDto;
import com.dreamsol.dtos.responseDtos.SeriesResponseDto;
import com.dreamsol.dtos.responseDtos.ValidatedData;
import com.dreamsol.entites.Series;
import com.dreamsol.entites.Unit;
import com.dreamsol.repositories.SeriesRepository;
import com.dreamsol.repositories.UnitRepository;
import com.dreamsol.securities.JwtUtil;
import com.dreamsol.exceptions.ResourceNotFoundException;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeriesService {

    private final SeriesRepository seriesRepository;
    private final UnitRepository unitRepository;
    private final ExcelUtility excelUtility;
    private final JwtUtil jwtUtil;

    private int num;

    public ResponseEntity<SeriesResponseDto> createSeries(SeriesRequestDto seriesRequestDto) {
        num = 0;
        // Check if the unit exists
        unitRepository.findById(seriesRequestDto.getUnitId())
                .orElseThrow(() -> new RuntimeException(
                        "Unit with this id does not exist : " + seriesRequestDto.getUnitId()));
        Series series = DtoUtilities.seriesRequestDtoToSeries(seriesRequestDto);
        Optional<List<Series>> dbSeries = seriesRepository
                .findBySeriesForIgnoreCaseAndSubPrefixIgnoreCase(seriesRequestDto.getSeriesFor(),
                        seriesRequestDto.getSubPrefix());
        if (dbSeries.isPresent() && dbSeries.get().size() > 0) {
            dbSeries.get().stream()
                    .forEach(numSeries -> num = numSeries.getNumberSeries() > num ? numSeries.getNumberSeries() : num);
            series.setNumberSeries(num + 1);
            series.setPrefix(series.getSeriesFor().substring(0, 3).toUpperCase());
            series.setCreatedBy(jwtUtil.getCurrentLoginUser());
            series.setUpdatedBy(jwtUtil.getCurrentLoginUser());
            series.setStatus(true);
            Series savedSeries = seriesRepository.save(series);
            return ResponseEntity.ok().body(DtoUtilities.seriesToSeriesResponseDto(savedSeries));
        } else {
            series.setPrefix(series.getSeriesFor().substring(0, 3).toUpperCase());
            series.setUpdatedBy(jwtUtil.getCurrentLoginUser());
            series.setCreatedBy(jwtUtil.getCurrentLoginUser());
            series.setStatus(true);
            Series savedSeries = seriesRepository.save(series);
            return ResponseEntity.ok().body(DtoUtilities.seriesToSeriesResponseDto(savedSeries));
        }
    }

    public ResponseEntity<SeriesResponseDto> updateSeries(Long id, SeriesRequestDto seriesRequestDto) {
        num = 0;
        Series series = seriesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Series", "Id", id));
        // Check if the unit exists
        unitRepository.findById(seriesRequestDto.getUnitId())
                .orElseThrow(() -> new RuntimeException(
                        "Unit with this id does not exist : " + seriesRequestDto.getUnitId()));

        Optional<List<Series>> dbSeries = seriesRepository
                .findBySeriesForIgnoreCaseAndSubPrefixIgnoreCase(seriesRequestDto.getSeriesFor(),
                        series.getSubPrefix());
        if (dbSeries.isPresent()) {
            dbSeries.get().stream()
                    .forEach(numSeries -> num = numSeries.getNumberSeries() > num ? numSeries.getNumberSeries() : num);
            series.setNumberSeries(num + 1);
            series.setUpdatedBy(jwtUtil.getCurrentLoginUser());
            Series savedSeries = seriesRepository.save(series);
            return ResponseEntity.ok().body(DtoUtilities.seriesToSeriesResponseDto(savedSeries));
        } else {
            series.setPrefix(series.getSeriesFor().substring(0, 3).toUpperCase());
            series.setUpdatedBy(jwtUtil.getCurrentLoginUser());
            Series savedSeries = seriesRepository.save(series);
            return ResponseEntity.ok().body(DtoUtilities.seriesToSeriesResponseDto(savedSeries));
        }
    }

    public ResponseEntity<SeriesResponseDto> getSeriesById(Long id) {
        Series series = seriesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Series", "Id", id));
        return ResponseEntity.ok().body(DtoUtilities.seriesToSeriesResponseDto(series));
    }

    public ResponseEntity<?> getSeries(String purposeFor, int pageSize, int page, String sortBy, String sortDirection,
            String status,
            Long unitId) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("Asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by(direction, sortBy));
        Boolean statusBoolean = status != null ? Boolean.parseBoolean(status) : null;

        Page<Series> seriesPage = seriesRepository.findByStatusAndUnitIdAndseriesName(statusBoolean, unitId,
                purposeFor, pageRequest);

        Page<SeriesResponseDto> seriesResponseDtos = seriesPage.map(DtoUtilities::seriesToSeriesResponseDto);
        return ResponseEntity.ok(seriesResponseDtos);
    }

    public ResponseEntity<?> deleteSeries(Long id) {
        Series series = seriesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Series", "Id", id));
        if (!series.isStatus()) {
            throw new ResourceNotFoundException("Series", "Id", id);
        } else {
            series.setStatus(false);
            series.setUpdatedAt(LocalDateTime.now());
            series.setUpdatedBy(jwtUtil.getCurrentLoginUser());
            seriesRepository.save(series);
            return ResponseEntity.ok().body("Series Deleted Successfully");
        }
    }

    public ResponseEntity<?> downloadSeriesDataAsExcel(String status, Long unitId, String seriesFor) {
        try {
            Boolean statusBoolean = status != null ? Boolean.parseBoolean(status) : null;

            List<Series> seriesList = seriesRepository.findByStatusAndUnitIdAndseriesName(statusBoolean, unitId,
                    seriesFor);
            if (seriesList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No series available!");
            }

            List<SeriesResponseDto> seriesResDtoList = seriesList.stream()
                    .map(DtoUtilities::seriesToSeriesResponseDto)
                    .collect(Collectors.toList());

            String fileName = "series_excel_data.xlsx";
            String sheetName = fileName.substring(0, fileName.indexOf('.'));
            Resource resource = excelUtility.downloadDataAsExcel(seriesResDtoList, sheetName);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error! " + e);
        }
    }

    public ResponseEntity<?> downloadSeriesExcelSample() throws IOException {
        String fileName = "series_excel_sample.xlsx";
        String sheetName = fileName.substring(0, fileName.indexOf('.'));
        Resource resource = excelUtility.downloadExcelSample(SeriesRequestDto.class, sheetName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }
    public ResponseEntity<?> uploadPurposeExcel(MultipartFile file, Class<?> requestDtoClass)
    {
        try{
            if(excelUtility.isExcelFile(file))
            {
                ExcelValidateDataResponseDto validateDataResponse = excelUtility.validateExcelData(file,requestDtoClass);
                validateDataResponse = validateDataFromDB(validateDataResponse);
                validateDataResponse.setTotalValidData(validateDataResponse.getValidDataList().size());
                validateDataResponse.setTotalInvalidData(validateDataResponse.getInvalidDataList().size());
                if(validateDataResponse.getTotalData()==0){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No data available in excel sheet!");
                }
                return ResponseEntity.status(HttpStatus.OK).body(validateDataResponse);
            }else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect uploaded file type! supported [.xlsx or xls] type");
            }
        }catch(Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while validating excel data: "+e.getMessage());
        }
    }
    public ExcelValidateDataResponseDto validateDataFromDB(ExcelValidateDataResponseDto validateDataResponse){

        List<?> validList = validateDataResponse.getValidDataList();
        List<ValidatedData> invalidList = validateDataResponse.getInvalidDataList();
        List<SeriesRequestDto> seriesRequestDtos = new ArrayList<>();
        for(int i=0;i<validList.size();)
        {
            ValidatedData validatedData = (ValidatedData) validList.get(i);
            SeriesRequestDto seriesRequestDto = (SeriesRequestDto) validatedData.getData();
            ValidatedData checkedData = checkValidOrNot(seriesRequestDto);
            if(!checkedData.isStatus())
            {
                invalidList.add(checkedData);
                validList.remove(validatedData);
                continue;
            }
            seriesRequestDtos.add(seriesRequestDto);
            i++;
        }
        validateDataResponse.setValidDataList(seriesRequestDtos);
        return validateDataResponse;
    }
    public ValidatedData checkValidOrNot(SeriesRequestDto seriesRequestDto){
        StringBuilder message = new StringBuilder();
        boolean status = true;
        ValidatedData checkedData = new ValidatedData();
        Optional<Unit> unitOptional = unitRepository.findById(seriesRequestDto.getUnitId());
        if(unitOptional.isEmpty()){
            message.append("unit doesn't exist!, ");
            status = false;
        }
        checkedData.setData(seriesRequestDto);
        checkedData.setMessage(message.toString());
        checkedData.setStatus(status);
        return checkedData;
    }
    public ResponseEntity<?> saveBulkData(List<SeriesRequestDto> seriesRequestDtos) {
        try{
            String username = jwtUtil.getCurrentLoginUser();
            List<Series> seriesList = seriesRequestDtos.stream()
                    .map(seriesRequestDto -> {
                        Series series = DtoUtilities.seriesRequestDtoToSeries(seriesRequestDto);
                        Optional<List<Series>> dbSeries = seriesRepository
                                .findBySeriesForIgnoreCaseAndSubPrefixIgnoreCase(seriesRequestDto.getSeriesFor(),
                                        seriesRequestDto.getSubPrefix());
                        if (dbSeries.isPresent() && !dbSeries.get().isEmpty()) {
                            dbSeries.get()
                                    .forEach(numSeries -> num = numSeries.getNumberSeries() > num ? numSeries.getNumberSeries() : num);
                            series.setNumberSeries(num + 1);
                            series.setPrefix(series.getSeriesFor().substring(0, 3).toUpperCase());
                            series.setCreatedBy(jwtUtil.getCurrentLoginUser());
                            series.setUpdatedBy(jwtUtil.getCurrentLoginUser());
                        } else {
                            series.setPrefix(series.getSeriesFor().substring(0, 3).toUpperCase());
                            series.setUpdatedBy(jwtUtil.getCurrentLoginUser());
                            series.setCreatedBy(jwtUtil.getCurrentLoginUser());
                        }
                        series.setCreatedBy(username);
                        series.setUpdatedBy(username);
                        series.setStatus(seriesRequestDto.isStatus());
                        return series;
                    })
                    .collect(Collectors.toList());
            seriesRepository.saveAll(seriesList);
            return ResponseEntity.status(HttpStatus.CREATED).body("All data saved successfully");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while saving bulk data: "+e.getMessage());
        }
    }
    public ResponseEntity<?> getDropDown() {
        List<Series> series = seriesRepository.findAll();
        return ResponseEntity.ok(series.stream().map(this::seriesToDropDownRes).collect(Collectors.toList()));
    }

    private DropDownDto seriesToDropDownRes(Series series) {
        DropDownDto dto = new DropDownDto();
        dto.setId(series.getId());
        dto.setName(series.getSeriesFor());
        return dto;
    }
}
