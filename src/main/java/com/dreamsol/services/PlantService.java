package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.PlantRequestDto;
import com.dreamsol.dtos.responseDtos.DropDownDto;
import com.dreamsol.dtos.responseDtos.ExcelValidateDataResponseDto;
import com.dreamsol.dtos.responseDtos.PlantResponseDto;
import com.dreamsol.dtos.responseDtos.ValidatedData;
import com.dreamsol.entites.Plant;
import com.dreamsol.entites.Unit;
import com.dreamsol.repositories.PlantRepository;
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
public class PlantService {

    private final PlantRepository plantRepository;
    private final ExcelUtility excelUtility;
    private final UnitRepository unitRepository;
    private final JwtUtil jwtUtil;

    public ResponseEntity<PlantResponseDto> createPlant(PlantRequestDto plantRequestDto) {
        Plant plant = DtoUtilities.plantRequestDtoToPlant(plantRequestDto);

        Optional<Plant> dbPlant = plantRepository.findByPlantNameIgnoreCase(plantRequestDto.getPlantName());
        if (dbPlant.isPresent()) {
            throw new RuntimeException("Plant with name " + plantRequestDto.getPlantName() + " already Exist");
        }
        // Check if the unit exists
        unitRepository.findById(plantRequestDto.getUnitId())
                .orElseThrow(() -> new RuntimeException(
                        "Unit with this id does not exist : " + plantRequestDto.getUnitId()));
        plant.setCreatedBy(jwtUtil.getCurrentLoginUser());
        plant.setUpdatedBy(jwtUtil.getCurrentLoginUser());
        plant.setStatus(true);
        Plant savedPlant = plantRepository.save(plant);
        return ResponseEntity.ok(DtoUtilities.plantToPlantResponseDto(savedPlant));
    }

    public ResponseEntity<PlantResponseDto> updatePlant(Long id, PlantRequestDto plantRequestDto) {
        Plant plant = plantRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Plant", "Id", id));
        // Check if the unit exists
        unitRepository.findById(plantRequestDto.getUnitId())
                .orElseThrow(() -> new RuntimeException(
                        "Unit with this id does not exist : " + plantRequestDto.getUnitId()));
        Plant updatedPlant = DtoUtilities.plantRequestDtoToPlant(plant, plantRequestDto);
        updatedPlant.setUpdatedBy(jwtUtil.getCurrentLoginUser());
        updatedPlant = plantRepository.save(updatedPlant);
        return ResponseEntity.ok(DtoUtilities.plantToPlantResponseDto(updatedPlant));
    }

    public ResponseEntity<PlantResponseDto> getPlantById(Long id) {
        Plant plant = plantRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Plant", "Id", id));
        return ResponseEntity.ok(DtoUtilities.plantToPlantResponseDto(plant));
    }

    public ResponseEntity<?> getPlants(String plantName, int pageSize, int page, String sortBy, String sortDirection,
            String status,
            Long unitId) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("Asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by(direction, sortBy));
        Boolean statusBoolean = status != null ? Boolean.parseBoolean(status) : null;

        Page<Plant> plantsPage = plantRepository.findByStatusAndUnitIdAndPlantNameIgnoreCase(statusBoolean, unitId,
                plantName, pageRequest);

        Page<PlantResponseDto> plantResponseDtos = plantsPage.map(DtoUtilities::plantToPlantResponseDto);
        return ResponseEntity.ok(plantResponseDtos);

    }

    public ResponseEntity<?> deletePlant(Long id) {
        Plant plant = plantRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Plant", "Id", id));

        if (plant.isStatus()) {
            plant.setStatus(false);
            plant.setUpdatedAt(LocalDateTime.now());
            plant.setUpdatedBy(jwtUtil.getCurrentLoginUser());
            plantRepository.save(plant);
            return ResponseEntity.ok().body("Plant has been deleted");
        } else {
            throw new ResourceNotFoundException("Plant", "Id", id);
        }
    }

    public ResponseEntity<?> downloadPlantDataAsExcel(String status, Long unitId, String plantName) {
        try {
            Boolean statusBoolean = status != null ? Boolean.parseBoolean(status) : null;

            List<Plant> plantList = plantRepository.findByStatusAndUnitIdAndPlantNameIgnoreCase(statusBoolean, unitId,
                    plantName);
            if (plantList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No plants available!");
            }

            List<PlantResponseDto> plantResDtoList = plantList.stream()
                    .map(DtoUtilities::plantToPlantResponseDto)
                    .collect(Collectors.toList());

            String fileName = "plant_excel_data.xlsx";
            String sheetName = fileName.substring(0, fileName.indexOf('.'));
            Resource resource = excelUtility.downloadDataAsExcel(plantResDtoList, sheetName);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error! " + e);
        }
    }

    public ResponseEntity<?> downloadPlantExcelSample() throws IOException {
        String fileName = "plant_excel_sample.xlsx";
        String sheetName = fileName.substring(0, fileName.indexOf('.'));
        Resource resource = excelUtility.downloadExcelSample(PlantRequestDto.class, sheetName);

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
        List<PlantRequestDto> plantRequestDtos = new ArrayList<>();
        for(int i=0;i<validList.size();)
        {
            ValidatedData validatedData = (ValidatedData) validList.get(i);
            PlantRequestDto plantRequestDto = (PlantRequestDto) validatedData.getData();
            ValidatedData checkedData = checkValidOrNot(plantRequestDto);
            if(!checkedData.isStatus())
            {
                invalidList.add(checkedData);
                validList.remove(validatedData);
                continue;
            }
            plantRequestDtos.add(plantRequestDto);
            i++;
        }
        validateDataResponse.setValidDataList(plantRequestDtos);
        return validateDataResponse;
    }
    public ValidatedData checkValidOrNot(PlantRequestDto plantRequestDto){
        StringBuilder message = new StringBuilder();
        boolean status = true;
        ValidatedData checkedData = new ValidatedData();
        Optional<Plant> dbPlant = plantRepository.findByPlantNameIgnoreCase(plantRequestDto.getPlantName());
        if (dbPlant.isPresent()) {
            message.append("plant already exist!");
            status = false;
        }
        // Check if the unit exists
        Optional<Unit> dbUnit = unitRepository.findById(plantRequestDto.getUnitId());
        if(dbUnit.isEmpty()){
            message.append("unit doesn't exist");
            status = false;
        }
        checkedData.setData(plantRequestDto);
        checkedData.setMessage(message.toString());
        checkedData.setStatus(status);
        return checkedData;
    }
    public ResponseEntity<?> saveBulkData(List<PlantRequestDto> plantRequestDtos) {
        try{
            String username = jwtUtil.getCurrentLoginUser();
            List<Plant> plantList = plantRequestDtos.stream()
                            .map((plantRequestDto -> {
                                Plant plant = DtoUtilities.plantRequestDtoToPlant(plantRequestDto);
                                plant.setUnitId(plantRequestDto.getUnitId());
                                plant.setCreatedBy(username);
                                plant.setUpdatedBy(username);
                                return plant;
                            }))
                            .collect(Collectors.toList());
            plantRepository.saveAll(plantList);
            return ResponseEntity.status(HttpStatus.CREATED).body("All data saved successfully");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while saving bulk data: "+e.getMessage());
        }
    }
    public ResponseEntity<?> getDropDown() {
        List<Plant> plants = plantRepository.findAll();
        return ResponseEntity.ok(plants.stream().map(this::plantToDropDownRes).collect(Collectors.toList()));
    }

    private DropDownDto plantToDropDownRes(Plant Plant) {
        DropDownDto dto = new DropDownDto();
        dto.setId(Plant.getId());
        dto.setName(Plant.getPlantName());
        return dto;
    }
}
