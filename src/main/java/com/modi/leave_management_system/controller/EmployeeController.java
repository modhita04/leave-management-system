package com.modi.leave_management_system.controller;

import com.modi.leave_management_system.entity.Employee;
import com.modi.leave_management_system.repository.EmployeeRepository;
import com.modi.leave_management_system.service.LeaveBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private LeaveBalanceService  leaveBalanceService;
    //POST /api/employee : create new employee and allocate their balances
    @PostMapping
    public ResponseEntity<Employee> addEmployee(@RequestBody Employee employee){
        Employee saved = employeeRepository.save(employee);
        leaveBalanceService.allocateBalancesForEmployee(employee.getEmpId());
        return ResponseEntity.ok(saved);
    }
    //GET /api/employees/{id}
    //fetch a single employee by ID
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployee(@PathVariable Long id){
        Employee employee = employeeRepository.findById(id).orElseThrow(()-> new RuntimeException("Employee not found"));
        return ResponseEntity.ok(employee);
    }
    //GET /api/employees
    //fetch all employees - admin use
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees(){
        return ResponseEntity.ok(employeeRepository.findAll());
    }
}
