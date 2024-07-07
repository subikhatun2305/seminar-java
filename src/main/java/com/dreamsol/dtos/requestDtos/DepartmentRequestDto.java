package com.dreamsol.dtos.requestDtos;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Validated
@EqualsAndHashCode(callSuper = false)
public class DepartmentRequestDto extends CommonAutoIdEntityRequestDto {

    @NotEmpty(message = "departmentName cannot be Empty")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Only alphabets, numbers, and spaces are allowed in departmentName")
    @Size(min = 3, max = 50, message = "departmentName length must be between 3 and 50 characters")
    private String departmentName;

    @NotEmpty(message = "departmentCode cannot be Empty")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Only alphabets, numbers, and spaces are allowed in departmentCode")
    @Size(min = 3, max = 50, message = "departmentCode length must be between 3 and 50 characters")
    private String departmentCode;

}
