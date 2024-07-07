package com.dreamsol.services;

import com.dreamsol.dtos.responseDtos.ExcelValidateDataResponseDto;
import com.dreamsol.dtos.responseDtos.ValidatedData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CommonService<T,ID>
{
    ResponseEntity<?> create(T data);
    ResponseEntity<?> update(T data, ID id);
    ResponseEntity<?> delete(ID id);
    ResponseEntity<?> get(ID id);
    ResponseEntity<?> getDropDown();
    ResponseEntity<?> getAll(Integer pageNumber,Integer pageSize,String sortBy,String sortDir,Long unitId,Boolean status);
    ResponseEntity<?> downloadDataAsExcel(Long unitId, Boolean status);
    ResponseEntity<?> downloadExcelSample();
    ResponseEntity<?> uploadExcelFile(MultipartFile file,Class<?> currentClass);
    ExcelValidateDataResponseDto validateDataFromDB(ExcelValidateDataResponseDto validateDataResponse);
    ValidatedData checkValidOrNot(T data);
    ResponseEntity<?> saveBulkData(List<T> list);
}
