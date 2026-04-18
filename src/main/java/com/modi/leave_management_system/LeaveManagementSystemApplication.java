package com.modi.leave_management_system;

import com.modi.leave_management_system.entity.LeaveType;
import com.modi.leave_management_system.repository.LeaveTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LeaveManagementSystemApplication implements CommandLineRunner {

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    public static void main(String[] args) {
        SpringApplication.run(LeaveManagementSystemApplication.class, args);
    }

    @Override
    public void run(String... args) {
        LeaveType casual = new LeaveType();
        casual.setLeaveTypeName("casual");
        casual.setDefaultDaysPerYear(12);
        casual.setCarryForwardAllowed(false);

        leaveTypeRepository.save(casual);
        System.out.println("Saved: " + leaveTypeRepository.findAll());
    }
}
