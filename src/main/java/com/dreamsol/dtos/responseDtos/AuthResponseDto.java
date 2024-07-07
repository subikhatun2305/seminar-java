package com.dreamsol.dtos.responseDtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDto
{
    private String accessToken;
    private String refreshToken;
}
