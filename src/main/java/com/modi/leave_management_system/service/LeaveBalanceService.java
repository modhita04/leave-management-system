package com.modi.leave_management_system.service;

import com.modi.leave_management_system.entity.Employee;
import com.modi.leave_management_system.entity.LeaveType;
import com.modi.leave_management_system.repository.EmployeeRepository;
import com.modi.leave_management_system.repository.LeaveBalanceRepository;
import com.modi.leave_management_system.repository.LeaveTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.modi.leave_management_system.entity.LeaveBalance;
import com.modi.leave_management_system.dto.LeaveBalanceDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class LeaveBalanceService {
    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private LeaveTypeRepository leaveTypeRepository;
    //1. Allocate balances to employee at beginning of year
    //creates one leavebalance row per leave type for employee
    public void allocateBalancesForEmployee(Long empId) {
        Employee employee = employeeRepository.findById(empId).orElseThrow(() -> new RuntimeException("Employee not found" + empId));
        int currentYear = LocalDateTime.now().getYear();
        //fetch leave types from db
        List<LeaveType> allLeaveTypes = leaveTypeRepository.findAll();
        for(LeaveType leaveType : allLeaveTypes) {
            //check if balance already exists for employee+type+year
            //to prevent over writing
            boolean alreadyExists = leaveBalanceRepository.findByEmployeeAndLeaveTypeAndYear(employee, leaveType, currentYear).isPresent();
            if (!alreadyExists) {
                LeaveBalance leaveBalance = new LeaveBalance();
                leaveBalance.setEmployee(employee);
                leaveBalance.setLeaveType(leaveType);
                leaveBalance.setYear(currentYear);
                leaveBalance.setTotalDays(leaveType.getDefaultDaysPerYear());
                leaveBalance.setUsedDays(0);
                leaveBalance.setPendingDays(0);
                leaveBalanceRepository.save(leaveBalance);
            }
        }
        }
        // 2. Get all leave balances for employee in current year
        // show all leave balances at once in employee profile page
        public List<LeaveBalanceDTO> getBalancesForEmployee(Long empId) {
              Employee employee = employeeRepository.findById(empId).orElseThrow(() -> new RuntimeException("Employee not found" + empId));
              int currentYear = LocalDateTime.now().getYear();
              List<LeaveBalance> balances = leaveBalanceRepository.findByEmployeeAndYear(employee, currentYear);
              return balances.stream()
                      .map(this::convertToDTO)
                      .collect(Collectors.toList());
        }
        //3. Get available balances while applying for leave
    // only return balances employee has
          public List<LeaveBalanceDTO> getAvailableBalances(Long empId){
        Employee employee = employeeRepository.findById(empId).orElseThrow(() -> new RuntimeException("Employee not found" + empId));
        int currentYear = LocalDateTime.now().getYear();
        return leaveBalanceRepository.
                findAvailableBalances(employee, currentYear)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
          }
          // 4. Deducting days internally whenever leave is approved
          // move days from pendingDays to usedDays
          public void deductLeave(Employee employee, LeaveType leaveType, double days){
               int currentYear = LocalDateTime.now().getYear();
               LeaveBalance leaveBalance = leaveBalanceRepository.findByEmployeeAndLeaveTypeAndYear(employee, leaveType, currentYear)
                       .orElseThrow(()-> new RuntimeException("Balance not found"));
               leaveBalance.setUsedDays(leaveBalance.getUsedDays()+days);
               leaveBalance.setPendingDays(leaveBalance.getPendingDays()-days);
               leaveBalanceRepository.save(leaveBalance);
          }
          //5. Hold days as pending, called when leave is pending for approval
          // these are not added to used days yet, but are deducted from pending days
          public void holdPendingDays(Employee employee, LeaveType leaveType, double days){
               int currentYear = LocalDateTime.now().getYear();
               LeaveBalance leaveBalance = leaveBalanceRepository.findByEmployeeAndLeaveTypeAndYear(employee, leaveType, currentYear)
                       .orElseThrow(() -> new RuntimeException("Balance not found"));
               leaveBalance.setPendingDays(leaveBalance.getPendingDays()-days);
               leaveBalanceRepository.save(leaveBalance);
          }
          // 6. Release pending days if leave is rejected or cancelled - days go back to being available
          public void releasePendingDays(Employee employee, LeaveType leaveType, double days){
        int currentYear = LocalDateTime.now().getYear();
              LeaveBalance leaveBalance = leaveBalanceRepository.findByEmployeeAndLeaveTypeAndYear(employee, leaveType, currentYear)
                      .orElseThrow(() -> new RuntimeException("Balance not found"));
              leaveBalance.setPendingDays(leaveBalance.getPendingDays()-days);

          }
          //entity to DTO conversion
          private LeaveBalanceDTO convertToDTO(LeaveBalance leaveBalance){
              LeaveBalanceDTO dto = new  LeaveBalanceDTO();
              dto.setLeaveBalanceId(leaveBalance.getLeaveBalanceId());
              dto.setLeaveTypeName(leaveBalance.getLeaveType().getLeaveTypeName());
              dto.setYear(leaveBalance.getYear());
              dto.setTotalDays(leaveBalance.getTotalDays());
              dto.setUsedDays(leaveBalance.getUsedDays());
              dto.setPendingDays(leaveBalance.getPendingDays());
              dto.setAvailableDays(leaveBalance.getAvailableDays());
              return dto;
          }
}



