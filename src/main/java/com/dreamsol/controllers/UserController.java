package com.dreamsol.controllers;

import com.dreamsol.dtos.requestDtos.UserRequestDto;
import com.dreamsol.services.CommonService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class UserController
{
    private final CommonService<UserRequestDto,Long> userService;

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserRequestDto userRequestDto, @PathVariable Long id)
    {
        return userService.update(userRequestDto,id);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id)
    {
        return userService.delete(id);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id)
    {
        return userService.get(id);
    }

    @GetMapping("/get-dropdown")
    public ResponseEntity<?> getDropDown()
    {
        return userService.getDropDown();
    }
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir,
            @RequestParam(value = "unitId", defaultValue = "1", required = false) Long unitId,
            @RequestParam(value = "status", required = false) Boolean status
    )
    {
        return userService.getAll(pageNumber,pageSize,sortBy,sortDir,unitId,status);
    }
    @GetMapping(value = "/download-excel-data", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelData(
            @RequestParam(value = "unitId", defaultValue = "1", required = false) Long unitId,
            @RequestParam(value = "status", required = false) Boolean status
    )
    {
        return userService.downloadDataAsExcel(unitId,status);
    }
    @GetMapping(value = "/download-excel-sample",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelSample()
    {
        return userService.downloadExcelSample();
    }
    @PostMapping(value = "/upload-excel-data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadExcelData(@RequestParam("file") MultipartFile file)
    {
        return userService.uploadExcelFile(file, UserRequestDto.class);
    }
    @PostMapping("/save-bulk-data")
    public ResponseEntity<?> saveBulkData(@RequestBody @Valid List<UserRequestDto> userList)
    {
        return userService.saveBulkData(userList);
    }
}