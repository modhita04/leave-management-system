package com.modi.leave_management_system.entity;

import jakarta.persistence.*;

import com.modi.leave_management_system.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long empId;
    @Column(nullable = false)
    private String empName;
    @Column(nullable = false, unique = true)
    private String empEmail;
    @Enumerated(EnumType.STRING)
    private Role role; //EMPLOYEE, MANAGER, ADMIN
    @ManyToOne
    @JoinColumn(name="manager_id") //self referncing foreign key, who is the manager to this emp, one manager can have many employees
    private Employee manager;
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<LeaveRequest> leaveRequests; // all the leave requests of employee, one employee has many leave requests
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<LeaveBalance> leaveBalances; // all the balances per leave type, one employee has many leave balances
}
