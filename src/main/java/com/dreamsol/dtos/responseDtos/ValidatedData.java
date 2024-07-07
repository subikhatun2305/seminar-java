package com.dreamsol.dtos.responseDtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidatedData {
    private Object data;
    private boolean status;
    private String message;
}
