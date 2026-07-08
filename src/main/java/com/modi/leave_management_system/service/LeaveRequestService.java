package com.modi.leave_management_system.service;

import com.modi.leave_management_system.dto.LeaveBalanceDTO;
import com.modi.leave_management_system.dto.LeaveRequestDTO;
import com.modi.leave_management_system.entity.Employee;
import com.modi.leave_management_system.entity.LeaveBalance;
import com.modi.leave_management_system.entity.LeaveRequest;
import com.modi.leave_management_system.entity.LeaveType;
import com.modi.leave_management_system.enums.LeaveStatus;
import com.modi.leave_management_system.exception.InsufficientLeaveException;
import com.modi.leave_management_system.exception.OverlappingLeaveException;
import com.modi.leave_management_system.repository.EmployeeRepository;
import com.modi.leave_management_system.repository.LeaveRequestRepository;
import com.modi.leave_management_system.repository.LeaveTypeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveRequestService {
    @Autowired
    private LeaveRequestRepository leaveRequestRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private LeaveTypeRepository leaveTypeRepository;
    @Autowired
    private LeaveBalanceService leaveBalanceService;

    //1. Apply for leave, employee submits a new leave request
    // validates if balance is there, weekends, dates overlap
    @Transactional
    public LeaveRequestDTO applyForLeave(Long employeeId, Long leaveTypeId, LocalDate startDate, LocalDate endDate, String reason){
        //1. fetch employee and leave type
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(()-> new RuntimeException("Employee not found"));
        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId).orElseThrow(()-> new RuntimeException("LeaveType not found"));
        //2. Validate dates
        if(endDate.isBefore(LocalDate.now()) || startDate.isBefore(LocalDate.now())){
            throw new RuntimeException("Start date and end date must be before start date");
        }
        //3. calculate working days excluding weekends
        double leaveDays = calculateLeaveDays(startDate, endDate);
        if(leaveDays==0){
            throw new RuntimeException("Selected days fall during weekends");
        }
        //4. check for overlapping requests
        List<LeaveRequest> overlapping = leaveRequestRepository.findOverlappingRequests(employee, startDate, endDate);
        if(!overlapping.isEmpty()){
            throw new OverlappingLeaveException("You already have overlapping leave request during these days");
        }
        //5.check if sufficient balance for leavetype exists
        double available = leaveBalanceService.
                getAvailableBalances(employeeId)
                .stream()
                .filter(b-> b.getLeaveTypeName().equals(leaveType.getLeaveTypeName()))
                .mapToDouble(b->b.getAvailableDays())
                .findFirst()
                .orElse(0);
        if(available<leaveDays){
            throw new InsufficientLeaveException("Insufficient balance. Available leave days: " + available + "Requested days: "+leaveDays);
        }
        //6.Create and save request
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setStartDate(startDate);
        leaveRequest.setEndDate(endDate);
        leaveRequest.setReason(reason);
        leaveRequest.setNumberOfDays(leaveDays);
        leaveRequest.setStatus(LeaveStatus.PENDING);
        LeaveRequest savedLeaveRequest = leaveRequestRepository.save(leaveRequest);

        //7. Lock days as pending in balance
        leaveBalanceService.holdPendingDays(employee, leaveType, leaveDays);
        return convertToDTO(savedLeaveRequest);

    }
    //2. Approve leave, manager action
    @Transactional
    public LeaveRequestDTO approveLeave(Long leaveRequestId, Long managerId, String comment){
        //1. fetch request
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId).orElseThrow(()-> new RuntimeException("LeaveRequest not found"));
        //2. Only pending requests can be approved
        if(leaveRequest.getStatus() != LeaveStatus.PENDING){
            throw new RuntimeException("Only pending requests can be approved. Current leave status is" + leaveRequest.getStatus());
        }
        //3. fetch manager
        Employee manager = employeeRepository.findById(managerId).orElseThrow(()-> new RuntimeException("Manager not found"));
        //4. verify whether manager actually manages the employee
        Employee employee = leaveRequest.getEmployee();
        if(employee.getManager() == null || !employee.getManager().getEmpId().equals(managerId)){
            throw new RuntimeException("You are unauthorized to approve this request");
        }
        //5.update a request
        leaveRequest.setStatus(LeaveStatus.APPROVED);
        leaveRequest.setManagerComment(comment);
        leaveRequest.setApprovedBy(manager);
        LeaveRequest savedLeaveRequest = leaveRequestRepository.save(leaveRequest);
        //6.move days from pending-> used in balance
        leaveBalanceService.deductLeave(employee, leaveRequest.getLeaveType(), leaveRequest.getNumberOfDays());
        return convertToDTO(savedLeaveRequest);
    }
    //3. Reject leave request - manager action
    @Transactional
    public LeaveRequestDTO rejectLeave(Long leaveRequestId, Long managerId, String comment){
        //1. fetch request
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId).orElseThrow(()-> new RuntimeException("LeaveRequest not found"));
        //2. Only pending requests can be approved
        if(leaveRequest.getStatus() != LeaveStatus.PENDING){
            throw new RuntimeException("Only pending requests can be rejected. Current leave status is" + leaveRequest.getStatus());
        }
        //3. fetch manager
        Employee manager = employeeRepository.findById(managerId).orElseThrow(()-> new RuntimeException("Manager not found"));
        //4. verify whether manager actually manages the employee
        Employee employee = leaveRequest.getEmployee();
        if(employee.getManager() == null || !employee.getManager().getEmpId().equals(managerId)){
            throw new RuntimeException("You are unauthorized to reject this request");
        }
        //5.update a request
        leaveRequest.setStatus(LeaveStatus.REJECTED);
        leaveRequest.setManagerComment(comment);
        leaveRequest.setApprovedBy(manager);
        LeaveRequest savedLeaveRequest = leaveRequestRepository.save(leaveRequest);
        //6.release pending days back to available
        leaveBalanceService.releasePendingDays(employee, leaveRequest.getLeaveType(), leaveRequest.getNumberOfDays());
        return convertToDTO(savedLeaveRequest);

    }
    //4. Cancel leave - employee action
    @Transactional
    public LeaveRequestDTO cancelLeave(Long leaveRequestId, Long employeeId){
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId).orElseThrow(()-> new RuntimeException("LeaveRequest not found"));
        //employee who applied can only cancel
        if(!leaveRequest.getEmployee().getEmpId().equals(employeeId)){
            throw new RuntimeException("You can only cancel your own leave request");
        }
        //can only cancel pending requests, approved leaves need separate method
        if(leaveRequest.getStatus() != LeaveStatus.PENDING){
            throw new RuntimeException("Only pending requests  can be cancelled. Current leave status is" + leaveRequest.getStatus());
        }
        leaveRequest.setStatus(LeaveStatus.CANCELLED);
        LeaveRequest savedLeaveRequest = leaveRequestRepository.save(leaveRequest);
        //release pending days back
        leaveBalanceService.releasePendingDays(leaveRequest.getEmployee(), leaveRequest.getLeaveType(), leaveRequest.getNumberOfDays());
        return convertToDTO(savedLeaveRequest);
    }
    //5. Get my leaves, employee views their own history
    public List<LeaveRequestDTO> getMyLeaves(Long employeeId){
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new RuntimeException("Employee not found"));
        return leaveRequestRepository.findByEmployee(employee)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    //6.Get pending requests, manager sees team's inbox
    public List<LeaveRequestDTO> getPendingRequestsForManager(Long managerId){
        Employee manager = employeeRepository.findById(managerId).orElseThrow(() -> new RuntimeException("Manager not found"));
        return leaveRequestRepository.findPendingRequestsByManager(manager)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    //7.Get team history - manager sees all requests from team
    public List<LeaveRequestDTO> getTeamLeaveHistory(Long managerId){
        Employee manager = employeeRepository.findById(managerId).orElseThrow(() -> new RuntimeException("Manager not found"));
        return leaveRequestRepository.findAllRequestsByManager(manager)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    //private helper 1. calculate working days between 2 dates, aka leaveDays
    private double calculateLeaveDays(LocalDate startDate, LocalDate endDate){
        double count = 0;
        LocalDate current = startDate;
        while(!current.isAfter(endDate)){
            DayOfWeek day = current.getDayOfWeek();
            if(day!= DayOfWeek.SATURDAY && day!= DayOfWeek.SUNDAY){
                count++;
            }
            current = current.plusDays(1);
        }
        return count;
    }
    //private helper 2.entity to DTO
    //entity to DTO conversion
    private LeaveRequestDTO convertToDTO(LeaveRequest lr) {
        LeaveRequestDTO dto = new LeaveRequestDTO();
        dto.setEmployeeId(lr.getEmployee().getEmpId());
        dto.setEmployeeName(lr.getEmployee().getEmpName());
        dto.setLeaveTypeName(lr.getLeaveType().getLeaveTypeName());
        dto.setStartDate(lr.getStartDate());
        dto.setEndDate(lr.getEndDate());
        dto.setNumberOfDays(lr.getNumberOfDays());
        dto.setLeaveStatus(lr.getStatus());
        dto.setReason(lr.getReason());
        dto.setManagerComment(lr.getManagerComment());
        dto.setApprovedByName(
                lr.getApprovedBy() != null ? lr.getApprovedBy().getEmpName() : null);
        dto.setAppliedAt(lr.getAppliedAt());
        return dto;

    }
}
