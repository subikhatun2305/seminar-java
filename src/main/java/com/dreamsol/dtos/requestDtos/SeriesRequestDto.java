
package com.dreamsol.dtos.requestDtos;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
public class SeriesRequestDto extends CommonAutoIdEntityRequestDto {

    @NotEmpty(message = "seriesFor cannot be Empty")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Only alphabets and spaces are allowed in seriesFor")
    @Size(min = 5, max = 50, message = "seriesFor length must be between 5 and 50 characters")
    private String seriesFor;

    @NotEmpty(message = "subPrefix cannot be Empty")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Only alphabets and spaces are allowed in subPrefix")
    @Size(min = 5, max = 50, message = "subPrefix length must be between 5 and 50 characters")
    private String subPrefix;

    @Max(value = 99999, message = "numberSeries should be less thant 99999")
    @Min(value = 999, message = "numberSeries should be more than 999")
    private int numberSeries;
}
