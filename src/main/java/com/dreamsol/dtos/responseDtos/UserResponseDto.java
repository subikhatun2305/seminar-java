package com.dreamsol.dtos.responseDtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto extends CommonAutoIdEntityResponseDto
{
    private String name;
    private String email;
    private Long mobile;
    private UserTypeResponseDto usertype;
}
