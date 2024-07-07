package com.dreamsol.dtos.responseDtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PurposeResponseDto extends CommonAutoIdEntityResponseDto {
    private String purposeFor;
    private String purposeBrief;
    private boolean alert;
    private LocalTime alertTime;
    private UserResponseDto user;
    private DepartmentResponseDto department;
}
