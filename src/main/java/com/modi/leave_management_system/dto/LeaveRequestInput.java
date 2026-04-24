package com.modi.leave_management_system.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveRequestInput {
    private Long employeeId;
    private Long leaveTypeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
}
