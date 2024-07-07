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
public class UnitRequestDto {
    @NotEmpty(message = "unitName cannot be empty")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Only alphabets, numbers, and spaces are allowed in unitName")
    @Size(min = 2, max = 50, message = "unitName length must be between 2 and 50 characters")
    private String unitName;

    @NotEmpty(message = "unitIp cannot be empty")
    // @Pattern(regexp =
    // "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$",
    // message = "unitIp must be a valid IP address")
    private String unitIp;

    @NotEmpty(message = "unitCity cannot be empty")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Only alphabets and spaces are allowed in unitCity")
    @Size(min = 3, max = 50, message = "unitCity length must be between 3 and 50 characters")
    private String unitCity;

    @NotEmpty(message = "passAddress cannot be empty")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Only alphabets, numbers, and spaces are allowed in passAddress")
    @Size(min = 5, max = 100, message = "passAddress length must be between 5 and 100 characters")
    private String passAddress;

    @NotEmpty(message = "passDisclaimer cannot be empty")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Only alphabets, numbers, and spaces are allowed in passDisclaimer")
    @Size(min = 5, max = 250, message = "passDisclaimer length must be between 5 and 250 characters")
    private String passDisclaimer;
}
