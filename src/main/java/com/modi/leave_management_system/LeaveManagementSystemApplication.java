package com.modi.leave_management_system;

import com.modi.leave_management_system.entity.Employee;
import com.modi.leave_management_system.entity.LeaveType;
import com.modi.leave_management_system.enums.Role;
import com.modi.leave_management_system.repository.EmployeeRepository;
import com.modi.leave_management_system.repository.LeaveTypeRepository;
import com.modi.leave_management_system.service.LeaveBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LeaveManagementSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeaveManagementSystemApplication.class, args);
    }
}