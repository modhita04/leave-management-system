package com.modi.leave_management_system.dto;

import com.modi.leave_management_system.enums.LeaveStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestDTO {
    private Long employeeId;
    private String employeeName;
    private String LeaveTypeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private double numberOfDays;
    private LeaveStatus leaveStatus;
    private String reason;
    private String managerComment;
    private String approvedByName;
    private LocalDateTime appliedAt;

}
