package com.modi.leave_management_system.controller;

import com.modi.leave_management_system.dto.LeaveBalanceDTO;
import com.modi.leave_management_system.service.LeaveBalanceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/balances")
public class LeaveBalanceController {

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    // GET /api/balances
    // employee sees all their balances for current year
    // employeeId comes from JWT token — no path variable needed
    @GetMapping
    public ResponseEntity<List<LeaveBalanceDTO>> getMyBalances(
            HttpServletRequest httpServletRequest) {

        Long employeeId = (Long) httpServletRequest.getAttribute("empId");

        return ResponseEntity.ok(
                leaveBalanceService.getBalancesForEmployee(employeeId));
    }

    // GET /api/balances/available
    // employee sees only leave types they still have days left for
    @GetMapping("/available")
    public ResponseEntity<List<LeaveBalanceDTO>> getAvailableBalances(
            HttpServletRequest httpServletRequest) {

        Long employeeId = (Long) httpServletRequest.getAttribute("empId");

        return ResponseEntity.ok(
                leaveBalanceService.getAvailableBalances(employeeId));
    }
}