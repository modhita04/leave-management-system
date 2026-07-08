package com.modi.leave_management_system.service;

import com.modi.leave_management_system.config.JwtUtil;
import com.modi.leave_management_system.dto.LoginRequest;
import com.modi.leave_management_system.dto.LoginResponse;
import com.modi.leave_management_system.entity.Employee;
import com.modi.leave_management_system.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    public LoginResponse login(LoginRequest loginRequest) {
        //1. find employee by email
        Employee employee = employeeRepository.findByEmpEmail(loginRequest.getEmail()).orElseThrow(()-> new RuntimeException("Invalid email or password"));
        //2. verify password stored against BCrypt hash
        if(!passwordEncoder.matches(loginRequest.getPassword(), employee.getPassword()))
        {
            throw new RuntimeException("Invalid password");
        }
        //3.generate JWT with identity fit in
        String token = jwtUtil.generateToken(employee.getEmpEmail(), employee.getRole().name(), String.valueOf(employee.getEmpId()));
        //4. return token + basic info
        return new LoginResponse(
                token,
                employee.getRole().name(),
                employee.getEmpId(),
                employee.getEmpName()
        );


    }
}
