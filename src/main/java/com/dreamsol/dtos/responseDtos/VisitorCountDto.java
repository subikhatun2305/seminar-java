package com.dreamsol.dtos.responseDtos;

import lombok.Setter;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VisitorCountDto {
    private Long totalVisitor;
    private Long visitorIn;
    private Long visitorOut;
    private Long visitorApprovalRequired;
    private Long VistorApprovalNotRequired;
}
