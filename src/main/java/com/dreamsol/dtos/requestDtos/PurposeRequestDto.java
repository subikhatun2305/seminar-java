package com.dreamsol.dtos.requestDtos;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurposeRequestDto extends CommonAutoIdEntityRequestDto {

    @NotEmpty(message = "purposeFor cannot be Empty")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Only alphabets and spaces are allowed in purposeFor")
    @Size(min = 5, max = 50, message = "purposeFor length must be between 5 and 50 characters")
    private String purposeFor;

    @NotEmpty(message = "purposeBrief cannot be Empty")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Only alphabets and spaces are allowed in purposeBrief")
    @Size(min = 3, max = 350, message = "purposeBrief length must be between 5 and 50 characters")
    private String purposeBrief;

    private boolean alert;

    @Size(max = 10, min = 5, message = "Size cannot be more than 10 and less than 5 for Time")
    private String alertTime;

    private Long userId;

    private Long departmentId;

}