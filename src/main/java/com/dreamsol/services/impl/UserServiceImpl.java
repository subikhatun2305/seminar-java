package com.dreamsol.services.impl;

import com.dreamsol.dtos.requestDtos.UserRequestDto;
import com.dreamsol.dtos.responseDtos.DropDownDto;
import com.dreamsol.dtos.responseDtos.ExcelValidateDataResponseDto;
import com.dreamsol.dtos.responseDtos.UserResponseDto;
import com.dreamsol.dtos.responseDtos.ValidatedData;
import com.dreamsol.entites.Unit;
import com.dreamsol.entites.User;
import com.dreamsol.entites.UserType;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.UnitRepository;
import com.dreamsol.repositories.UserRepository;
import com.dreamsol.repositories.UserTypeRepository;
import com.dreamsol.securities.JwtUtil;
import com.dreamsol.services.CommonService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("userService")
@RequiredArgsConstructor
public class UserServiceImpl implements CommonService<UserRequestDto,Long>
{
    private final JwtUtil jwtUtil;
    private final DtoUtilities dtoUtilities;
    private final ExcelUtility excelUtility;
    private final UserRepository userRepository;
    private final UserTypeRepository userTypeRepository;
    private final UnitRepository unitRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public ResponseEntity<?> create(UserRequestDto userRequestDto)
    {
        try {

            Optional<User> userOptional = userRepository.findByEmailOrMobile(userRequestDto.getEmail(), userRequestDto.getMobile());
            if (userOptional.isPresent()) {
                logger.info("user already exist");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("user already exist!");
            }

            Optional<UserType> userTypeOptional = userTypeRepository.findByUserTypeName(userRequestDto.getUserTypeName());
            if(userTypeOptional.isEmpty()){
                logger.info("usertype doesn't exist!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("usertype doesn't exist!");
            }

            User user = dtoUtilities.userRequstDtoToUser(userRequestDto);
            user.setUserType(userTypeOptional.get());
            userRepository.save(user);
            logger.info("New user created successfully!");
            return ResponseEntity.status(HttpStatus.CREATED).body("New user created successfully!");
        } catch (Exception e) {
            logger.error("Error occurred while creating new user: ",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while creating new user.");
        }
    }

    @Override
    public ResponseEntity<?> update(UserRequestDto userRequestDto, Long id) {
        try {
            User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("user not found with id: " + id));

            Optional<UserType> userTypeOptional = userTypeRepository.findByUserTypeName(userRequestDto.getUserTypeName());
            if(userTypeOptional.isEmpty()){
                logger.info("usertype doesn't exist!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("usertype doesn't exist!");
            }
            BeanUtils.copyProperties(userRequestDto,user);
            user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
            user.setUserType(userTypeOptional.get());
            user.setUpdatedBy(jwtUtil.getCurrentLoginUser());
            userRepository.save(user);
            logger.info("User with id: "+id+" updated successfully!");
            return ResponseEntity.status(HttpStatus.OK).body("User with id: "+id+" updated successfully!");
        } catch (Exception e) {
            logger.error("Error occurred while updating user: ",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while updating user.");
        }
    }

    @Override
    public ResponseEntity<?> delete(Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("user","id",id));
            user.setStatus(false);
            userRepository.save(user);
            logger.info("user with id: "+id+" deleted successfully!");
            return ResponseEntity.status(HttpStatus.OK).body("user with id: "+id+" deleted successfully!");
        } catch (Exception e) {
            logger.error("Error occurred while deleting user with id: "+id,e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while deleting user with id: "+id);
        }
    }

    @Override
    public ResponseEntity<?> get(Long id) {
        try {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isEmpty()) {
                logger.info("user not found with id: "+id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with id: " + id);
            }
            logger.info("user with id: "+id+" found successfully!");
            return ResponseEntity.status(HttpStatus.FOUND).body(dtoUtilities.userToUserResponseDto(userOptional.get()));
        } catch (Exception e) {
            logger.error("Error occurred while fetching user with id: "+id,e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while fetching user with id: "+id);
        }
    }

    @Override
    public ResponseEntity<?> getDropDown() {
        try {
            List<User> users = userRepository.findAll();
            List<DropDownDto> dropDownDtos = users.stream()
                    .map(user -> dtoUtilities.createDropDown.apply(user.getId(), user.getEmail()))
                    .collect(Collectors.toList());
            logger.info("fetched all users for drop-down");
            return ResponseEntity.status(HttpStatus.OK).body(dropDownDtos);
        }catch(Exception e){
            logger.error("Error occurred while fetching user drop-down: ",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while fetching user drop-down");
        }
    }

    @Override
    public ResponseEntity<?> getAll(Integer pageNumber,Integer pageSize,String sortBy,String sortDir,Long unitId,Boolean status) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(pageNumber,pageSize, sort);
            List<User> userList = userRepository.findByFilters(unitId,status,pageable);
            if (userList.isEmpty()) {
                logger.info("No users available!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No users available!");
            }
            List<UserResponseDto> userResponseDtoList = userList.stream()
                    .map((dtoUtilities::userToUserResponseDto)).collect(Collectors.toList());
            logger.info("fetching all users successfully!");
            return ResponseEntity.status(HttpStatus.OK).body(userResponseDtoList);
        } catch (Exception e) {
            logger.error("Error occurred while fetching user's all data",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while fetching user's all data");
        }
    }

    @Override
    public ResponseEntity<?> downloadDataAsExcel(Long unitId,Boolean status) {
        try {
            List<User> userList = userRepository.findByFilters(unitId,status);
            if (userList.isEmpty()) {
                logger.info("No users available!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No users available!");
            }
            List<UserResponseDto> userResponseDtoList = userList.stream().map(dtoUtilities::userToUserResponseDto)
                    .collect(Collectors.toList());
            String fileName = "user_excel_data.xlsx";
            String sheetName = fileName.substring(0,fileName.indexOf('.'));
            Resource resource = excelUtility.downloadDataAsExcel(userResponseDtoList, sheetName);
            logger.info("data as excel file downloaded successfully!");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        } catch (Exception e) {
            logger.error("Error occurred while downloading data as excel",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while downloading data as excel.");
        }
    }
    @Override
    public ResponseEntity<?> downloadExcelSample()
    {
        try {
            String fileName = "user_excel_sample.xlsx";
            String sheetName = fileName.substring(0, fileName.indexOf('.'));
            Resource resource = excelUtility.downloadExcelSample(UserRequestDto.class, sheetName);
            logger.info("Excel format downloaded successfully!");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        }catch (Exception e)
        {
            logger.error("Error occurred while downloading excel format: ",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while downloading excel format");
        }
    }
    @Override
    public ResponseEntity<?> uploadExcelFile(MultipartFile file,Class<?> requestDtoClass)
    {
        try{
            if(excelUtility.isExcelFile(file))
            {
                ExcelValidateDataResponseDto validateDataResponse = excelUtility.validateExcelData(file,requestDtoClass);
                validateDataResponse = validateDataFromDB(validateDataResponse);
                validateDataResponse.setTotalValidData(validateDataResponse.getValidDataList().size());
                validateDataResponse.setTotalInvalidData(validateDataResponse.getInvalidDataList().size());
                if(validateDataResponse.getTotalData()==0){
                    logger.info("No data available in excel sheet!");
                    return ResponseEntity.status(HttpStatus.OK).body("No data available in excel sheet!");
                }
                logger.info("Excel data validated successfully!");
                return ResponseEntity.status(HttpStatus.OK).body(validateDataResponse);
            }else {
                logger.info("Incorrect uploaded file type!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect uploaded file type! supported only excel [.xlsx or xls] type");
            }
        }catch(Exception e)
        {
            logger.error("Error occurred while validating excel data",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while validating excel data.");
        }
    }
    public ExcelValidateDataResponseDto validateDataFromDB(ExcelValidateDataResponseDto validateDataResponse)
    {
        List<?> validList = validateDataResponse.getValidDataList();
        List<ValidatedData> invalidList = validateDataResponse.getInvalidDataList();
        List<UserRequestDto> userRequestDtoList = new ArrayList<>();
        for(int i=0;i<validList.size();)
        {
            ValidatedData validatedData = (ValidatedData) validList.get(i);
            UserRequestDto userRequestDto = (UserRequestDto) validatedData.getData();
            ValidatedData checkedData = checkValidOrNot(userRequestDto);
            if(!checkedData.isStatus())
            {
                invalidList.add(checkedData);
                validList.remove(validatedData);
                continue;
            }
            userRequestDtoList.add(userRequestDto);
            i++;
        }
        validateDataResponse.setValidDataList(userRequestDtoList);
        return validateDataResponse;
    }

    public ValidatedData checkValidOrNot(UserRequestDto userRequestDto){
        StringBuilder message = new StringBuilder();
        boolean status = true;
        ValidatedData checkedData = new ValidatedData();
        Optional<User> userOptional = userRepository.findByEmailOrMobile(userRequestDto.getEmail(),userRequestDto.getMobile());
        if(userOptional.isPresent()) {
            message.append("user with given mobile/email already exist, ");
            status = false;
        }
        Optional<UserType> userTypeOptional = userTypeRepository.findByUserTypeName(userRequestDto.getUserTypeName());
        if(userTypeOptional.isEmpty()) {
            message.append("usertype doesn't exist!, ");
            status = false;
        }
        Optional<Unit> unitOptional = unitRepository.findById(userRequestDto.getUnitId());
        if(unitOptional.isEmpty()){
            message.append("unit doesn't exist!");
            status = false;
        }
        checkedData.setData(userRequestDto);
        checkedData.setMessage(message.toString());
        checkedData.setStatus(status);
        return checkedData;
    }
    @Override
    public ResponseEntity<?> saveBulkData(List<UserRequestDto> userRequestDtoList) {
        try{
            String username = jwtUtil.getCurrentLoginUser();
            List<User> userList = userRequestDtoList.stream()
                    .map((userRequestDto -> {
                        User user = dtoUtilities.userRequstDtoToUser(userRequestDto);
                        user.setCreatedBy(username);
                        user.setUpdatedBy(username);
                        userTypeRepository.findByUserTypeName(userRequestDto.getUserTypeName()).ifPresent(user::setUserType);
                        return user;
                    }))
                    .collect(Collectors.toList());
            userRepository.saveAll(userList);
            logger.info("All data saved successfully!");
            return ResponseEntity.status(HttpStatus.CREATED).body("All data saved successfully");
        }catch (Exception e){
            logger.error("Error occurred while saving bulk data, ",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while saving bulk data: "+e.getMessage());
        }
    }
}
