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
@AllArgsConstructor
@NoArgsConstructor
public class PlantRequestDto extends CommonAutoIdEntityRequestDto {

    @NotEmpty(message = "plantName cannot be Empty")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Only alphabets, numbers, and spaces are allowed in plantName")
    @Size(min = 3, max = 50, message = "plantName length must be between 3 and 50 characters")
    private String plantName;

    @NotEmpty(message = "plantBrief cannot be Empty")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Only alphabets, numbers, and spaces are allowed in plantBrief")
    @Size(min = 5, max = 250, message = "plantBrief length must be between 5 and 250 characters")
    private String plantBrief;

}
