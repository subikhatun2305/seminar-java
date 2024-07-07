package com.dreamsol.dtos.requestDtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserRequestDto extends CommonAutoIdEntityRequestDto
{

    @NotBlank(message = "name is mandatory")
    @Size(min = 3, max=50, message = "user name should contain min of 3 and max of 100 characters")
    @Pattern(regexp = "^[A-Za-z]+(?:[\\s'][A-Za-z]+)*$", message = "name should contain alphabets only")
    @Schema(description = "name of user", example = " ")
    private String name;

    @NotBlank(message = "Email is mandatory")
    @Size(min = 8, max = 100, message = "email must be 8 to 100 characters long")
    @Email(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "Invalid email format")
    @Schema(description = "email of user", example = " ")
    private String email;

    @NotNull(message = "Mobile number is mandatory")
    @Min(value = 6000000000L, message = "Mobile number must be at least 10 digits and starts with 6,7,8 or 9")
    @Max(value = 9999999999L, message = "Mobile number must be at most 10 digits")
    @Schema(description = "mobile no. of user", example = "0")
    private Long mobile;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
            message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character (@#$%^&+=)"
    )
    @Schema(description = "password of user", example = " ")
    private String password;

    @NotBlank(message = "usertype is mandatory")
    private String userTypeName;

}
