package com.dreamsol.dtos.requestDtos;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VisitorPrerequestDto extends CommonAutoIdEntityRequestDto
{

    @NotBlank(message = "Name is mandatory")
    @Size(min = 3, max=50, message = "Visitor name should contain a minimum of 3 and a maximum of 50 characters")
    @Pattern(regexp = "^[A-Za-z]+(?:[\\s'][A-Za-z]+)*$", message = "Name should contain alphabets only")
    @Schema(description = "Name of visitor", example = " ")
    private String name;

    @NotNull(message = "Mobile no. is mandatory")
    @Min(value = 6000000000L, message = "Mobile number must start with 6,7,8 or 9")
    @Max(value = 9999999999L, message = "Mobile number must be at most 10 digits")
    @Schema(description = "mobile no. of visitor", example = "0")
    private Long mobile;

    @Size(max = 100, message = "Email must be at most 100 characters long")
    @Email(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "Invalid email format")
    @Schema(description = "Email of visitor", example = " ")
    private String email;

    @NotBlank(message = "Organization name is mandatory")
    @Size(min = 3, max = 50, message = "Organization name must be 3 to 50 characters long")
    @Schema(description = "Organization name of visitor", example = " ")
    private String organizationName;

    @Size(max = 100, message = "Address must be at most 100 characters long")
    @Schema(description = "Address of the visitor", example = " ")
    private String address;

    @Size(max = 50, message = "Possessions allowed must be at most 50 characters long")
    @Schema(description = "Allowed possessions for visitor", example = " ")
    private String possessionsAllowed;

    @NotNull(message = "meeting purpose must be selected")
    @Schema(description = "Purpose of the meeting", example = " ")
    private Long meetingPurposeId;

    @NotNull(message = "must schedule the meeting date")
    @Schema(description = "Scheduled time for the meeting", example = " ")
    private String meetingSchedule;

    @Schema(description = "Start hours for the meeting", example = " ")
    private String startHours;

    @Schema(description = "End hours for the meeting", example = " ")
    private String endHours;

    @Size(max = 100, message = "Location must be at most 100 characters long")
    @Schema(description = "Location of the meeting", example = " ")
    private String location;

    @Schema(description = "meeting status", example = " ")
    private String meetingStatus;

}
