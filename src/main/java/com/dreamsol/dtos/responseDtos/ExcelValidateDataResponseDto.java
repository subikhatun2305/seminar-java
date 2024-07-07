package com.dreamsol.dtos.responseDtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExcelValidateDataResponseDto
{
    List<?> validDataList;
    List<ValidatedData> invalidDataList;
    long totalData;
    long totalValidData;
    long totalInvalidData;
    String message;
}
