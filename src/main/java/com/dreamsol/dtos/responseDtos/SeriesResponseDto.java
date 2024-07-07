package com.dreamsol.dtos.responseDtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SeriesResponseDto extends CommonAutoIdEntityResponseDto {

    private String seriesFor;
    private String prefix;
    private String subPrefix;
    private int numberSeries;
}
