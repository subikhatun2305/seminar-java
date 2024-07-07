package com.dreamsol.securities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class TokenPayload
{
    private Long userid;
    private Long unitId;
    private String name;
    private String username;
    private Long mobile;
    private String email;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean status;
}
