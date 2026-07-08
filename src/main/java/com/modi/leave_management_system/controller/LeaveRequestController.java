package com.modi.leave_management_system.controller;

import com.modi.leave_management_system.dto.LeaveRequestDTO;
import com.modi.leave_management_system.dto.LeaveRequestInput;
import com.modi.leave_management_system.dto.LeaveRequestInput;
import com.modi.leave_management_system.service.LeaveRequestService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    // POST /api/leaves/apply
    // employee submits a new leave request
    // employeeId comes from JWT token — not from client
    @PostMapping("/apply")
    public ResponseEntity<LeaveRequestDTO> applyForLeave(
            @RequestBody LeaveRequestInput input,        // ← comma was missing here
            HttpServletRequest httpServletRequest) {

        Long employeeId = (Long) httpServletRequest.getAttribute("empId");

        LeaveRequestDTO result = leaveRequestService.applyForLeave(
                employeeId,
                input.getLeaveTypeId(),     // ← was getleaveTypeId() — Java is case sensitive
                input.getStartDate(),
                input.getEndDate(),
                input.getReason()
        );
        return ResponseEntity.ok(result);   // ← was returning 'leaveRequest' which doesn't exist
    }

    // POST /api/leaves/{requestId}/approve
    // manager approves a pending request
    // managerId comes from JWT token — not from URL
    @PostMapping("/{requestId}/approve")
    public ResponseEntity<LeaveRequestDTO> approveLeave(
            @PathVariable Long requestId,
            @RequestParam(required = false) String comment,  // ← was @PathVariable, should be @RequestParam
            HttpServletRequest httpServletRequest) {

        Long managerId = (Long) httpServletRequest.getAttribute("empId");

        return ResponseEntity.ok(
                leaveRequestService.approveLeave(requestId, managerId, comment));
    }

    // POST /api/leaves/{requestId}/reject
    // manager rejects a pending request
    // managerId comes from JWT token — not from URL
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<LeaveRequestDTO> rejectLeave(
            @PathVariable Long requestId,                    // ← was leaveRequestId, must match {requestId}
            @RequestParam(required = false) String comment,  // ← was @PathVariable, should be @RequestParam
            HttpServletRequest httpServletRequest) {

        Long managerId = (Long) httpServletRequest.getAttribute("empId");

        return ResponseEntity.ok(
                leaveRequestService.rejectLeave(requestId, managerId, comment));
    }

    // POST /api/leaves/{requestId}/cancel
    // employee cancels their own pending request
    // employeeId comes from JWT token — not from URL
    @PostMapping("/{requestId}/cancel")
    public ResponseEntity<LeaveRequestDTO> cancelLeave(
            @PathVariable Long requestId,
            HttpServletRequest httpServletRequest) {   // ← removed @PathVariable Long employeeId

        Long employeeId = (Long) httpServletRequest.getAttribute("empId");

        return ResponseEntity.ok(
                leaveRequestService.cancelLeave(requestId, employeeId));
    }

    // GET /api/leaves/my
    // employee views their own leave history
    // employeeId comes from JWT token — no path variable needed
    @GetMapping("/my")                                       // ← removed /{employeeId} from path
    public ResponseEntity<List<LeaveRequestDTO>> getMyLeaves(
            HttpServletRequest httpServletRequest) {         // ← removed @PathVariable

        Long employeeId = (Long) httpServletRequest.getAttribute("empId");

        return ResponseEntity.ok(
                leaveRequestService.getMyLeaves(employeeId));
    }

    // GET /api/leaves/pending
    // manager sees all pending requests from their team
    // managerId comes from JWT token — no path variable needed
    @GetMapping("/pending")                                  // ← removed /{managerId} from path
    public ResponseEntity<List<LeaveRequestDTO>> getPendingLeaves(
            HttpServletRequest httpServletRequest) {         // ← removed @PathVariable

        Long managerId = (Long) httpServletRequest.getAttribute("empId");

        return ResponseEntity.ok(
                leaveRequestService.getPendingRequestsForManager(managerId));
    }

    // GET /api/leaves/team
    // manager sees full leave history of their team
    // managerId comes from JWT token — no path variable needed
    @GetMapping("/team")                                     // ← removed /{managerId} from path
    public ResponseEntity<List<LeaveRequestDTO>> getTeamLeaves(
            HttpServletRequest httpServletRequest) {         // ← removed @PathVariable

        Long managerId = (Long) httpServletRequest.getAttribute("empId");

        return ResponseEntity.ok(
                leaveRequestService.getTeamLeaveHistory(managerId));
    }
}