package com.modi.leave_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveBalanceDTO { //basically DTOs are used to simplify information being sent to frontend
    private long leaveBalanceId;
    private String leaveTypeName;
    private int year;
    private double usedDays;
    private double pendingDays;
    private double availableDays; //computed in service and sent to frontend
    private double totalDays;
}

